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

    /**
     * 生成紧凑 JSON（手写而非 org.json：内核保持纯 Kotlin，才能跑 JVM 单测）。
     *
     * 三段口径：
     *   · `all_history`——全部可用历史（来自小时归档，不受原始文件裁剪影响）
     *   · `recent_7_days`——近 7 天（含每日气压极值明细）
     *   · `body_context`——体感与环境：心率、腕温、光照的**小时级统计与明细**
     *
     * 为什么要带体感：主人的判断是对的——头痛未必来自气压，也可能是发热（腕温升高）。
     * 只给气压，AI 就没法排除混淆因素，只能在气压里硬找关联。
     * 带上的都是**抗离群的中位数与极值**，不是瞬时值：袖子上翻一次就能让瞬时腕温跳好几度。
     */
    fun build(
        allHistory: AssociationSummary,
        recent: AssociationSummary,
        checkIns: List<CheckInRecord>,
        zoneOffsetMs: Long,
        hourlyRows: List<HourlyRow> = emptyList(),
        recentFromMs: Long = 0L,
    ): String {
        val tagCounts = checkIns.groupingBy { it.tags }.eachCount()
        val intensityCounts = checkIns.groupingBy { it.intensity }.eachCount()

        val builder = StringBuilder()
        builder.append('{')
        builder.append("\"all_history\":").append(scopeObject(allHistory, zoneOffsetMs, includeDaily = false))
        builder.append(',')
        builder.append("\"recent_7_days\":")
            .append(scopeObject(recent, zoneOffsetMs, includeDaily = true))
        builder.append(',')
        builder.append("\"check_in_tags\":").append(countObject(tagCounts))
        builder.append(',')
        builder.append("\"check_in_intensity\":").append(countObject(intensityCounts))
        // 逐条打卡明细（含手写备注原文）。
        // 曾经这里刻意不发备注，理由是"保护隐私"——但那是错的判断：
        // 标签只能归成六类，而"起床后右侧发紧"这种手写描述才包含可分析的细节，
        // 挡掉它等于让 AI 失去最有用的一路输入。是否上传由用户在同意弹窗里决定。
        if (checkIns.isNotEmpty()) {
            builder.append(",\"check_in_detail\":[")
            builder.append(
                checkIns.joinToString(",") { record ->
                    buildString {
                        append("{\"t\":").append(record.timestampMs)
                        append(",\"tag\":\"").append(escape(record.tags)).append('"')
                        append(",\"intensity\":\"").append(escape(record.intensity)).append('"')
                        record.chartPressureHpa?.let { append(",\"pressure\":%.1f".format(it)) }
                        if (record.note.isNotBlank()) {
                            append(",\"note\":\"").append(escape(record.note)).append('"')
                        }
                        append('}')
                    }
                }
            )
            builder.append(']')
        }
        if (hourlyRows.isNotEmpty()) {
            builder.append(',')
            builder.append("\"body_context\":").append(bodyContext(hourlyRows, recentFromMs))
        }
        builder.append('}')
        return builder.toString()
    }

    /**
     * 体感与环境：两段概要 + 近期的**小时级明细**。
     *
     * 明细是有意给的：AI 最擅长的就是从序列里找模式，把小时级序列交给它，
     * 比我们自己先下结论更有价值。代价很小——一周 168 行、约 7 KB。
     */
    private fun bodyContext(rows: List<HourlyRow>, recentFromMs: Long): String {
        val recent = rows.filter { it.hourStartMs >= recentFromMs }
        val builder = StringBuilder()
        builder.append('{')
        builder.append(
            "\"caveat\":\"腕温与光照受环境与佩戴影响很大（衣袖遮挡、空调房、洗手都会造成离群），" +
                "故一律给中位数与极值，不给瞬时值；心率中不区分静息与活动时须结合静息基线看\""
        )
        builder.append(",\"all_history\":").append(bodySummaryJson(BodyStats.summarize(rows)))
        builder.append(",\"recent_7_days\":").append(bodySummaryJson(BodyStats.summarize(recent)))
        builder.append(",\"hourly_series_7d\":[")
        builder.append(
            recent.joinToString(",") { row ->
                buildString {
                    append("{\"t\":").append(row.hourStartMs)
                    append(",\"p\":%.1f".format(row.weatherAvgHpa))
                    append(",\"pmin\":%.1f".format(row.weatherMinHpa))
                    append(",\"pmax\":%.1f".format(row.weatherMaxHpa))
                    row.heartRateAvg?.let { append(",\"hr\":%.0f".format(it)) }
                    row.restingHeartRate?.let { append(",\"rhr\":%.0f".format(it)) }
                    row.wristTempAvg?.let { append(",\"wt\":%.2f".format(it)) }
                    row.wristTempMin?.let { append(",\"wtmin\":%.2f".format(it)) }
                    row.lightAvgLux?.let { append(",\"lux\":%.0f".format(it)) }
                    append('}')
                }
            }
        )
        builder.append(']')
        builder.append('}')
        return builder.toString()
    }

    private fun bodySummaryJson(summary: BodySummary): String = buildString {
        append('{')
        append("\"hourly_rows\":").append(summary.hourlyRows)
        summary.restingHeartRate?.let { append(",\"resting_hr_bpm\":").append(statJson(it)) }
        summary.heartRate?.let { append(",\"heart_rate_bpm\":").append(statJson(it)) }
        summary.wristTemperature?.let { append(",\"wrist_temp_c\":").append(statJson(it)) }
        summary.light?.let { append(",\"light_lux\":").append(statJson(it)) }
        append('}')
    }

    private fun statJson(stat: StatSummary): String =
        "{\"median\":%.2f,\"min\":%.2f,\"max\":%.2f,\"hours\":%d}"
            .format(stat.median, stat.min, stat.max, stat.count)

    private fun scopeObject(
        summary: AssociationSummary,
        zoneOffsetMs: Long,
        includeDaily: Boolean,
    ): String {
        val builder = StringBuilder()
        builder.append('{')
        builder.append("\"days_with_data\":").append(summary.daysWithData).append(',')
        builder.append("\"big_swing_days\":").append(summary.bigSwingDays).append(',')
        builder.append("\"big_swing_threshold_hpa\":").append(AssociationAnalyzer.BIG_SWING_HPA).append(',')
        builder.append("\"big_swing_dates\":").append(stringArray(summary.bigSwingDayLabels)).append(',')
        builder.append("\"check_in_count\":").append(summary.checkInCount).append(',')
        builder.append("\"check_in_on_big_swing_days\":").append(summary.checkInsOnBigSwingDays)
        if (includeDaily) {
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
            builder.append(",\"daily_pressure_hpa\":[").append(
                dailyPressure.joinToString(",") { day ->
                    "{\"date\":\"${escape(day.label)}\",\"min\":%.1f,\"max\":%.1f}"
                        .format(day.minHpa, day.maxHpa)
                }
            ).append(']')
        }
        builder.append('}')
        return builder.toString()
    }

    /**
     * 系统指令：约束 + 输出形态 + 两段口径。
     *
     * 第 1 条最重要——样本量在统计上不足以支撑任何医学结论，模型必须被明确禁止越界；
     * 第 4 条是给手表屏幕的：表格与代码块在 189dp 宽上根本没法看，从源头禁掉。
     * 第 5 条要求分段作答，因为主人明确要「既看全局也看近期」。
     */
    fun buildSystemInstruction(): String = """
        你是数据分析助手。用户会给你三部分聚合统计：
        `all_history`（全部可用历史）、`recent_7_days`（近 7 天）、
        `body_context`（心率/腕温/光照的小时级统计与明细），
        以及 `check_in_detail`（他手写的身体感受）。
        这些数据不含身份信息，也没有逐点原始读数。

        【判断纪律】
        1. 只做描述性分析，措辞限于「数据显示…可能有关联…建议继续观察」；
           禁止任何医学诊断、病因推断、用药或治疗建议。
        2. 必须明确指出样本量很小、结论不可靠。样本少的时候不要用百分比或
           术语制造确定性；宁可说"现在还看不出来"，也不要硬给一个像结论的说法。
        3. `check_in_detail` 里的 `note` 是用户最直接的自我描述，把它当作主要文本证据，
           标签只是归类。`body_context` 用来**排除混淆因素**：例如头痛若同时伴随腕温升高，
           发热就是不能排除的解释；若心跳与体温都正常，则气压变化的解释相对更强。
           提到体感数据时要说明其局限（腕温受环境与衣袖影响）。
        4. 不要使用表格、代码块或长列表——阅读终端是一块很小的手表屏幕。
           需要分段就用短段落。

        【篇幅】
        不限制字数。需要多长就写多长，**不要为了简短牺牲准确性**，
        但也不要为了凑长度重复已经说过的话。信息密度比字数重要。

        【语气】
        温和、体谅，同时保持专业。把自己当成一位细心而不打扰的观察者，
        而不是一台播报机器：
        · 用平静的语气陈述，不夸张、不吓人、不说教；
        · 判断该给就给，但把话说得让人愿意听——"看起来"比"肯定"更诚实，也更舒服；
        · 不用命令式的"你应该""必须"，可以换成"如果方便的话，不妨…"；
        · 不堆专业术语；非用不可时，顺带用一句人话解释它。
    """.trimIndent()

    /** 用户内容：只带聚合统计。 */
    fun buildUserContent(digestJson: String): String = "统计（JSON）：\n$digestJson"

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
