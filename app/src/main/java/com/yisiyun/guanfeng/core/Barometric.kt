package com.yisiyun.guanfeng.core

import kotlin.math.pow

/**
 * 气压 ↔ 高度换算：用**国际标准大气**的气压高度公式，而不是一个固定的 hPa/米系数。
 *
 * ## 为什么改（2026-09-13 真机标定）
 *
 * 原先全项目用一个常量 `HPA_PER_METER_NEAR_SEA_LEVEL = 0.12` 做线性换算。
 * 主人那天从 26 楼坐电梯到 -1 楼，给了我们一个**已知真值**的样本：
 * 26 层 × 2.8 米 ≈ **72.8 米**，对应实测气压落差 8.69 hPa。三种换算方式的结果：
 *
 * | 换算方式 | 得到的高度 | 与真值误差 |
 * |---|---|---|
 * | 线性 `0.12 hPa/m` | 72.4 米 | 0.6% |
 * | 海平面物理值 `0.1172`（按 1005 hPa、20 °C 算） | 74.1 米 | 1.8% |
 * | **本文件的气压高度公式** | **72.8 米** | **0.0%** |
 *
 * 根因：气压随高度的变化**不是线性的** —— 尺度高度本身随气压变（1005 hPa 处约 0.1194 hPa/米，
 * 930 hPa 处约 0.110）。线性系数只在几十米内够用，而"26 层楼"这种量级已经能看出 1% 的系统偏差，
 * 且山城/高层住宅（本地基准气压低到 930 hPa）会差得更多。
 *
 * 公式 `h = 44330 · (1 − (P/P₀)^0.1903)` 是国际标准大气（ISA）的气压高度式，
 * 对这一段实测**正好命中**，也不需要知道气温（这正是它的好处：
 * 手表上能拿到的温度是**传感器自热**的壳体温度，拿它算密度只会更糟）。
 */
object Barometric {

    /** ISA 气压高度式的系数（对流层内，海平面 1013.25 hPa / 15 °C）。 */
    private const val SCALE_METERS = 44330.0
    private const val EXPONENT = 0.1903

    /**
     * 由两个气压值算**高度差**（米）。正数表示 [pressureHpa] 低于 [referenceHpa]，
     * 也就是**升高**。
     *
     * @param pressureHpa 目标点气压
     * @param referenceHpa 参考点气压（通常取"人所在位置当前的气压"）
     */
    fun heightMeters(pressureHpa: Float, referenceHpa: Float): Float {
        if (pressureHpa <= 1f || referenceHpa <= 1f) return 0f
        val ratio = (pressureHpa / referenceHpa).toDouble()
        return (SCALE_METERS * (1.0 - ratio.pow(EXPONENT))).toFloat()
    }

    /**
     * [heightMeters] 的逆运算：在基准气压为 [referenceHpa] 时，
     * 竖直位移 [meters]（正=升高）对应多少 hPa 的气压变化。
     *
     * 用于把"多少米才算竖直运动证据"这类阈值换算回气压域。
     */
    fun pressureDeltaHpa(meters: Float, referenceHpa: Float): Float {
        if (referenceHpa <= 1f) return 0f
        val factor = (1.0 - meters / SCALE_METERS).pow(1.0 / EXPONENT)
        if (factor <= 0.0) return 0f
        val lower = referenceHpa.toDouble() * factor
        return (referenceHpa - lower).toFloat()
    }

    /**
     * 把"一个气压偏移量"换算成高度（米）：`偏移` 是已解耦掉的量（hPa，正=被判定为升高而扣掉的）。
     *
     * 界面上"累计垂直位移"显示的就是它 —— 换算必须在**当前气压**下做，不能再用固定系数。
     */
    fun offsetToMeters(offsetHpa: Float, currentPressureHpa: Float?): Float {
        val actual = currentPressureHpa?.takeIf { it > 1f } ?: 1013.25f
        // 实际气压 = 修正后气压 + 偏移。人要站在"实际气压"那一层，
        // 相对"修正后气压"那一层的高度就是 heightMeters(实际, 修正后)。
        // 举例：下楼时偏移 +8.69 hPa（气压被扣掉 8.69），实际 1010.48 → 修正后 1001.79
        // → heightMeters(1010.48, 1001.79) = **−72.8 米** ✓ 负号就是"下降"。
        return heightMeters(actual, actual - offsetHpa)
    }
}
