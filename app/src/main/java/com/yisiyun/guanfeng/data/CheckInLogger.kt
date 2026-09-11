package com.yisiyun.guanfeng.data

import android.content.Context
import com.yisiyun.guanfeng.core.TrendResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 不适打卡落盘。
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
    ): Boolean = runCatching {
        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(timestampMs))
        val line = listOf(
            timestampMs.toString(),
            dateTime,
            pressureHpa?.let { "%.2f".format(it) } ?: "",
            trend?.deltaHpaPer3h?.let { "%.2f".format(it) } ?: "",
            trend?.rateHpaPerHour?.let { "%.3f".format(it) } ?: "",
            trend?.elevationMeters?.let { "%.1f".format(it) } ?: "",
            heartRateBpm?.let { "%.0f".format(it) } ?: "",
            wristTemperatureC?.let { "%.1f".format(it) } ?: "",
            lightLux?.let { "%.0f".format(it) } ?: "",
            tags,
            intensity,
            // 逗号是分隔符，正文里的半角逗号换成全角，避免破坏列结构。
            note.replace(',', '，'),
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
                "elevation_meters,heart_rate_bpm,wrist_temp_c,light_lux,tags,intensity,note"
    }
}
