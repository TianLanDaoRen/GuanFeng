package com.yisiyun.guanfeng.log

import android.content.Context
import com.yisiyun.guanfeng.core.TrendResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 会话日志：把每个采样点连同当时的趋势结论落成 CSV。
 *
 * 为什么必须落盘：真机测试时手表戴在手腕上会断开 USB，而 logcat 是内存环形缓冲，
 * 几分钟就被冲掉——只有写文件才能事后复盘。
 *
 * 位置取 getExternalFilesDir(null)，即 /sdcard/Android/data/<包名>/files/，
 * 可以直接 adb pull 取走，不需要 run-as，也不受分区存储限制。
 *
 * 每次应用启动生成一个带时间戳的新文件，多次测试互不混淆。
 */
class CsvSessionLogger(context: Context) {

    private val file: File

    /** 已写入的数据行数（不含表头）。 */
    var rowCount: Int = 0
        private set

    val path: String get() = file.absolutePath

    val displayName: String get() = file.name

    init {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        file = File(directory, "guanfeng_$stamp.csv")
        runCatching {
            directory.mkdirs()
            file.writeText(HEADER + "\n")
        }
    }

    @Synchronized
    fun append(
        timestampMs: Long,
        pressureHpa: Float,
        verticalAccel: Float,
        stepsInWindow: Int,
        trend: TrendResult?,
        restingHeartRateBpm: Float? = null,
        lightDelta10Min: Float? = null,
        /** 解耦掉高度之后的「天气分量」气压；长视图与打卡快照都用它，不能用原始读数。 */
        weatherPressureHpa: Float? = null,
        lightLux: Float? = null,
    ): Boolean = runCatching {
        val clock = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(timestampMs))
        val line = buildString {
            append(timestampMs).append(',')
            append(clock).append(',')
            append("%.2f".format(pressureHpa)).append(',')
            append("%.2f".format(verticalAccel)).append(',')
            append(stepsInWindow).append(',')
            append("%.3f".format(trend?.rateHpaPerHour ?: 0f)).append(',')
            append("%.3f".format(trend?.deltaHpaPer3h ?: 0f)).append(',')
            append(trend?.grade?.label ?: "等样本").append(',')
            append(trend?.weatherSamples ?: 0).append(',')
            append(trend?.elevationEvents ?: 0).append(',')
            append("%.2f".format(trend?.elevationMeters ?: 0f)).append(',')
            append("%.3f".format(trend?.fitRSquared ?: 0f)).append(',')
            append("%.2f".format(trend?.windowMinutes ?: 0f)).append(',')
            append("%.0f".format((trend?.coverageFraction ?: 0f) * 100f)).append(',')
            append(trend?.confidence?.label ?: "数据不足").append(',')
            append(restingHeartRateBpm?.let { "%.0f".format(it) } ?: "").append(',')
            append(lightDelta10Min?.let { "%.0f".format(it) } ?: "").append(',')
            append(weatherPressureHpa?.let { "%.2f".format(it) } ?: "").append(',')
            append(lightLux?.let { "%.0f".format(it) } ?: "")
        }
        file.appendText(line + "\n")
        rowCount++
        true
    }.getOrElse { false }

    private companion object {
        const val HEADER =
            "timestamp_ms,clock,pressure_hpa,vertical_accel,steps,rate_hpa_per_hour," +
                "delta_hpa_3h,grade,weather_samples,elevation_events,elevation_meters," +
                "r_squared,window_minutes,coverage_pct,confidence," +
                "resting_heart_rate_bpm,light_delta_10min,weather_pressure_hpa,light_lux"
    }
}
