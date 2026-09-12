package com.yisiyun.guanfeng.data

import android.content.Context
import android.util.Log
import com.yisiyun.guanfeng.log.QweatherLogger

/**
 * 天气采集的编排：**同意 → 坐标 → 取数 → 落盘**。
 *
 * ## 它不做什么
 *
 * **不参与任何判定**。采集回来的数据只写进 CSV，一个字段都不喂给气压趋势、
 * 高度解耦或天气过程状态机。这是刻意的：观风的判据必须只依赖手表自己的传感器，
 * 否则它就从"仪器"退化成了"转发天气预报的壳子"。外部数据的角色只有一个——
 * **事后校准的参照物**。
 *
 * ## 时序上的两条纪律
 *
 * 1. **绝不阻塞调用方**。采集是从传感器主循环里异步派生出来的：主循环每 5 秒
 *    转一圈，而这里最坏要等 20 秒的网络超时。直接在里面发请求会让传感器回调堆积。
 * 2. **失败不做重试风暴**。取数失败就等下一个 30 分钟周期。手表流量与电量都金贵，
 *    而且和风对错误请求过多是会冻结帐号的。
 */
object WeatherCollector {

    private const val TAG = "WeatherCollector"

    /** 采集周期。主人定 30 分钟：够密（一天 48 条），又远低于和风免费额度（月耗 2.9%）。 */
    const val INTERVAL_MS = 30L * 60L * 1000L

    /**
     * 失败后的重试间隔。
     *
     * 为什么不是"失败就等满 30 分钟"：最常见的失败是**用户还没授权定位**，
     * 那是个一次性动作——他授权之后不该再等半小时才开始采。
     * 为什么也不是每 5 秒重试：那是重试风暴，而且和风对错误请求过多会冻结帐号。
     * 5 分钟是这两者之间的合理点：故障恢复够快，日志也不会被刷屏。
     */
    private const val RETRY_INTERVAL_MS = 5L * 60L * 1000L

    /**
     * 每个采集周期里静默刷新位置的预算。
     *
     * 与设置流程共用同一个 30 秒：主人实测下来"星况好时 30 秒够了，不好再等也没用"。
     * 这个数字改小还有个额外好处——GPS 开机时长直接决定耗电，30 秒 × 每 30 分钟
     * 约等于每小时 60 秒 GPS 开机，比 100 秒那版省七成。
     */
    private const val SILENT_REFRESH_MS = SiteLocation.GPS_TIMEOUT_MS

    private const val PREFS = "guanfeng_weather"
    private const val KEY_LAST_AT = "last_fetch_at"
    private const val KEY_LAST_NOTE = "last_fetch_note"
    private const val KEY_LAST_OK = "last_fetch_ok"

    /** 采集结果，供界面显示"上次什么时候采的、成没成"。 */
    data class Outcome(
        val ok: Boolean,
        val note: String,
        val nowRows: Int,
        val hourlyRows: Int,
    )

    /** 上次取数时间（0 = 从未）。 */
    fun lastFetchAt(context: Context): Long =
        prefs(context).getLong(KEY_LAST_AT, 0L)

    /** 上次取数的结果说明。 */
    fun lastNote(context: Context): String =
        prefs(context).getString(KEY_LAST_NOTE, "").orEmpty()

    /**
     * 该不该采了。成功了等满 30 分钟，失败了只等 [RETRY_INTERVAL_MS]。
     * 主循环用它决定要不要派生采集。
     */
    fun isDue(context: Context, nowMs: Long): Boolean {
        val p = prefs(context)
        if (!p.contains(KEY_LAST_AT)) return true
        val interval = if (p.getBoolean(KEY_LAST_OK, false)) INTERVAL_MS else RETRY_INTERVAL_MS
        return nowMs - p.getLong(KEY_LAST_AT, 0L) >= interval
    }

    /**
     * 执行一次采集。**会阻塞当前协程**（最坏约 20 秒），
     * 调用方必须把它放在独立协程里，不要放在传感器的采样循环里。
     */
    suspend fun collect(context: Context, nowMs: Long = System.currentTimeMillis()): Outcome {
        // 所有提前返回都走 finish()：**无论成败都要记下这次尝试的时间**。
        // 否则 isDue 会永远为真，主循环每 5 秒就派生一次采集——这正是我在 KDoc 里
        // 写"失败不做重试风暴"要防的事，而第一版代码漏了这条（真机日志里被抓到）。
        if (!WeatherConsent.isGranted(context)) {
            return finish(context, nowMs, Outcome(false, "未同意联网", 0, 0))
        }
        if (!QweatherClient.isConfigured) {
            return finish(
                context, nowMs,
                Outcome(false, "未配置代理密钥（local.properties 缺 qweather.gateKey）", 0, 0),
            )
        }

        // 取坐标：**先静默刷新一次（30 秒），失败才回退到历史坐标**（主人定的策略）。
        // 与设置流程里那次"拿到才继续"不同：第一次必须确知在哪，之后只需保持新鲜，
        // 所以这里绝不因为刷新失败就放弃采集。
        val fix = SiteLocation.refreshOrRemember(context, gpsTimeoutMs = SILENT_REFRESH_MS)
            ?: return finish(
                context, nowMs,
                Outcome(false, "取不到坐标（没有定位权限且无历史坐标）", 0, 0),
            )

        var nowRows = 0
        var hourlyRows = 0
        val problems = mutableListOf<String>()

        // 实时：一次一行，是真正的时间序列
        val now = QweatherClient.fetchNow(fix.lat, fix.lon)
        if (now.data != null) {
            val wrote = QweatherLogger.append(
                context = context,
                fileName = QweatherLogger.NOW_FILE,
                header = QweatherLogger.NOW_HEADER,
                row = QweatherLogger.formatNowRow(
                    timestampMs = nowMs,
                    lat = fix.lat,
                    lon = fix.lon,
                    now = now.data,
                    httpMs = now.elapsedMs,
                    locationSource = fix.source,
                ),
            )
            if (wrote) nowRows = 1 else problems += "实时数据写盘失败"
        } else {
            problems += "实时取数失败：${now.error}"
        }

        // 逐小时：一次 24 行，是预报快照。降水量、概率、风级、湿度都在这里。
        val hourly = QweatherClient.fetchHourly(fix.lat, fix.lon)
        if (hourly.data != null) {
            val rows = hourly.data.map { QweatherLogger.formatHourRow(nowMs, fix.lat, fix.lon, it) }
            val wrote = QweatherLogger.appendRows(
                context = context,
                fileName = QweatherLogger.HOURLY_FILE,
                header = QweatherLogger.HOURLY_HEADER,
                rows = rows,
            )
            if (wrote) hourlyRows = rows.size else problems += "逐小时数据写盘失败"
        } else {
            problems += "逐小时取数失败：${hourly.error}"
        }

        val ok = nowRows > 0 || hourlyRows > 0
        val note = if (ok) {
            "${fix.source} · 实时 ${nowRows} 行 · 逐小时 ${hourlyRows} 行" +
                problems.joinToString("；", prefix = if (problems.isEmpty()) "" else "；")
        } else {
            problems.joinToString("；")
        }
        Log.i(TAG, "采集完成：$note")
        return finish(context, nowMs, Outcome(ok, note, nowRows, hourlyRows))
    }

    /** 统一出口：记录本次尝试时刻与结果。下一个周期才会再试，不会连打。 */
    private fun finish(context: Context, nowMs: Long, outcome: Outcome): Outcome {
        prefs(context).edit()
            .putLong(KEY_LAST_AT, nowMs)
            .putString(KEY_LAST_NOTE, outcome.note)
            .putBoolean(KEY_LAST_OK, outcome.ok)
            .apply()
        return outcome
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
