package com.yisiyun.guanfeng.core

/**
 * 小时级的**长期归档行**。
 *
 * 与 [HourlyBucket]（临时折叠用）的区别：这一行是**要落盘长期保存**的，
 * 因此除气压外还带上了体感与环境的分量——AI 报告需要它们来判断混淆因素
 * （例如头痛也可能来自发热，而不是气压）。
 *
 * 所有数值都取中位数/均值这类**抗离群**的统计量：腕温尤其如此，
 * 袖子翻上去、进一趟空调房，瞬时值就会跳好几度，逐点数据本身没有意义。
 */
data class HourlyRow(
    val hourStartMs: Long,
    val weatherAvgHpa: Float,
    val weatherMinHpa: Float,
    val weatherMaxHpa: Float,
    val rawAvgHpa: Float,
    val samples: Int,
    /** 该小时内的平均心率；没有心率数据时为 null（不要用 0 冒充）。 */
    val heartRateAvg: Float?,
    val restingHeartRate: Float?,
    val wristTempAvg: Float?,
    val wristTempMin: Float?,
    val wristTempMax: Float?,
    val lightAvgLux: Float?,
    val lightMinLux: Float?,
)

/**
 * 小时归档的累加器（纯逻辑，可单测）。
 *
 * 由采集器在每来一个样本时喂入，整点切换时把上一小时落盘。
 * 这里**不做任何跨小时的插值或补点**：没数据的时段就是没有行，
 * 让下游如实看到空洞，而不是被补出来的假连续性骗过去。
 */
class HourAccumulator {

    /**
     * 体感量逐次留存，最后取**中位数**而不是均值。
     *
     * 这不是洁癖：袖子翻上去、进一趟空调房、洗个手，腕温瞬时就能跳 3℃，
     * 一次这样的读数足以把整小时的均值拉偏一度多（实测 33.0/36.2/33.2 → 均值 34.13，
     * 而中位数是 33.2）。归档是要长期保存、要喂给 AI 的，被离群点污染的均值会一路传下去。
     *
     * 容量可控：累加器只在**每 5 秒的聚合样本**上被喂一次，一小时最多 720 个值。
     */
    private val hrValues = ArrayList<Float>(720)
    private val restingValues = ArrayList<Float>(720)
    private val tempValues = ArrayList<Float>(720)
    private val lightValues = ArrayList<Float>(720)

    private var hourStartMs: Long = -1
    private var weatherSum = 0.0
    private var weatherMin = Float.MAX_VALUE
    private var weatherMax = -Float.MAX_VALUE
    private var rawSum = 0.0
    private var samples = 0

    /**
     * 喂入一个样本。
     *
     * 返回值：整点切换时返回**被滚动掉的上一小时**（调用方负责落盘），否则为 null。
     * 由 add 自己负责"先取快照再重置"，避免调用方忘记顺序而丢掉整整一小时的数据。
     */
    fun add(
        timestampMs: Long,
        weatherHpa: Float,
        rawHpa: Float,
        heartRateBpm: Float?,
        restingHeartRateBpm: Float?,
        wristTempC: Float?,
        lightLux: Float?,
    ): HourlyRow? {
        val hour = HourlyAccumulator.floorToHour(timestampMs)
        var completed: HourlyRow? = null
        if (hourStartMs == -1L) {
            hourStartMs = hour
        } else if (hour != hourStartMs) {
            completed = snapshot()
            reset(hour)
        }

        weatherSum += weatherHpa
        if (weatherHpa < weatherMin) weatherMin = weatherHpa
        if (weatherHpa > weatherMax) weatherMax = weatherHpa
        rawSum += rawHpa
        samples++

        heartRateBpm?.let { hrValues += it }
        restingHeartRateBpm?.let { restingValues += it }
        wristTempC?.let { tempValues += it }
        lightLux?.let { lightValues += it }
        return completed
    }

    /** 取出当前小时的归档行；还没有任何样本时返回 null。 */
    fun snapshot(): HourlyRow? {
        if (samples == 0) return null
        return HourlyRow(
            hourStartMs = hourStartMs,
            weatherAvgHpa = (weatherSum / samples).toFloat(),
            weatherMinHpa = weatherMin,
            weatherMaxHpa = weatherMax,
            rawAvgHpa = (rawSum / samples).toFloat(),
            samples = samples,
            heartRateAvg = BodyStats.median(hrValues),
            restingHeartRate = BodyStats.median(restingValues),
            wristTempAvg = BodyStats.median(tempValues),
            wristTempMin = tempValues.minOrNull(),
            wristTempMax = tempValues.maxOrNull(),
            lightAvgLux = BodyStats.median(lightValues),
            lightMinLux = lightValues.minOrNull(),
        )
    }

    fun reset(hourStart: Long) {
        hourStartMs = hourStart
        weatherSum = 0.0
        weatherMin = Float.MAX_VALUE
        weatherMax = -Float.MAX_VALUE
        rawSum = 0.0
        samples = 0
        hrValues.clear()
        restingValues.clear()
        tempValues.clear()
        lightValues.clear()
    }
}

/** [backfillCarriedOver] 的结果。 */
data class BackfillResult(
    /** 实际补进累加器的样本条数（0 表示没有可补的）。 */
    val fedSamples: Int,
    /**
     * 补齐过程中被滚动出来的完整小时行。
     *
     * 按构造**正常情况下一定是空的**（补进来的样本全都属于当前小时）。
     * 之所以还要把它交回调用方，是因为"理论上不可能"正是往年丢数据的常见借口——
     * 万一以后有人改了过滤条件，这一行至少会被落盘，而不是无声消失。
     */
    val completedRows: List<HourlyRow>,
)

/**
 * 启动时的归档补齐：把"本次启动前、且属于 [currentHourStartMs] 这个小时"的样本
 * 先喂进累加器，让整点滚动出的那一行把重启前的那段也算进去。
 *
 * ## 为什么必须有它（真机实证）
 *
 * 累加器活在内存里，进程一重启就清零，于是重启前那一小段**从归档里永久消失**。
 * 实测：22:14 重启之后，`hourly.csv` 里 22:00 那行只有 **545/720** 个样本，
 * 缺的正是 22:00–22:14 这 14 分钟（满勤 720 = 60 分钟 × 12 个/分钟）。
 * 归档是长期趋势与 AI 报告的唯一数据源，缺一段**不会报错**，只会悄悄改变结论。
 *
 * ## 两个刻意的取舍
 *
 * 1. **天气分量用 `原始气压 − 累计高度偏移` 还原**，而不是从 CSV 读 `weather_pressure_hpa`。
 *    因为恢复样本用的 [PressureSample] 根本不带那一列；而高度偏移是 5 分钟落一次盘的，
 *    最多陈旧 5 分钟，且只在检出竖直位移时才变，对"一小时的平均气压"足够。
 * 2. **体感两列留空**（瞬时心率、腕温在原始 CSV 里没有）。宁可让这一小时的心率/腕温
 *    由重启后的样本决定，也不拿 0 或旧值冒充——"看起来有数据"比"没有数据"更危险。
 *
 * 抽成纯函数（而不是留在采集器里）是为了能在 JVM 上钉住：
 * 这是"重启就丢一段"的唯一防线，而"我写了但没人调用"是真实发生过的失败模式。
 *
 * @return 补进去的条数，以及（正常为空的）被滚动出来的整点行。
 */
fun HourAccumulator.backfillCarriedOver(
    samples: List<PressureSample>,
    currentHourStartMs: Long,
    elevationOffsetHpa: Float,
): BackfillResult {
    val carried = samples.filter { it.timestampMs >= currentHourStartMs }
    val completed = ArrayList<HourlyRow>(0)
    carried.forEach { sample ->
        add(
            timestampMs = sample.timestampMs,
            weatherHpa = sample.pressureHpa - elevationOffsetHpa,
            rawHpa = sample.pressureHpa,
            heartRateBpm = null,
            restingHeartRateBpm = null,
            wristTempC = null,
            lightLux = null,
        )?.let { completed += it }
    }
    return BackfillResult(fedSamples = carried.size, completedRows = completed)
}

/**
 * 一个整点至少要凑到这么多个样本才值得单独成行（60 个 = 5 分钟）。
 * 启动/停机都可能切出几秒钟的碎片，为它们写一整行只会污染长期归档。
 */
const val MIN_REPAIR_SAMPLES = 60

/**
 * 从 [buckets] 里挑出**归档里整点缺失**的那些小时，用于补写。
 *
 * ## 为什么需要它（2026-09-12 真机实证）
 *
 * 睡眠模式在 **02:59** 把应用整个停掉，而 02:00 这个整点还差一分钟才到点，
 * 于是它在 `hourly.csv` 里**整行消失**——而 `guanfeng_samples.csv` 里那 59 分钟完整存在。
 * 既有的 `backfillCarriedOver` 补不到它：那个只覆盖"当前小时"，
 * 而这次重启已经过去 5.23 小时，且 `loadRecent` 的「断档 > 5 分钟即停」
 * 把恢复样本清空了，`backfillCarriedOver` 拿到的是空列表。
 *
 * 结果是**每晚都会丢一个整点**（睡着的那个小时），一个月就是三十个小时，
 * 而归档是"永不删除"的长期存储——丢了就真没了（样本文件只留约两周）。
 *
 * ## 刻意划的三条边界
 *
 * 1. **只补整点缺失的，不碰已存在的行**。已有的行是实时累加器写的，带完整体感数据；
 *    而补写的行只有气压（原始 CSV 里没有那一刻的心率/腕温）。用补写的行去替换已有的行
 *    是**降级**，不是修复。已存在但不满勤的行（如被重启切掉一段）保持原样，如实留疤。
 * 2. **不补当前小时**。它归实时累加器 + `backfillCarriedOver` 管；
 *    在这里补会让每个整点出现两行（去重虽能兜住，但没必要制造重复）。
 * 3. **只在 [earliestHourStartMs] 之后补**。太久远的缺失没有修复价值，
 *    而扫描范围本身也受样本文件尾部长度限制。
 */
fun missingHourBuckets(
    existingHourStarts: Set<Long>,
    buckets: List<HourlyBucket>,
    currentHourStartMs: Long,
    earliestHourStartMs: Long,
    minSamples: Int = MIN_REPAIR_SAMPLES,
): List<HourlyBucket> = buckets.filter { bucket ->
    bucket.hourStartMs < currentHourStartMs &&
        bucket.hourStartMs >= earliestHourStartMs &&
        bucket.sampleCount >= minSamples &&
        bucket.hourStartMs !in existingHourStarts
}
