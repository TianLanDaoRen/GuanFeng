package com.yisiyun.guanfeng.log

import android.content.Context
import java.io.File

/**
 * 长期归档体检。
 *
 * ## 为什么要做这件事
 *
 * 小时归档（`hourly.csv`）是整个应用里**唯一永不删除**的数据：原始采样文件只留
 * 最近 30 MB（约 15 天）就会被紧凑化裁掉，而长期趋势、关联视图、AI 报告全靠归档。
 * 它一旦悄悄停止增长，后果是**几周后才发现**——那时原始样本早被裁掉，那段历史
 * 就再也补不回来了（2026-09-12 就踩过一次：睡眠模式把应用停掉，导致每晚少一个整点，
 * 而当时的备份路径一个都没盖住它）。
 *
 * 所以每次打开记录页时做一次体检，把三件事摆出来：
 *   1. **近段缺口**：原始文件里有的整点，归档里却缺了几个（修补之后仍然缺）
 *   2. **归档覆盖**：最早/最新覆盖到哪个整点、共几小时（这决定"能回看多久"）
 *   3. **原始文件体积**：离 30 MB 上限还有多远（离得越近，越依赖归档的完整性）
 *
 * ## 判定口径（两处容易搞错的地方）
 *
 * - **当前这个小时不算缺口**：归档只记**已完成**的小时，正在走的这一小时本来就还没落盘。
 * - **只比"解耦口径"的整点**：归档存的是 `weather_pressure_hpa`（去掉高度分量）的均值，
 *   所以对比用的也是只接受带该列的整点，两边才是同一把尺子。
 */
data class ArchiveHealth(
    /** 归档覆盖的整点数（去重后）。 */
    val archiveHours: Int,
    /** 同一整点被落盘多次的行数（应用重启会把当前小时再写一次，读取时按整点去重）。 */
    val duplicateArchiveHours: Int,
    /** 归档最早覆盖的整点。 */
    val earliestArchiveHourMs: Long?,
    /** 归档最新覆盖的整点。 */
    val latestArchiveHourMs: Long?,
    /** 近段窗口内、原始文件覆盖到的整点数（不含当前未走完的小时）。 */
    val recentHourCount: Int,
    /** 修补之后**仍然缺**的整点（升序）。空 = 健康。 */
    val missingRecentHours: List<Long>,
    /** 原始采样文件体积。 */
    val sampleBytes: Long,
    /** 原始采样文件的体积上限（紧凑化阈值）。 */
    val sampleLimitBytes: Long,
    /** 原始文件第一条样本的时刻：它后面才是归档独有覆盖的那一段。 */
    val sampleEarliestMs: Long?,
) {
    val healthy: Boolean get() = missingRecentHours.isEmpty()

    /** 原始文件占上限的比例（0..1）。 */
    val sampleUsage: Float
        get() = if (sampleLimitBytes <= 0L) 0f else (sampleBytes.toFloat() / sampleLimitBytes).coerceIn(0f, 1f)
}

/**
 * 纯判定：把"归档有哪些整点"与"原始文件近段覆盖了哪些整点"对一遍。
 *
 * 不碰 Context、不开文件——这样"当前这一小时不该算缺口"这类口径能被单测钉死。
 */
internal fun evaluateArchiveHealth(
    archiveHours: Collection<Long>,
    sampleRecentHours: Collection<Long>,
    nowMs: Long,
    sampleBytes: Long,
    sampleLimitBytes: Long,
    sampleEarliestMs: Long?,
): ArchiveHealth {
    val hourMs = 60L * 60L * 1000L
    val currentHour = nowMs / hourMs * hourMs
    val uniqueArchive = archiveHours.toSet()
    // 当前小时没走完，归档里没有它是正常的
    val counted = sampleRecentHours.filter { it < currentHour }.toSet()
    return ArchiveHealth(
        archiveHours = uniqueArchive.size,
        duplicateArchiveHours = (archiveHours.size - uniqueArchive.size).coerceAtLeast(0),
        earliestArchiveHourMs = uniqueArchive.minOrNull(),
        latestArchiveHourMs = uniqueArchive.maxOrNull(),
        recentHourCount = counted.size,
        missingRecentHours = counted.filter { it !in uniqueArchive }.sorted(),
        sampleBytes = sampleBytes,
        sampleLimitBytes = sampleLimitBytes,
        sampleEarliestMs = sampleEarliestMs,
    )
}

/**
 * 读盘体检（磁盘 I/O，调用方放到 IO 线程上）。
 *
 * 代价可控：归档本身很小（约 1 KB/天），原始文件只用尾部窗口读一次
 * （见 [SessionHistory.loadTailHourlyBuckets]），不扫整份。
 */
fun loadArchiveHealth(
    context: Context,
    nowMs: Long,
    recentWindowMs: Long,
): ArchiveHealth {
    val archiveHours = HourlyArchive.loadAll(context).map { it.hourStartMs }
    val recentHours = runCatching {
        SessionHistory.loadTailHourlyBuckets(
            context = context,
            nowMs = nowMs,
            withinMs = recentWindowMs,
        ).map { it.hourStartMs }
    }.getOrDefault(emptyList())
    val sample = SessionHistory.sampleFile(context)
    return evaluateArchiveHealth(
        archiveHours = archiveHours,
        sampleRecentHours = recentHours,
        nowMs = nowMs,
        sampleBytes = runCatching { sample.length() }.getOrDefault(0L),
        sampleLimitBytes = CsvSessionLogger.MAX_BYTES,
        sampleEarliestMs = firstSampleTimestampMs(sample),
    )
}

/**
 * 原始文件**第一条数据行**的时间戳：文件是时序追加的，读两行就够，
 * 不必为了"最早保留到什么时候"扫整份 30MB。
 */
private fun firstSampleTimestampMs(file: File): Long? = runCatching {
    if (!file.isFile) return null
    file.bufferedReader().use { reader ->
        reader.readLine()
        reader.readLine()?.substringBefore(',')?.toLongOrNull()
    }
}.getOrNull()
