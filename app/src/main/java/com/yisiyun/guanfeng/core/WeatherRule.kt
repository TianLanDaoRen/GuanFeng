package com.yisiyun.guanfeng.core

/**
 * 观风 · 风雨判定规则。
 *
 * 诚实声明（重要）：
 * 下面的映射是**基于气压趋势与降水之间定性关系的启发式规则**，
 * 没有用任何真实降水观测数据校准过，因此 calibrated 恒为 false。
 * 它给的是「高低倾向 + 依据」，不是一个可信的概率数字。
 * 若将来要用真实数据校准，只需替换本文件，内核不必改动。
 */
enum class RainLikelihood(val label: String) {
    HIGH("高"),
    MEDIUM("中"),
    LOW("低"),
    UNKNOWN("未知")
}

data class WeatherAssessment(
    val likelihood: RainLikelihood,
    val advice: String,
    val rationale: String,
    val calibrated: Boolean = false
)

object WeatherRule {

    /**
     * 置信闸门：置信度不是 OK 就直接给「未知」。
     *
     * 实测教训：曾经出现过「窗口 0.14 分钟、R² 0.029，却报出缓升 1.382 hPa/h」的假警报。
     * 与其在 UI 上标注一堆免责说明，不如在源头拒绝给出结论。
     */
    fun assess(trend: TrendResult): WeatherAssessment {
        if (trend.confidence != TrendConfidence.OK) {
            return WeatherAssessment(
                likelihood = RainLikelihood.UNKNOWN,
                advice = "再等等",
                rationale = when (trend.confidence) {
                    TrendConfidence.SHORT_WINDOW ->
                        "窗口只覆盖 %.0f%%，样本还没铺满，斜率不可信"
                            .format(trend.coverageFraction * 100f)

                    TrendConfidence.NOISY ->
                        "窗口内气压抖动过大（R² %.2f），算出的 %.2f hPa/h 撑不起判断"
                            .format(trend.fitRSquared, trend.rateHpaPerHour)

                    TrendConfidence.INSUFFICIENT ->
                        "有效样本 ${trend.weatherSamples} 个，还不足以判断趋势"

                    TrendConfidence.OK -> ""
                }
            )
        }

        return when (trend.grade) {
            TrendGrade.FALLING_FAST -> WeatherAssessment(
                likelihood = RainLikelihood.HIGH,
                advice = "带伞",
                rationale = "3 小时变压 %.1f hPa，气压在急降，通常对应低压槽或强对流逼近"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.FALLING -> WeatherAssessment(
                likelihood = RainLikelihood.MEDIUM,
                advice = "备把伞",
                rationale = "3 小时变压 %.1f hPa，气压缓降，天气有转坏倾向"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.STEADY -> WeatherAssessment(
                likelihood = RainLikelihood.LOW,
                advice = "无变化",
                rationale = "3 小时变压 %.1f hPa，气压平稳"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.RISING -> WeatherAssessment(
                likelihood = RainLikelihood.LOW,
                advice = "天气转稳",
                rationale = "3 小时变压 %+.1f hPa，气压回升，天气趋稳"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.RISING_FAST -> WeatherAssessment(
                likelihood = RainLikelihood.LOW,
                advice = "转晴伴风",
                rationale = "3 小时变压 %+.1f hPa，气压急升，多为冷空气过境后转晴"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.INSUFFICIENT -> WeatherAssessment(
                likelihood = RainLikelihood.UNKNOWN,
                advice = "多攒点样本",
                rationale = "有效样本 ${trend.weatherSamples} 个，还不足以判断趋势"
            )
        }
    }
}
