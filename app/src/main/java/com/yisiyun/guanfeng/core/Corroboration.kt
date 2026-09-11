package com.yisiyun.guanfeng.core

enum class CorroborationKind { LIGHT_DROP, HEART_RATE_UP, WRIST_TEMP_SHIFT }

/**
 * 一条体感/环境佐证。
 *
 * [canUpgradeLikelihood] 是关键区分：**只有物理因果清楚的那一条才允许升档**，
 * 其余只上屏提示，不参与判定。
 */
data class Corroboration(
    val kind: CorroborationKind,
    val label: String,
    val canUpgradeLikelihood: Boolean,
    val note: String,
)

/**
 * 体感与环境佐证。
 *
 * ## 权重是怎么定的（这一节比代码重要）
 *
 * 主人要求把心率、腕温、光照一起纳入风雨预测。查了资料之后，**只有一条站得住**，
 * 另两条我按「不参与判定、只作提示」处理，理由如下：
 *
 * 1. **光照骤降——站得住，允许升档。**
 *    云层增厚会显著削弱日光，资料里明确把「白天光照突然跌破阈值（如 500 lux）」
 *    当作云量增加的迹象。这是物理因果（云厚 → 光弱 → 降水接近），不是相关性猜测。
 *    注意腕上的光传感器会被衣袖与室内环境主导，所以**不用绝对阈值，只用相对跌幅**，
 *    并要求基准亮度本身有参考意义。
 *
 * 2. **静息心率——不参与判定。**
 *    文献里「低压导致心率上升」说的是**高海拔**（数百 hPa 量级），
 *    而天气级的气压变化只有几个 hPa，量级差了上百倍。
 *    气象敏感的研究主要集中在头痛/偏头痛，且机理仍是假说。
 *    所以它只能作为「身体已有反应」的提示，不能拿来预测降雨。
 *
 * 3. **腕温——不参与判定。**
 *    资料原话：环境温度是腕温最大的混淆项，衣袖内外的读数能差出好几度；
 *    因此厂商普遍是拿它与自身滚动基线比较，而不是用绝对阈值。
 *    它反映的是环境与佩戴状况，不是天气。
 *
 * 气压始终是唯一判定依据（3 小时降 4 hPa 即「暴风定律」的门槛，见 WeatherRule）。
 * 本文件的作用是：光照那一档做微调，另两条让你知道身体有没有在响应。
 */
object CorroborationEngine {

    /** 10 分钟内相对跌幅达到此比例，视为云层增厚的物理迹象。 */
    const val LIGHT_DROP_RATIO = 0.4f

    /** 基准亮度低于此值就不算——夜间、袖内、室内的跌幅没有天气含义。 */
    const val LIGHT_MEANINGFUL_LUX = 200f

    /** 静息心率高于基线此值，视为身体已有反应（仅提示）。 */
    const val HEART_RATE_DELTA_BPM = 8f

    /** 腕温偏离会话基线此值就提示（仅提示：环境与衣袖混淆太大）。 */
    const val WRIST_TEMP_DELTA_C = 0.8f

    fun evaluate(
        lightLux: Float?,
        lightDelta10Min: Float?,
        heartRateBpm: Float?,
        restingHeartRateBpm: Float?,
        wristTemperatureC: Float?,
        wristTempBaselineC: Float?,
    ): List<Corroboration> {
        val result = mutableListOf<Corroboration>()

        if (lightLux != null && lightDelta10Min != null && lightDelta10Min < 0f) {
            val previousLux = lightLux - lightDelta10Min
            if (previousLux >= LIGHT_MEANINGFUL_LUX) {
                val ratio = -lightDelta10Min / previousLux
                if (ratio >= LIGHT_DROP_RATIO) {
                    result += Corroboration(
                        kind = CorroborationKind.LIGHT_DROP,
                        label = "光照骤降 %.0f%%".format(ratio * 100),
                        canUpgradeLikelihood = true,
                        note = "10 分钟内环境光由 %.0f lux 降到 %.0f lux（−%.0f%%），" +
                            "与云层增厚的表现一致；这是本应用中唯一具备物理因果的佐证信号"
                                .format(previousLux, lightLux, ratio * 100),
                    )
                }
            }
        }

        if (heartRateBpm != null && restingHeartRateBpm != null &&
            heartRateBpm - restingHeartRateBpm >= HEART_RATE_DELTA_BPM
        ) {
            result += Corroboration(
                kind = CorroborationKind.HEART_RATE_UP,
                label = "心率偏高 %.0f bpm".format(heartRateBpm - restingHeartRateBpm),
                canUpgradeLikelihood = false,
                note = "静息心率 %.0f bpm，高于基线 %.0f bpm。仅作为「身体已有反应」的提示：" +
                    "文献中气压导致心率上升的证据来自高海拔（数百 hPa 量级），" +
                    "天气级的几 hPa 不足以据此预测降雨"
                    .format(heartRateBpm, restingHeartRateBpm),
            )
        }

        if (wristTemperatureC != null && wristTempBaselineC != null &&
            kotlin.math.abs(wristTemperatureC - wristTempBaselineC) >= WRIST_TEMP_DELTA_C
        ) {
            val delta = wristTemperatureC - wristTempBaselineC
            result += Corroboration(
                kind = CorroborationKind.WRIST_TEMP_SHIFT,
                label = "腕温偏离 %+.1f℃".format(delta),
                canUpgradeLikelihood = false,
                note = "腕温 %.1f℃，偏离本次会话基线 %.1f℃。仅作提示：" +
                    "环境温度与衣袖是腕温最大的混淆项，它反映佩戴与环境，不是天气"
                    .format(wristTemperatureC, wristTempBaselineC),
            )
        }

        return result
    }

    /**
     * 用佐证微调风雨倾向：**只在气压本身已在下降倾向时，最多升一档**。
     *
     * 这样设计的原因：佐证不能凭空造出结论（否则一次开灯关灯的误判就能报出降雨），
     * 只能加强一个已经存在的下降判断。
     */
    fun apply(
        likelihood: RainLikelihood,
        items: List<Corroboration>,
    ): RainLikelihood {
        if (items.none { it.canUpgradeLikelihood }) return likelihood
        return when (likelihood) {
            RainLikelihood.LOW -> RainLikelihood.MEDIUM
            RainLikelihood.MEDIUM -> RainLikelihood.HIGH
            else -> likelihood
        }
    }
}
