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
    /**
     * 类别：[CATEGORY_SYMPTOM] 或 [CATEGORY_COMFORT]。
     *
     * 默认症状：旧文件没有这一列，而那时这一页只记不适。
     */
    val category: String = CATEGORY_SYMPTOM,
) {
    /** 对照组（舒适）。统计要把它从症状组里分出来。 */
    val isComfort: Boolean get() = category == CATEGORY_COMFORT

    /** 画图与统计都用这个：优先天气分量，旧记录退回首列的气压。 */
    val chartPressureHpa: Float? get() = weatherPressureHpa ?: pressureHpa
}

/**
 * 打卡的两个类别。**这是两级选择里的第一级**（2026-09-12 主人定）。
 *
 * 为什么必须是独立的一维、而不是"再加一个叫舒适的标签"：
 * 自定义文本没法归类——用户写"起床后右侧发紧"是症状，写"今天挺舒服"是对照，
 * 而两者在文件里长得一模一样。把它们混在一个标签维度里，分母就被污染了。
 * 单独立一维之后，类别由用户显式给出，其余字段自由。
 *
 * 兼容：旧记录没有这一列 → 一律按 [CATEGORY_SYMPTOM] 读
 * （那时这一页就叫"不适打卡"，标签表也全是症状，可以断定）。
 * 唯一一条已知的例外是主人手工判定后改过的那条，见 README 第十一节。
 */
const val CATEGORY_SYMPTOM = "symptom"
const val CATEGORY_COMFORT = "comfort"

/**
 * 气压 × 体感的观察性小结。
 *
 * ## ⚠️ 口径警告（2026-09-12 起生效，尚未实现）
 *
 * 本类原先的注释写着：「标签表全是症状，所以『落在气压大变化日的 N 次』的分母
 * 是症状事件，才有解释力；**若将来加上『今天感觉不错』这类正常状态标签，
 * 这个统计口径就失效了**」。
 *
 * 主人 2026-09-12 加了「[COMFORT_TAG]」这个标签——**这个前提现在已经被打破了**：
 * `checkInCount` 取的是全部打卡，一旦「舒适」的打卡混进去，
 * 分母就不是症状事件而是"所有记录"，比值会被稀释、朝"没有关联"偏。
 *
 * 正确做法是拆成两组：症状组算原来的比例，「舒适」组单独作**对照组**
 * （对照组的落点比例本来就该接近天数比例——两组差异才是值得看的东西）。
 * 这一步还没做，等的是一句话的确认：**「舒适」要不要参与统计，还是只作记录。**
 * 在改好之前，不要在报告里把这个比值当结论用。
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
    /** 「气压剧烈变化日」的天数：当天出现过 ≥ [BIG_CHANGE_HPA] 的 3 小时变压。 */
    val bigSwingDays: Int,
    val checkInCount: Int,
    /** 落在「大变化日」上的打卡数。 */
    val checkInsOnBigSwingDays: Int,
    /** 「舒适」打卡总数（对照组）。 */
    val comfortCount: Int,
    /** 「舒适」打卡里落在气压大变化日的次数。 */
    val comfortOnBigSwingDays: Int,
    /** 小时均值序列（画曲线用），按小时升序。 */
    val hourly: List<HourlyBucket>,
    /** 每个大变化日的日期标签，用于展示。 */
    val bigSwingDayLabels: List<String>,
) {
    /** 不适打卡落在气压大变化日的比例；没有打卡时返回 null（不编造 0%）。 */
    val overlapRatio: Float?
        get() = if (checkInCount == 0) null else checkInsOnBigSwingDays.toFloat() / checkInCount

    /**
     * 舒适打卡落在变化日的比例——**对照组的基准线**。
     *
     * 这个数才是让上面那个比例有意义的东西：如果适组和对照组差不多，
     * 说明打卡落点跟气压没关系；对照组明显更低才有话可说。
     */
    val comfortRatio: Float?
        get() = if (comfortCount == 0) null else comfortOnBigSwingDays.toFloat() / comfortCount
}

object AssociationAnalyzer {

    /**
     * 「气压剧烈变化日」的判据：**这一天之内出现过 ≥1.5 hPa 的 3 小时变压**。
     *
     * ## 为什么从"整日极差 ≥3 hPa"改成这个（2026-09-14 主人指出）
     *
     * 旧判据是"这一天所有小时里 max−min ≥ 3"，有两个致命毛病：
     *
     * 1. **0 点一刀切**：一次 3.93 hPa 的摆动横跨 09-12 与 09-13 两天，
     *    按日历日切完，09-12 分到 3.49（算）、09-13 只分到 2.49（不算）——
     *    **同一次摆动两天命运不同**。主人原话："过了 24 点，前面的就不看了是吧？"
     * 2. **名不副实**：实测 09-11~09-14 四天，最大 3 小时变压**从未超过 1.52 hPa**，
     *    而"整日极差 3.49"是十几个小时**慢慢累积**出来的。旧判据实际测的是
     *    "这一天气压慢悠悠走了个大来回"，**不是"变化剧烈"**。
     *
     * 新判据用气象标准（3 小时变压），并与观风自己的报警门限**取同一个数：1.5**（`WeatherEpisodeTracker.enterDropHpa`）。
     *    主人 2026-09-14 纠正过我一次：真正的报警门限是 1.5，不是 2.0
     *    —— 2.0 是 WeatherRule 里[中度倾向]的门限，那是另一个量。。
     * 滚动窗口天然跨天鲁棒；归属规则：窗口**结束**在哪天就算哪天。
     *
     * 按新判据这四天**一天都不算** —— 这才是诚实的结论：那几天只是正常的云层与风的波动
     * （主人原话）。
     */
    const val BIG_CHANGE_HPA = 3.0f

    /** 3 小时变压的窗口（气象惯例）。 */
    const val BIG_CHANGE_WINDOW_HOURS = 3

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
    /**
     * 这一天里是否出现过 ≥[BIG_CHANGE_HPA] 的 [BIG_CHANGE_WINDOW_HOURS] 小时变压。
     *
     * 用**小时均值**而不是小时极值：极值会被单点毛刺带偏，均值才是气象上"变压"的口径。
     * 比较的是**已观测到的相邻小时**（夜里进睡眠模式会缺几个小时）——
     * 所以"3 小时"是"最多 3 个已观测小时"，跨度可能略长于 3 小时。这一点如实记在这里，
     * 与上面"夜里没数据"那段裁决同一个理由：范围本就划在白天。
     */
    internal fun dayHasBigChange(buckets: List<HourlyBucket>): Boolean {
        val sorted = buckets.sortedBy { it.hourStartMs }
        for (i in sorted.indices) {
            for (j in maxOf(0, i - BIG_CHANGE_WINDOW_HOURS) until i) {
                if (kotlin.math.abs(sorted[i].avgHpa - sorted[j].avgHpa) >= BIG_CHANGE_HPA) {
                    return true
                }
            }
        }
        return false
    }

    fun analyze(
        hourly: List<HourlyBucket>,
        checkIns: List<CheckInRecord>,
        periodDays: Int,
        zoneOffsetMs: Long,
    ): AssociationSummary {
        val sorted = hourly.sortedBy { it.hourStartMs }
        val byDay = sorted.groupBy { dayStartOf(it.hourStartMs, zoneOffsetMs) }
        val bigSwingDayStarts = byDay.entries
            .filter { (_, buckets) -> dayHasBigChange(buckets) }
            .map { it.key }
            .sorted()
        val bigSwingSet = bigSwingDayStarts.toHashSet()

        // 两组分开算：症状组才是"事件"，对照组只是基准线。
        // 合并成一个比值等于把"今天挺舒服"也算成不适——那正是加这一维要解决的事。
        fun onBigSwing(records: List<CheckInRecord>) = records.count { record ->
            bigSwingSet.contains(dayStartOf(record.timestampMs, zoneOffsetMs))
        }
        val symptoms = checkIns.filter { !it.isComfort }
        val comforts = checkIns.filter { it.isComfort }

        return AssociationSummary(
            periodDays = periodDays,
            daysWithData = byDay.size,
            bigSwingDays = bigSwingDayStarts.size,
            checkInCount = symptoms.size,
            checkInsOnBigSwingDays = onBigSwing(symptoms),
            comfortCount = comforts.size,
            comfortOnBigSwingDays = onBigSwing(comforts),
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
