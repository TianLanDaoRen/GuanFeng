package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 体感佐证的单测。
 *
 * 重点钉两条口径：
 *   ① 只有光照骤降允许升档（它有物理因果），心率与腕温永远不能；
 *   ② 佐证不能凭空造出结论，只能在气压已在下降时最多升一档。
 */
class CorroborationTest {

    @Test
    fun `光照骤降且基准够亮时算作佐证`() {
        val items = CorroborationEngine.evaluate(
            lightLux = 240f,
            lightDelta10Min = -260f, // 10 分钟前 500 lux，跌到 240，跌 52%
            heartRateBpm = null,
            restingHeartRateBpm = null,
            wristTemperatureC = null,
            wristTempBaselineC = null,
        )

        assertEquals(1, items.size)
        assertEquals(CorroborationKind.LIGHT_DROP, items[0].kind)
        assertTrue("光照是唯一允许升档的信号", items[0].canUpgradeLikelihood)
        assertTrue(items[0].label.contains("52%"))
    }

    @Test
    fun `基准亮度太低时不算_夜里关灯不是天气`() {
        val items = CorroborationEngine.evaluate(
            lightLux = 1f,
            lightDelta10Min = -19f, // 20 lux 跌到 1 lux，跌幅 95% 但基准无意义
            heartRateBpm = null,
            restingHeartRateBpm = null,
            wristTemperatureC = null,
            wristTempBaselineC = null,
        )

        assertTrue("夜间或袖内的跌幅没有天气含义", items.isEmpty())
    }

    @Test
    fun `跌幅不够不算`() {
        val items = CorroborationEngine.evaluate(
            lightLux = 420f,
            lightDelta10Min = -80f, // 500 → 420，跌 16%
            heartRateBpm = null,
            restingHeartRateBpm = null,
            wristTemperatureC = null,
            wristTempBaselineC = null,
        )

        assertTrue(items.isEmpty())
    }

    @Test
    fun `心率偏高只提示_不允许升档`() {
        val items = CorroborationEngine.evaluate(
            lightLux = null,
            lightDelta10Min = null,
            heartRateBpm = 78f,
            restingHeartRateBpm = 62f,
            wristTemperatureC = null,
            wristTempBaselineC = null,
        )

        assertEquals(1, items.size)
        assertEquals(CorroborationKind.HEART_RATE_UP, items[0].kind)
        assertFalse("天气级气压不足以据此预测降雨", items[0].canUpgradeLikelihood)
        assertTrue(items[0].label.contains("16"))
    }

    @Test
    fun `腕温偏离只提示_且注明环境混淆`() {
        val items = CorroborationEngine.evaluate(
            lightLux = null,
            lightDelta10Min = null,
            heartRateBpm = null,
            restingHeartRateBpm = null,
            wristTemperatureC = 34.2f,
            wristTempBaselineC = 33.0f,
        )

        assertEquals(1, items.size)
        assertEquals(CorroborationKind.WRIST_TEMP_SHIFT, items[0].kind)
        assertFalse(items[0].canUpgradeLikelihood)
        assertTrue("必须写明环境是最大混淆项", items[0].note.contains("混淆"))
    }

    @Test
    fun `佐证最多升一档_且不能凭空造出结论`() {
        val upgradable = listOf(
            Corroboration(CorroborationKind.LIGHT_DROP, "光照骤降 60%", true, "")
        )
        val onlyHint = listOf(
            Corroboration(CorroborationKind.HEART_RATE_UP, "心率偏高", false, "")
        )

        assertEquals(
            RainLikelihood.MEDIUM,
            CorroborationEngine.apply(RainLikelihood.LOW, upgradable),
        )
        assertEquals(
            RainLikelihood.HIGH,
            CorroborationEngine.apply(RainLikelihood.MEDIUM, upgradable),
        )
        assertEquals(
            "已经是最高的就不再叠加",
            RainLikelihood.HIGH,
            CorroborationEngine.apply(RainLikelihood.HIGH, upgradable),
        )
        assertEquals(
            "未知不能被佐证升成已知——那样一次关灯就能报出降雨",
            RainLikelihood.UNKNOWN,
            CorroborationEngine.apply(RainLikelihood.UNKNOWN, upgradable),
        )
        assertEquals(
            "只有提示类佐证时不改判定",
            RainLikelihood.LOW,
            CorroborationEngine.apply(RainLikelihood.LOW, onlyHint),
        )
    }
}
