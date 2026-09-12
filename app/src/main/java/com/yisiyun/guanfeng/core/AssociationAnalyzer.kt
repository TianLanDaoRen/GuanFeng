package com.yisiyun.guanfeng.core

/**
 * 体感打卡记录（纯数据）。
 * 与气压桶一起喂给 [AssociationAnalyzer]，得出「气压 × 体感」的观察性小结。
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
 * 气压 × 体感的观察性小结。
 *
 * ## 注意：这里的「打卡」指的是**体感症状那些次**
 *
 * 标签表 `SYMPTOM_TAGS` 全是症状（头痛/头晕/颈肩/关节/疲劳/睡眠差），
 * 所以「落在气压大变化日的 N 次」这句话的分母是**症状事件**，才有解释力。
 * 若将来加上「今天感觉不错」这类正常状态标签，这个统计口径就失效了——
 * 正常状态本来每天都该记，落在哪天都合理，比值会趋近于天数比例。
 * 要加这种标签，必须同时把统计改成"对照组 vs 症状组"。
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

    /*
     * ## 关于"夜里没数据"——这是**已裁决的设计**，不要再加"日可评估"门槛
     *
     * 手表进睡眠模式会停掉应用（见 README 第十一节），所以每天必然缺掉夜里那几个小时，
     * 于是"当天的气压极差"实际是在**白天+傍晚已观测到的小时**上算的。
     * 我一度提议：一天不足 16 小时就判为"不可评估"、整体剔出统计。
     * 主人 2026-09-11 否掉了，理由是：
     *
     * > 大变化日不应该改变吧。毕竟白天的时候也很常见 >=3hPa 的。
     * > 夜里发生了什么我们并不关心。只要标准是一致的，就不会有问题。
     *
     * 这个判断是对的，而且是领域上的对：**要研究的现象发生在白天**——
     * 人在白天清醒、有不适才会打卡，夜里那一段本来就不产生打卡点。
     * 缺夜不是"缺测量"，而是**范围本就划在白天**。加门槛反而会把
     * "白天确实变了 3 hPa、只是夜里没记录"的正常日子判成不可评估。
     *
     * 唯一残留的、需要读者心里有数的一点：判据是统一的，但**每天实际观测到的小时数不统一**
     * （睡得早有 6 小时、睡得晚有 9 小时）。记录特别短的那几天，"不是大变化日"的证据相对弱。
     * 这一点如实记在 README 里，不做代码处理——为它加机制，就把一句"仅供参考"变成了
     * 一个会被当成结论的数字。
     */

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
