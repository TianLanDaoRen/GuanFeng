package com.yisiyun.guanfeng.core

/**
 * 采样聚合器：把高频传感器读数压成「一个样本」。
 *
 * 为什么需要它——正式窗口是 3 小时，而传感器以约 8 Hz 上报：
 *   · 若按 2 秒落一点，一天就是 4 万多行，且相邻样本的差异几乎全是噪声
 *     （实测 2 秒尺度噪声折算 0.6 hPa/min，是判定阈值的 6 倍）；
 *   · 按 **5 秒**聚合成一个样本后（每个样本约聚 40 个原始读数），噪声在聚合里被抹平，
 *     3 小时 2160 个样本，内存与最小二乘拟合仍很轻，落盘约 1.7 万行/天（≈2 MB）。
 *
 * 聚合口径（三路各有各的正确统计量）：
 *   · 气压：**中位数**（抗单点尖峰，比均值稳）
 *   · 垂直加速度：**峰值**（轨迹特征不能被平均掉，否则抬腕/电梯脉冲会消失）
 *   · 步数：**求和**
 */
class SampleAggregator {

    private val pressures = ArrayList<Float>(256)
    private var accelPeak = 0f
    private var verticalDisplacement = 0f
    private var steps = 0

    /** 当前聚合区间内的垂直加速度峰值（供实时判定「是否静止」用）。 */
    val currentAccelPeak: Float get() = accelPeak

    /** 当前聚合区间内的步数。 */
    val currentSteps: Int get() = steps

    fun addPressure(value: Float) {
        pressures += value
    }

    /** 采集器算出的竖直净位移（米），原样带进样本供高度分类器判断。 */
    fun setVerticalDisplacement(meters: Float) {
        verticalDisplacement = meters
    }

    fun addVerticalAccel(value: Float) {
        if (value > accelPeak) accelPeak = value
    }

    fun addStep() {
        steps++
    }

    /** 取走聚合结果并清零。这段时间内一个气压读数都没收到时返回 null——不造样本。 */
    fun flush(timestampMs: Long): PressureSample? {
        if (pressures.isEmpty()) return null
        val sample = PressureSample(
            timestampMs = timestampMs,
            pressureHpa = median(pressures),
            verticalAccel = accelPeak,
            verticalDisplacementM = verticalDisplacement,
            stepsInWindow = steps,
        )
        pressures.clear()
        accelPeak = 0f
        steps = 0
        return sample
    }

    companion object {
        /** 中位数：偶数个取中间两个的均值；空列表返回 0。 */
        fun median(values: List<Float>): Float {
            if (values.isEmpty()) return 0f
            val sorted = values.sorted()
            val middle = sorted.size / 2
            return if (sorted.size % 2 == 1) {
                sorted[middle]
            } else {
                (sorted[middle - 1] + sorted[middle]) / 2f
            }
        }
    }
}
