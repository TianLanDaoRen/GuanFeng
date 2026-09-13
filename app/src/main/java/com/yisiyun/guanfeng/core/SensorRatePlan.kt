package com.yisiyun.guanfeng.core

/**
 * 采样速率的**两档策略**：平时低速，气压一动就升到高速。
 *
 * ## 为什么可以用气压当触发器（2026-09-13 实测）
 *
 * 竖直运动**一定**让气压以竖直速率变化（电梯实测 2.8 hPa/分、26 层楼 8.7 hPa），
 * 而天气做不到（实测整夜 ≤0.05 hPa/分）；反过来，**气压平的时候不可能正在发生垂直位移**。
 * 所以"气压速率超阈值"是"可能正在竖直运动"的**充要信号**，不需要靠加速度去猜
 * （用加速度当触发器会循环依赖：判断有没有运动本身就需要高速率）。
 *
 * 用 41.6 小时真实数据统计：高速档只占 **0.2~0.7%** 的时间。
 *
 * ## 代价（同样实测，回放器复现应用自己的聚合值后跑出来的）
 *
 * | 场景 | 气压触发 | 全 1Hz 不触发 |
 * |---|---|---|
 * | 电梯（下 26 层） | **0.02 hPa** | 0.08 hPa |
 * | 走走停停（平地，运动占比 40%） | 0.00 hPa | 0.00 hPa |
 * | 静止 | 0.00 hPa | 0.00 hPa |
 *
 * 也就是说：**即使触发器一次都不触发，退化成的也是"全 1Hz"这一档，误差 0.08 hPa（≈0.7 米）**
 * ——最坏情况是有界的，这是它比"固定半速"更值得做的原因。
 */
object SensorRatePlan {

    /**
     * 低速档：1 Hz。
     *
     * ## 但这台机器上只有气压能真的降到 1 Hz（2026-09-13 真机实测）
     *
     * `dumpsys sensorservice` 里 OWW221 的传感器能力：
     *   · `Press_Sensor`：`minRate = 1.00Hz` → 请求 1 Hz **被接受** ✓
     *   · `Gravity_Sensor`：`minRate = **5.00Hz**` → 请求 1 Hz **被 HAL 钳回 5 Hz** ✗
     *   · AOSP 合成的 Linear Acceleration 跟随重力，同样是 5 Hz ✗
     *
     * 实测证据：应用日志写"采样周期 1000 毫秒（低速档 1Hz）"，而 dumpsys 显示
     * `0x06 sampling_period = 1000.0 ms`（气压 ✓）、`0x09/0x0a = 200.0 ms`（重力/线加速度 ✗）。
     *
     * 所以这套两档策略在**本机**上的真实收益只是"气压 5Hz→1Hz"：
     * 事件投递从 15 条/秒降到 11 条/秒（**−27%**），而不是建模时假设的 −79% ——
     * 那个模型假设运动流也能降到 1 Hz，而硬件不允许。
     * 好处是**精度风险随之归零**：运动证据始终在 5 Hz，解耦物理与已验证的一模一样。
     *
     * 教训：**降频能降到多少，必须先读器件的 minRate，不能只看自己的请求。**
     */
    const val LOW_US = 1_000_000

    /** 高速档：5 Hz（与原实现一致）。 */
    const val HIGH_US = 200_000

    /** 触发阈值：30 秒基线上的气压速率超过它就算"可能在竖直运动"。 */
    const val TRIGGER_HPA_PER_MIN = 0.3f

    /** 触发后维持高速档多久。电梯全程（几十秒）都在里面。 */
    const val HOLD_MS = 60_000L

    /**
     * 该不该升到高速档。
     *
     * @param recentRateHpaPerMin 最近一段基线上的气压速率（hPa/分，带符号）
     * @param baselineSpanMs 这段基线实际有多长（不够长就不判，避免抖动作触发）
     */
    fun shouldGoFast(recentRateHpaPerMin: Float, baselineSpanMs: Long): Boolean =
        baselineSpanMs >= 20_000L && kotlin.math.abs(recentRateHpaPerMin) > TRIGGER_HPA_PER_MIN

    /** 高速档是否该退出（超过保持时间）。 */
    fun shouldReturnToLow(nowMs: Long, fastUntilMs: Long): Boolean = nowMs >= fastUntilMs
}
