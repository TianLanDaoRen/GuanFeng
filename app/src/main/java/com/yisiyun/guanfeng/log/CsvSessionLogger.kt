package com.yisiyun.guanfeng.log

import android.content.Context
import com.yisiyun.guanfeng.core.TrendResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 采样日志：把每个采样点连同当时的趋势结论落成 CSV。
 *
 * 为什么必须落盘：真机测试时手表戴在手腕上会断开 USB，而 logcat 是内存环形缓冲，
 * 几分钟就被冲掉——只有写文件才能事后复盘。
 *
 * 位置取 getExternalFilesDir(null)，即 /sdcard/Android/data/<包名>/files/，
 * 可以直接 adb pull 取走，不需要 run-as，也不受分区存储限制。
 *
 * ## 单一文件 + 体积上限（主人的设计）
 *
 * 原先是"每次启动一个新文件"，实测 11 小时产生 14 个（重启就新开），
 * 碎片化且难以回看。现在改为**唯一文件 + 只追加**：
 *   · 只有一个 `guanfeng_samples.csv`，跨会话连续；
 *   · 追加是 O(1)，不会为了删旧行而每次重写整个文件；
 *   · 超过 [MAX_BYTES] 时做一次**紧凑化**，丢掉最旧的一段、保留最新的 [KEEP_BYTES]。
 *
 * 关于"append 前 pop 最远记录"：如果每追加一行都去删最旧的一行，
 * 每次都变成 O(文件大小) 的重写，手表上根本扛不住（30MB 文件一天会被重写一万次）。
 * 所以实现为"超限时紧凑一次"——效果相同（有上限、丢最旧），代价从每天上万次重写降为几周一次。
 */
class CsvSessionLogger(context: Context) {

    private val directory: File
    private val file: File

    /** 已写入的数据行数（不含表头）。跨会话累加（初始化时从现有文件数一遍）。 */
    var rowCount: Int = 0
        private set

    /** 本文件累计丢弃的行数，便于在记录页判断"数据被裁过"。 */
    var droppedRows: Int = 0
        private set

    val path: String get() = file.absolutePath

    val displayName: String get() = FILE_NAME

    val sizeBytes: Long get() = runCatching { file.length() }.getOrDefault(0L)

    init {
        directory = context.getExternalFilesDir(null) ?: context.filesDir
        file = File(directory, FILE_NAME)
        runCatching {
            directory.mkdirs()
            if (!file.exists()) file.writeText(HEADER + "\n")
            rowCount = file.readLines().count { it.isNotBlank() } - 1
        }
    }

    /** 越界就紧凑化：只保留最新的一段，旧行丢掉。 */
    @Synchronized
    private fun compactIfNeeded() {
        val size = runCatching { file.length() }.getOrDefault(0L)
        if (size <= MAX_BYTES) return
        runCatching {
            val bytesToKeep = KEEP_BYTES.toInt()
            val startAt = (size - bytesToKeep).coerceAtLeast(0L)
            val temp = File(directory, FILE_NAME + ".tmp")
            file.inputStream().use { input ->
                input.channel.use { channel ->
                    channel.position(startAt)
                    temp.outputStream().use { output ->
                        output.write((HEADER + "\n").toByteArray())
                        val buffer = ByteArray(64 * 1024)
                        var skippedFirst = false
                        while (true) {
                            val read = channel.read(java.nio.ByteBuffer.wrap(buffer))
                            if (read <= 0) break
                            var offset = 0
                            if (!skippedFirst) {
                                // 丢掉可能被切断的半行，避免写出畸形记录
                                offset = 0
                                while (offset < read && buffer[offset] != '\n'.code.toByte()) offset++
                                if (offset < read) offset++ else continue
                                skippedFirst = true
                            }
                            output.write(buffer, offset, read - offset)
                        }
                    }
                }
            }
            val removed = rowCount - temp.readLines().count { it.isNotBlank() } + 1
            if (file.delete() && temp.renameTo(file)) {
                droppedRows += removed.coerceAtLeast(0)
                rowCount = file.readLines().count { it.isNotBlank() } - 1
            } else {
                temp.delete()
            }
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
        // 每 200 行才查一次体积：查 length() 是系统调用，不必每次追加都问
        if (rowCount % 200 == 0) compactIfNeeded()
        true
    }.getOrElse { false }

    companion object {
        /** 唯一文件名：跨会话连续，不再碎片化。 */
        const val FILE_NAME = "guanfeng_samples.csv"

        /** 文件体积上限 30 MB（主人定的）。5 秒采样约 2 MB/天，即约 15 天滚动窗口。 */
        const val MAX_BYTES = 30L * 1024L * 1024L

        /** 紧凑化后保留的字节数：留一点余量，避免刚紧凑完又立刻超限。 */
        const val KEEP_BYTES = 25L * 1024L * 1024L

        const val HEADER =
            "timestamp_ms,clock,pressure_hpa,vertical_accel,steps,rate_hpa_per_hour," +
                "delta_hpa_3h,grade,weather_samples,elevation_events,elevation_meters," +
                "r_squared,window_minutes,coverage_pct,confidence," +
                "resting_heart_rate_bpm,light_delta_10min,weather_pressure_hpa,light_lux"
    }
}
