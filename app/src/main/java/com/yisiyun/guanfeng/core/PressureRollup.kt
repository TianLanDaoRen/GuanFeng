package com.yisiyun.guanfeng.core

/**
 * 小时级降采样桶。
 *
 * 为什么需要它：原始采样是 5 秒一个点，一周就是约 12 万行 / 15 MB 文本。
 * 要在手腕上画「最近一周的气压曲线 + 打卡点」这张关联图，直接把原始样本读进来
 * 会又慢又占内存。按小时折叠之后，一周只有 168 个桶，一次读盘几十 KB 就够。
 *
 * 分工：
 *   · 原始样本（5 秒）—— 只服务 3 小时趋势窗口与高度解耦；
 *   · 小时桶 —— 服务长视图（关联视图 / 周报）。
 */
data class HourlyBucket(
    val hourStartMs: Long,
    val avgHpa: Float,
    val minHpa: Float,
    val maxHpa: Float,
    val sampleCount: Int,
) {
    val swingHpa: Float get() = maxHpa - minHpa
}

/**
 * 边读边折叠，**不把整周样本留在内存里**。
 * 用法：从磁盘逐行解析出样本后立刻 add，最后 buckets() 取结果。
 *
 * 注意喂进来的必须是**天气分量**的气压（已解耦掉高度），不是气压计原始读数——
 * 否则坐一趟电梯（9 hPa）就会被算成一次「气压大变化日」。
 */
class HourlyAccumulator {

    private class Partial(var sum: Double, var min: Float, var max: Float, var count: Int)

    private val partials = HashMap<Long, Partial>()

    fun add(timestampMs: Long, pressureHpa: Float) {
        val hourStart = floorToHour(timestampMs)
        val slot = partials[hourStart]
        if (slot == null) {
            partials[hourStart] = Partial(pressureHpa.toDouble(), pressureHpa, pressureHpa, 1)
        } else {
            slot.sum += pressureHpa
            if (pressureHpa < slot.min) slot.min = pressureHpa
            if (pressureHpa > slot.max) slot.max = pressureHpa
            slot.count++
        }
    }

    fun addAll(samples: Iterable<PressureSample>) {
        samples.forEach { add(it.timestampMs, it.pressureHpa) }
    }

    fun buckets(): List<HourlyBucket> = partials.entries
        .map { (hourStart, slot) ->
            HourlyBucket(
                hourStartMs = hourStart,
                avgHpa = (slot.sum / slot.count).toFloat(),
                minHpa = slot.min,
                maxHpa = slot.max,
                sampleCount = slot.count,
            )
        }
        .sortedBy { it.hourStartMs }

    companion object {
        /** 向下取整到整点。用 UTC 毫秒做步长，时区不影响分桶边界的一致性。 */
        fun floorToHour(timestampMs: Long): Long =
            timestampMs - Math.floorMod(timestampMs, 60L * 60L * 1000L)
    }
}
