package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 「先降后平」这条边界的回归测试。
 *
 * ## 为什么单独测它
 *
 * 这不是构造出来的极端情况，而是**真机遇到的形态**：2026-09-11 下午，
 * 主人在 14:00 看到「缓降 · 中 · 备把伞」（判断正确，手机预报 17:00 有雨），
 * 15:54 之后等级变成「平稳」，16:51 置信度变成「趋势不稳」，界面退回速评。
 *
 * 根因有两条，都是设计错误而非实现错误：
 *   1. 判据只看窗口内**斜率**，而气压降完转入长时间平稳时斜率自然趋零 →
 *      于是把"曾经降过"忘干净，报出「平稳 · 无需带伞」——而外面正在下雨；
 *   2. **R² 闸门会否掉最典型的降雨曲线**：先急降后转平这条曲线本来就不直，
 *      R² 必然低，于是被当成"抖动过大"。
 *
 * 修法是把「降了多少」（净变幅）与「此刻降得多快」（斜率）分开：
 * 净变幅不需要线性拟合，斜率的可信度（R²）只管斜率自己。
 * 另加路径效率闸门，避免把来回振荡也当成急降。
 */
class WeatherRuleNetChangeTest {

    private val engine = PressureTrendEngine(windowMs = 3L * 60L * 60L * 1000L, minSamples = 20)

    private fun samples(count: Int = 400, shape: (Int) -> Float): List<PressureSample> =
        (0 until count).map { index ->
            PressureSample(index * 15_000L, shape(index))
        }

    @Test
    fun `先急降后长时间平稳_仍须给出转坏结论而不能当作无事`() {
        // 前 1/3 急降 5 hPa，之后一路平稳（雨已经到了或正在下）
        val data = samples { index ->
            if (index < 130) 1010f - index * (5f / 130f) else 1005f
        }

        val trend = engine.compute(data)
        val assessment = WeatherRule.assess(trend, recentFallHpa = -5f)

        assertTrue(
            "净降 5 hPa 已经过了「暴风定律」的门槛，不能因为斜率趋零就说没事",
            assessment.likelihood == RainLikelihood.HIGH ||
                assessment.likelihood == RainLikelihood.MEDIUM,
        )
        assertTrue(
            "建议必须是带伞类，不能是「无需带伞」",
            assessment.advice.contains("伞"),
        )
    }

    @Test
    fun `降完转平但净量不大_给已转平稳而不是退回速评`() {
        // 缓降 1 hPa 后转平：R² 会很低（曲线不直），但净量确实不大
        val data = samples { index ->
            if (index < 130) 1010f - index * (1f / 130f) else 1009f
        }

        val trend = engine.compute(data)
        val assessment = WeatherRule.assess(trend)

        assertTrue("应当给出一个结论，而不是未知", assessment.likelihood != RainLikelihood.UNKNOWN)
        assertEquals(
            "此时的诚实说法是变化已结束",
            RainLikelihood.LOW,
            assessment.likelihood,
        )
    }

    @Test
    fun `来回振荡仍然不给结论`() {
        // 净变幅接近 0 但路程很长：路径效率极低
        val data = samples { index -> 1010f + if (index % 40 < 20) 2f else -2f }

        val trend = engine.compute(data)
        val assessment = WeatherRule.assess(trend)

        assertTrue(
            "振荡不能被当成急降报出去（这正是路径效率闸门要挡的）",
            trend.pathLengthHpa > 10f,
        )
        assertEquals(
            "既没可信斜率、净量也不代表趋势 → 未知",
            RainLikelihood.UNKNOWN,
            assessment.likelihood,
        )
    }

    @Test
    fun `路径效率能区分降完转平与来回振荡`() {
        val dropThenFlat = engine.compute(samples { index ->
            if (index < 130) 1010f - index * (5f / 130f) else 1005f
        })
        val oscillating = engine.compute(samples { index -> 1010f + if (index % 40 < 20) 2f else -2f })

        val flatEfficiency = kotlin.math.abs(dropThenFlat.observedDeltaHpa) / dropThenFlat.pathLengthHpa
        val oscillationEfficiency = kotlin.math.abs(oscillating.observedDeltaHpa) / oscillating.pathLengthHpa

        assertTrue("降完转平的路径效率应当很高，实际 $flatEfficiency", flatEfficiency > 0.8f)
        assertTrue("振荡的路径效率应当很低，实际 $oscillationEfficiency", oscillationEfficiency < 0.5f)
    }
}
