package com.yisiyun.guanfeng.core

/**
 * 不适打卡记录（纯数据）。
 * 与气压桶一起喂给 [AssociationAnalyzer]，得出「气压 × 不适」的观察性小结。
 */
data class CheckInRecord(
    val timestampMs: Long,
    val tags: String,
    val intensity: String,
    val pressureHpa: Float?,
    val note: String,
    /** 解耦掉高度后的天气分量气压：绘制打卡点优先用它，避免电梯后的打卡点位错乱。 */
    val weatherPressureHpa: Float? = null,
) {
    /** 画图与统计都用这个：优先天气分量，旧记录退回首列的气压。 */
    val chartPressureHpa: Float? get() = weatherPressureHpa ?: pressureHpa
}

/**
 * 气压 × 不适的观察性小结。
 *
 * ## 必须说清的口径（这段是本文件存在的意义）
 *
 * 一周只有 7 天、打卡常常只有几次，**这个样本量在统计上不足以证明任何关联**。
 * 所以本类只做两件事：
 *   1. 描述事实：这几天里气压变化大的有几天、打卡几次、其中几次落在那些日子；
 *   2. 明确标注样本量，让读的人自己判断可信度。
 * 它**不计算相关系数**，也不给因果判断——那需要几十天以上的数据与降水真值，
 * 目前都没有。UI 上也必须把这句话显示出来。
 */
data class AssociationSummary(
    val periodDays: Int,
    val daysWithData: Int,
    /** 日气压落差 ≥ [BIG_SWING_HPA] 的天数。 */
    val bigSwingDays: Int,
    val checkInCount: Int,
    /** 落在「大变化日」上的打卡数。 */
    val checkInsOnBigSwingDays: Int,
    /** 小时均值序列（画曲线用），按小时升序。 */
    val hourly: List<HourlyBucket>,
    /** 每个大变化日的日期标签，用于展示。 */
    val bigSwingDayLabels: List<String>,
) {
    /** 打卡落在气压大变化日的比例；没有打卡时返回 null（不编造 0%）。 */
    val overlapRatio: Float?
        get() = if (checkInCount == 0) null else checkInsOnBigSwingDays.toFloat() / checkInCount
}

object AssociationAnalyzer {

    /** 一天之内气压落差达到这个值，就算「气压大变化日」。取 3 hPa：约等于一次明显锋面过境。 */
    const val BIG_SWING_HPA = 3.0f

    private const val DAY_MS = 24L * 60L * 60L * 1000L

    /**
     * @param zoneOffsetMs 本地时区偏移（例如东八区 = 8h）。日界按本地时间切，
     *   否则「今天」会在凌晨 8 点换日——这是容易被忽略但很影响观感的细节。
     */
    fun analyze(
        hourly: List<HourlyBucket>,
        checkIns: List<CheckInRecord>,
        periodDays: Int,
        zoneOffsetMs: Long,
    ): AssociationSummary {
        val sorted = hourly.sortedBy { it.hourStartMs }
        val byDay = sorted.groupBy { dayStartOf(it.hourStartMs, zoneOffsetMs) }
        val bigSwingDayStarts = byDay.entries
            .filter { (_, buckets) ->
                val max = buckets.maxOf { it.maxHpa }
                val min = buckets.minOf { it.minHpa }
                max - min >= BIG_SWING_HPA
            }
            .map { it.key }
            .sorted()
        val bigSwingSet = bigSwingDayStarts.toHashSet()

        val checkInsOnBigSwingDays = checkIns.count { record ->
            bigSwingSet.contains(dayStartOf(record.timestampMs, zoneOffsetMs))
        }

        return AssociationSummary(
            periodDays = periodDays,
            daysWithData = byDay.size,
            bigSwingDays = bigSwingDayStarts.size,
            checkInCount = checkIns.size,
            checkInsOnBigSwingDays = checkInsOnBigSwingDays,
            hourly = sorted,
            bigSwingDayLabels = bigSwingDayStarts.map { dayLabel(it, zoneOffsetMs) },
        )
    }

    fun dayStartOf(timestampMs: Long, zoneOffsetMs: Long): Long {
        val shifted = timestampMs + zoneOffsetMs
        return shifted - Math.floorMod(shifted, DAY_MS) - zoneOffsetMs
    }

    private fun dayLabel(dayStartMs: Long, zoneOffsetMs: Long): String {
        val shifted = dayStartMs + zoneOffsetMs
        val days = (shifted / DAY_MS).toInt()
        // 由天数反推月日（不引 java.time，内核保持纯 Kotlin 以便 JVM 单测）
        var year = 1970
        var remaining = days
        while (true) {
            val yearDays = if (isLeap(year)) 366 else 365
            if (remaining < yearDays) break
            remaining -= yearDays
            year++
        }
        val lengths = intArrayOf(
            31, if (isLeap(year)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        )
        var month = 0
        while (month < 12 && remaining >= lengths[month]) {
            remaining -= lengths[month]
            month++
        }
        return "%d/%d".format(month + 1, remaining + 1)
    }

    private fun isLeap(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
}
