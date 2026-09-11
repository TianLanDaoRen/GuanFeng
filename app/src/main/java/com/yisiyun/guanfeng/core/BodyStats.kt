package com.yisiyun.guanfeng.core

/** 一个量的抗离群概要。 */
data class StatSummary(val median: Float, val min: Float, val max: Float, val count: Int)

/**
 * 归档行的体感/环境概要。
 *
 * ## 为什么用中位数而不是平均值
 *
 * 腕温这类量的离群太常见：袖子翻上去、进一趟空调房、洗手，瞬时值就能跳好几度。
 * 平均值会被这些点拖走，中位数不会。所以**上报给 AI 的一律是中位数 + 极值**，
 * 而不是"平均温度"——后者看着平稳，其实可能已经被几个离群点带偏。
 *
 * 极值仍然保留：它能告诉 AI"这段时间有过 31.9℃ 的低点"，
 * 那是判断"是否只是环境变化"的线索，丢掉可惜。
 */
data class BodySummary(
    val hourlyRows: Int,
    val restingHeartRate: StatSummary?,
    val heartRate: StatSummary?,
    val wristTemperature: StatSummary?,
    val light: StatSummary?,
)

object BodyStats {

    fun median(values: List<Float>): Float? {
        if (values.isEmpty()) return null
        val sorted = values.sorted()
        val middle = sorted.size / 2
        return if (sorted.size % 2 == 1) {
            sorted[middle]
        } else {
            (sorted[middle - 1] + sorted[middle]) / 2f
        }
    }

    fun summarize(rows: List<HourlyRow>): BodySummary = BodySummary(
        hourlyRows = rows.size,
        restingHeartRate = stat(rows.mapNotNull { it.restingHeartRate }),
        heartRate = stat(rows.mapNotNull { it.heartRateAvg }),
        wristTemperature = stat(rows.mapNotNull { it.wristTempAvg }),
        light = stat(rows.mapNotNull { it.lightAvgLux }),
    )

    private fun stat(values: List<Float>): StatSummary? {
        if (values.isEmpty()) return null
        return StatSummary(
            median = median(values)!!,
            min = values.min(),
            max = values.max(),
            count = values.size,
        )
    }
}
