package com.yisiyun.guanfeng.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 两档采样速率的触发判据。
 *
 * 依据是 2026-09-13 的实测：竖直运动一定让气压以 ≥0.3 hPa/分变化（电梯 2.8、26 层 8.7），
 * 天气做不到（整夜 ≤0.05）；而气压平的时候不可能正在竖直位移。用 41.6 小时真实数据统计，
 * 高速档只占 0.2~0.7% 的时间。回放验证：电梯场景下误差 0.02 hPa，
 * 即使触发器一次都不触发（退化到全 1Hz）也只有 0.08 hPa。
 */
class SensorRatePlanTest {

    @Test
    fun `速率超阈值才升档_且不看方向`() {
        assertFalse("平地走路到不了这个量级", SensorRatePlan.shouldGoFast(0.2f, 30_000L))
        assertTrue("电梯下降（负速率）同样要升档", SensorRatePlan.shouldGoFast(-0.4f, 30_000L))
        assertTrue(SensorRatePlan.shouldGoFast(2.8f, 30_000L))
        // 刚好在阈值上不算（避免边界抖动反复切档）
        assertFalse(SensorRatePlan.shouldGoFast(SensorRatePlan.TRIGGER_HPA_PER_MIN, 30_000L))
    }

    @Test
    fun `基线太短不判档_免得一个抖动就升频`() {
        assertFalse("基线不足 20 秒时，速率算不准，不许触发", SensorRatePlan.shouldGoFast(5f, 10_000L))
        assertTrue(SensorRatePlan.shouldGoFast(0.5f, 20_000L))
    }

    @Test
    fun `高速档到点要退回低速`() {
        val t0 = 1_789_000_000_000L
        assertFalse(SensorRatePlan.shouldReturnToLow(t0 + 30_000L, t0 + SensorRatePlan.HOLD_MS))
        assertTrue(SensorRatePlan.shouldReturnToLow(t0 + SensorRatePlan.HOLD_MS, t0 + SensorRatePlan.HOLD_MS))
    }

    @Test
    fun `两档周期之比是五倍_与实测依据一致`() {
        assertTrue("高速档必须是 5Hz", SensorRatePlan.HIGH_US == 200_000)
        assertTrue("低速档必须是 1Hz", SensorRatePlan.LOW_US == 1_000_000)
        assertTrue(
            "电梯全程（几十秒）必须落在保持窗口内",
            SensorRatePlan.HOLD_MS >= 60_000L,
        )
    }
}
