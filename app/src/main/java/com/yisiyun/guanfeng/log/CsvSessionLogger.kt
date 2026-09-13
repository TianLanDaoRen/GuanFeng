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
            rowCount = countDataRows(file)
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
            val removed = rowCount - countDataRows(temp)
            if (file.delete() && temp.renameTo(file)) {
                droppedRows += removed.coerceAtLeast(0)
                rowCount = countDataRows(file)
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
        /** 竖直净位移（米）：高度分类器判"人是否真的在垂直运动"靠它。 */
        verticalDisplacementM: Float? = null,
    ): Boolean = runCatching {
        val clock = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(timestampMs))
        val line = buildString {
            append(timestampMs).append(',')
            append(clock).append(',')
            append(csvNum(pressureHpa, 2)).append(',')
            append(csvNum(verticalAccel, 2)).append(',')
            append(stepsInWindow).append(',')
            append(csvNum(trend?.rateHpaPerHour ?: 0f, 3)).append(',')
            append(csvNum(trend?.deltaHpaPer3h ?: 0f, 3)).append(',')
            append(trend?.grade?.label ?: "等样本").append(',')
            append(trend?.weatherSamples ?: 0).append(',')
            append(trend?.elevationEvents ?: 0).append(',')
            append(csvNum(trend?.elevationMeters ?: 0f, 2)).append(',')
            append(csvNum(trend?.fitRSquared ?: 0f, 3)).append(',')
            append(csvNum(trend?.windowMinutes ?: 0f, 2)).append(',')
            append(csvNum((trend?.coverageFraction ?: 0f) * 100f, 0)).append(',')
            append(trend?.confidence?.label ?: "数据不足").append(',')
            append(csvNum(restingHeartRateBpm, 0)).append(',')
            append(csvNum(lightDelta10Min, 0)).append(',')
            append(csvNum(weatherPressureHpa, 2)).append(',')
            append(csvNum(lightLux, 0)).append(',')
            append(csvNum(verticalDisplacementM, 2))
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
                "resting_heart_rate_bpm,light_delta_10min,weather_pressure_hpa,light_lux," +
                "vertical_displacement_m"
    }
}

/**
 * 数数据行（不含表头）。顶层函数是为了能被单测直接调用——行数算错会让
 * "紧凑化丢掉了多少行"跟着错。
 *
 * **不要用 `readLines()`**：文件上限 30MB，那会把它整份拉成二十多万个 `String`——
 * 在手表上是几十 MB 的瞬时分配，而且正好落在应用启动（init）与每次紧凑化
 * （那里还要连数两遍）这两条路径上。数换行符只占一个 64KB 缓冲，结果一样。
 *
 * 每行都以 `\n` 结尾（见 [CsvSessionLogger.append]），所以行数 = 换行数，减去表头那一行。
 * 万一上次写到一半被杀（末行没有换行），末行补算一行。
 */
internal fun countDataRows(target: File): Int = runCatching {
    if (!target.isFile) return 0
    var newlines = 0L
    var lastByte = -1
    target.inputStream().use { input ->
        val buffer = ByteArray(64 * 1024)
        while (true) {
            val read = input.read(buffer)
            if (read <= 0) break
            for (i in 0 until read) {
                if (buffer[i] == '\n'.code.toByte()) newlines++
            }
            lastByte = buffer[read - 1].toInt()
        }
    }
    var lines = newlines
    if (lastByte != -1 && lastByte != '\n'.code) lines++
    (lines - 1).coerceAtLeast(0).toInt()
}.getOrDefault(0)
