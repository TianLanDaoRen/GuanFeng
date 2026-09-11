package com.yisiyun.guanfeng.log

import android.content.Context
import com.yisiyun.guanfeng.core.PressureSample
import java.io.File

/**
 * 历史会话读取：让「正式 3 小时窗口」在服务重启后依然连续。
 *
 * 为什么必须有它：前台服务能保住进程，但服务重启、应用更新、或系统回收后
 * START_STICKY 重建，都会让内存里的样本全丢、窗口从零开始攒——
 * 3 小时窗口下这意味着要再等一小时才有可信读数。
 * 把最近一段历史从 CSV 读回来，窗口就能接着算。
 *
 * 两条铁规则：
 *   1. 只回填窗口内的样本（更早的对趋势没有贡献）；
 *   2. **遇到断档就停**——进程没运行的那段时间没有数据，
 *      若把空洞两端的样本接起来做回归，会算出一段根本不存在的趋势。
 *      宁可窗口短一点，也不要跨空洞拟合。
 */
object SessionHistory {

    private const val FILE_PREFIX = "guanfeng_"
    private const val FILE_SUFFIX = ".csv"
    private const val MAX_ROWS_SCANNED = 20_000

    /**
     * 纯解析：CSV 文本 → 样本列表。
     * 列位置由表头决定而不是写死下标，这样将来增删列也不会读错。
     * 坏行直接跳过——不让一行脏数据毁掉整次恢复。
     */
    fun parse(lines: List<String>): List<PressureSample> {
        if (lines.isEmpty()) return emptyList()
        val header = lines.first().split(',')
        val indexOfTimestamp = header.indexOf("timestamp_ms")
        val indexOfPressure = header.indexOf("pressure_hpa")
        val indexOfAccel = header.indexOf("vertical_accel")
        val indexOfSteps = header.indexOf("steps")
        if (indexOfTimestamp < 0 || indexOfPressure < 0) return emptyList()

        val required = maxOf(indexOfTimestamp, indexOfPressure, indexOfAccel, indexOfSteps)
        val result = ArrayList<PressureSample>()
        for (line in lines.drop(1)) {
            if (line.isBlank()) continue
            val cells = line.split(',')
            if (cells.size <= required) continue
            val timestamp = cells[indexOfTimestamp].toLongOrNull() ?: continue
            val pressure = cells[indexOfPressure].toFloatOrNull() ?: continue
            val accel = if (indexOfAccel >= 0) cells[indexOfAccel].toFloatOrNull() ?: 0f else 0f
            val steps = if (indexOfSteps >= 0) cells[indexOfSteps].toIntOrNull() ?: 0 else 0
            result += PressureSample(timestamp, pressure, accel, steps)
        }
        return result
    }

    /**
     * 从末尾往前取，直到超出窗口、或遇到超过 maxGapMs 的断档为止。
     * 返回按时间升序排列（引擎要求有序）。
     */
    fun tailWithoutGaps(
        samples: List<PressureSample>,
        nowMs: Long,
        windowMs: Long,
        maxGapMs: Long,
    ): List<PressureSample> {
        if (samples.isEmpty()) return emptyList()
        val ordered = samples.sortedBy { it.timestampMs }
        val collected = ArrayDeque<PressureSample>()
        var previous: PressureSample? = null
        for (index in ordered.indices.reversed()) {
            val sample = ordered[index]
            if (nowMs - sample.timestampMs > windowMs) break
            val prev = previous
            if (prev != null && prev.timestampMs - sample.timestampMs > maxGapMs) break
            collected.addFirst(sample)
            previous = sample
        }
        return collected.toList()
    }

    /** 从磁盘恢复：按修改时间从新到旧读会话文件，凑满窗口即停。 */
    fun loadRecent(
        context: Context,
        nowMs: Long,
        windowMs: Long,
        maxGapMs: Long,
    ): List<PressureSample> {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        val files = directory.listFiles { file ->
            file.isFile && file.name.startsWith(FILE_PREFIX) && file.name.endsWith(FILE_SUFFIX)
        }?.sortedByDescending { it.lastModified() } ?: return emptyList()

        val collected = ArrayList<PressureSample>()
        for (file in files) {
            val parsed = runCatching { readCapped(file) }.getOrElse { emptyList() }
            collected += parsed
            val oldest = collected.minOfOrNull { it.timestampMs } ?: continue
            if (nowMs - oldest <= windowMs) break
        }
        return tailWithoutGaps(collected, nowMs, windowMs, maxGapMs)
    }

    private fun readCapped(file: File): List<PressureSample> {
        val lines = ArrayList<String>()
        file.bufferedReader().useLines { sequence ->
            for (line in sequence) {
                lines += line
                if (lines.size >= MAX_ROWS_SCANNED) break
            }
        }
        return parse(lines)
    }
}
