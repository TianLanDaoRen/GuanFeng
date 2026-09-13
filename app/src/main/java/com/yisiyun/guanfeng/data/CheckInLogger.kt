package com.yisiyun.guanfeng.data

import android.content.Context
import com.yisiyun.guanfeng.core.CATEGORY_SYMPTOM
import com.yisiyun.guanfeng.core.TrendResult
import com.yisiyun.guanfeng.log.csvNum
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 体感打卡落盘。
 *
 * 这是本应用最个人化的一块：把「此刻的身体感受」与「此刻的气压环境」绑在一条记录里。
 * 单次打卡没有意义，几十天后回看才有——那时能回答「我在什么气压条件下会难受」，
 * 这类关联只有贴着皮肤、长期连续记录的设备才做得到。
 *
 * 与采样日志同样的原则：**按行即时追加**，进程被杀也不丢数据。
 * 存放在与采样日志相同的 external files 目录，方便一次 adb pull 全部取走。
 */
class CheckInLogger(context: Context) {

    private val file: File = File(
        context.getExternalFilesDir(null) ?: context.filesDir,
        "checkins.csv",
    )

    val path: String get() = file.absolutePath

    val displayName: String get() = file.name

    init {
        runCatching {
            file.parentFile?.mkdirs()
            if (!file.exists()) file.writeText(HEADER + "\n")
        }
    }

    @Synchronized
    fun append(
        timestampMs: Long,
        tags: String,
        intensity: String,
        note: String,
        pressureHpa: Float?,
        trend: TrendResult?,
        heartRateBpm: Float?,
        wristTemperatureC: Float?,
        lightLux: Float?,
        /** 解耦掉高度后的天气分量气压：关联图上的点要落在它上面，否则电梯后的打卡点位会错乱。 */
        weatherPressureHpa: Float? = null,
        /**
         * 类别（[CATEGORY_SYMPTOM] / [CATEGORY_COMFORT]）——两级选择里的第一级。
         * 附加在**最后一列**：这样旧文件里没有这一列的行仍然按位置解析得上，
         * 读出来为空即按"症状"处理（那时这一页只记不适）。
         */
        category: String = CATEGORY_SYMPTOM,
    ): Boolean = runCatching {
        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(timestampMs))
        val line = listOf(
            timestampMs.toString(),
            dateTime,
            csvNum(pressureHpa, 2),
            csvNum(trend?.deltaHpaPer3h, 2),
            csvNum(trend?.rateHpaPerHour, 3),
            csvNum(trend?.elevationMeters, 1),
            csvNum(heartRateBpm, 0),
            csvNum(wristTemperatureC, 1),
            csvNum(lightLux, 0),
            tags,
            intensity,
            // 逗号是分隔符，正文里的半角逗号换成全角，避免破坏列结构。
            note.replace(',', '，'),
            csvNum(weatherPressureHpa, 2),
            category,
        ).joinToString(",")
        file.appendText(line + "\n")
        true
    }.getOrElse { false }

    /** 今日打卡次数：直接用日期前缀数行，不为此引入数据库。 */
    fun countToday(): Int = runCatching {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        file.readLines().drop(1).count { line ->
            line.split(",").getOrNull(1)?.startsWith(today) == true
        }
    }.getOrElse { 0 }

    private companion object {
        const val HEADER =
            "timestamp_ms,datetime,pressure_hpa,delta_hpa_3h,rate_hpa_per_hour," +
                "elevation_meters,heart_rate_bpm,wrist_temp_c,light_lux,tags,intensity,note," +
                "weather_pressure_hpa,category"
    }
}
