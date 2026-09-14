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


    @Test
    fun `双尺度取或_短尺度抓脉冲长尺度抓慢漂移`() {
        // 短尺度（电梯脉冲）：20 秒基线上的 2.8 hPa/分 → 必须触发
        assertTrue(SensorRatePlan.shouldGoFast(2.8f, 20_000L))
        // 长尺度（缆车/滑降慢漂移）：0.08 hPa/分 够不到短尺度阈值，但 5 分钟基线上必须触发
        assertFalse("短尺度看不到慢漂移", SensorRatePlan.shouldGoFast(0.08f, 20_000L))
        assertTrue("长尺度必须抓到它", SensorRatePlan.shouldGoFast(0.08f, 300_000L))
        // 0.06 hPa/分 = 3.6 hPa/3h，是真实天气的量级 —— **有意**保留证据
        assertTrue(SensorRatePlan.shouldGoFast(0.06f, 300_000L))
        // 自然背景（半日潮 0.003 hPa/分）绝不许触发
        assertFalse(SensorRatePlan.shouldGoFast(0.003f, 300_000L))
    }

    @Test
    fun `长尺度看得到_不代表可以只留长尺度`() {
        // 这条钉住"短尺度不能删"：60 秒的电梯在 5 分钟基线上会被稀释到阈值以下，
        // 所以 20 秒这一档必须留着（回放实测：只留长尺度时电梯偏移差 0.02 → 8.88 hPa）
        assertTrue("短尺度在 20 秒基线上看得见电梯", SensorRatePlan.shouldGoFast(2.8f, 20_000L))
        // 注意：这里**不能**用一个"折算后的假速率"来证明稀释 —— 真实稀释发生在
        // "5 分钟基线里只有 1 分钟在降"这个数据形态上，不是把速率按时间比例缩放。
        // 稀释的证据在回放器里（只留长尺度时电梯偏移差 0.02 → 8.88 hPa），不在这条单测里。
        assertTrue("短尺度基线不足 20 秒时不判档", !SensorRatePlan.shouldGoFast(2.8f, 10_000L))
    }
}
