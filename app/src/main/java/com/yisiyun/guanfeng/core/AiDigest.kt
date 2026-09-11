package com.yisiyun.guanfeng.core

/**
 * AI 上报摘要的构造。
 *
 * ## 隐私边界（这是本文件存在的前提）
 *
 * 只构造**聚合统计**，绝不包含逐条原始记录、不做任何身份标识：
 *   · 有：周期天数、有效天数、气压大变化日数与日期、打卡次数与标签分布、
 *         每日气压极值（用于让模型看清"哪几天气压在动"）
 *   · 没有：逐条打卡时间戳、备注原文、心率/腕温等体征逐点数据、任何设备或用户标识
 *
 * 上传与否由用户在界面上显式决定；本文件只负责"如果要传，传什么"。
 */
object AiDigest {

    /** 生成紧凑 JSON（手写而非 org.json：内核保持纯 Kotlin，才能跑 JVM 单测）。 */
    fun build(
        summary: AssociationSummary,
        checkIns: List<CheckInRecord>,
        zoneOffsetMs: Long,
    ): String {
        val tagCounts = checkIns.groupingBy { it.tags }.eachCount()
        val intensityCounts = checkIns.groupingBy { it.intensity }.eachCount()

        val dailyPressure = summary.hourly
            .groupBy { AssociationAnalyzer.dayStartOf(it.hourStartMs, zoneOffsetMs) }
            .toSortedMap()
            .map { (dayStart, buckets) ->
                DailyPressure(
                    label = dayLabel(dayStart, zoneOffsetMs),
                    minHpa = buckets.minOf { it.minHpa },
                    maxHpa = buckets.maxOf { it.maxHpa },
                )
            }

        val builder = StringBuilder()
        builder.append('{')
        builder.append("\"period_days\":").append(summary.periodDays).append(',')
        builder.append("\"days_with_data\":").append(summary.daysWithData).append(',')
        builder.append("\"big_swing_days\":").append(summary.bigSwingDays).append(',')
        builder.append("\"big_swing_threshold_hpa\":").append(AssociationAnalyzer.BIG_SWING_HPA).append(',')
        builder.append("\"big_swing_dates\":").append(stringArray(summary.bigSwingDayLabels)).append(',')
        builder.append("\"check_in_count\":").append(summary.checkInCount).append(',')
        builder.append("\"check_in_on_big_swing_days\":").append(summary.checkInsOnBigSwingDays).append(',')
        builder.append("\"check_in_tags\":").append(countObject(tagCounts)).append(',')
        builder.append("\"check_in_intensity\":").append(countObject(intensityCounts)).append(',')
        builder.append("\"daily_pressure_hpa\":[").append(
            dailyPressure.joinToString(",") { day ->
                "{\"date\":\"${escape(day.label)}\",\"min\":%.1f,\"max\":%.1f}".format(day.minHpa, day.maxHpa)
            }
        ).append(']')
        builder.append('}')
        return builder.toString()
    }

    /**
     * 提示词。三条硬约束缺一不可——尤其是第 1 条：
     * 一周 7 天、几次打卡在统计上不足以支撑任何医学结论，模型必须被明确禁止越界。
     */
    fun buildPrompt(digestJson: String, periodDays: Int): String = """
        你是数据分析助手。下面是一位用户最近 $periodDays 天的「气压」与「自报不适」的聚合统计。
        这些数据不含任何逐条原始记录，也没有身份信息。

        请严格遵守以下约束：
        1. 只做描述性分析，措辞限于「数据显示…可能有关联…建议继续观察」；
           禁止任何医学诊断、病因推断、用药或治疗建议。
        2. 必须明确指出样本量很小、结论不可靠，不要用百分比或术语制造确定性。
        3. 中文，200 字以内，分三段：数据概况 / 可能存在的关联 / 一条可执行的观察建议。

        统计（JSON）：
        $digestJson
    """.trimIndent()

    private data class DailyPressure(val label: String, val minHpa: Float, val maxHpa: Float)

    private fun stringArray(values: List<String>): String =
        "[" + values.joinToString(",") { "\"${escape(it)}\"" } + "]"

    private fun countObject(counts: Map<String, Int>): String =
        "{" + counts.entries.joinToString(",") { "\"${escape(it.key)}\":${it.value}" } + "}"

    /** JSON 字符串转义：中文与常见符号原样保留，只处理必须转义的控制字符与引号。 */
    private fun escape(raw: String): String {
        val builder = StringBuilder(raw.length + 8)
        raw.forEach { ch ->
            when (ch) {
                '"' -> builder.append("\\\"")
                '\\' -> builder.append("\\\\")
                '\n' -> builder.append("\\n")
                '\r' -> builder.append("\\r")
                '\t' -> builder.append("\\t")
                else -> if (ch < ' ') builder.append("\\u%04x".format(ch.code)) else builder.append(ch)
            }
        }
        return builder.toString()
    }

    /** 与 AssociationAnalyzer 内部保持一致的日期标签（月/日）。 */
    private fun dayLabel(dayStartMs: Long, zoneOffsetMs: Long): String {
        val dayMs = 24L * 60L * 60L * 1000L
        val shifted = dayStartMs + zoneOffsetMs
        val days = (shifted / dayMs).toInt()
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
