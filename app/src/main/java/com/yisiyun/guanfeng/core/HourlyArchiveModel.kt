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
