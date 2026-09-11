package com.yisiyun.guanfeng.core

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 高度偏移累积机制的单测。
 *
 * 关联视图与「气压大变化日」都依赖一条**解耦掉高度的天气气压序列**。
 * 引擎只在窗口内做解耦，且它是**相对于窗口起点**的——所以调用方必须
 * 从每步回报里把高度分量累加起来，才能得到与窗口无关的序列。
 *
 * 如果这个回报机制坏了，症状是：坐一趟电梯（约 9 hPa）会被算成一次
 * 「气压大变化日」，整张关联图的结论全被污染。所以必须钉住。
 */
class ElevationOffsetAccumulationTest {

    private val engine = PressureTrendEngine(windowMs = 3L * 60L * 60L * 1000L, minSamples = 20)

    private fun flatSamples(count: Int, fromMs: Long = 0L, pressure: Float = 1010f) =
        (0 until count).map { index ->
            PressureSample(fromMs + index * 15_000L, pressure)
        }

    @Test
    fun `平稳时不应报出高度分量`() {
        val result = engine.compute(flatSamples(40))

        assertNull("没有竖直运动就不该有高度分量", result.lastElevationStepHpa)
    }

    @Test
    fun `电梯下行时报出负的高度分量并可累加`() {
        // 平稳段必须长到超过 minSamples，否则前缀都返回「数据不足」而报不出东西
        val samples = ArrayList<PressureSample>(flatSamples(24))
        var timestamp = 24 * 15_000L
        var pressure = 1010f
        // 每 15 秒降 0.9 hPa，并给出竖直加速度证据
        repeat(8) {
            pressure -= 0.9f
            samples += PressureSample(timestamp, pressure, verticalAccel = 1.4f)
            timestamp += 15_000L
        }

        // 按真实节奏走：每来一个新样本算一次（引擎只报「最后一步」）
        var accumulatedOffset = 0f
        var sampleCount = 0
        for (end in 20..samples.size) {
            val result = engine.compute(samples.subList(0, end))
            result.lastElevationStepHpa?.let {
                accumulatedOffset += it
                sampleCount++
            }
        }

        assertTrue("下行应被识别为高度事件", sampleCount > 0)
        assertTrue("累积偏移应为负（气压被剔掉），实际 $accumulatedOffset", accumulatedOffset < -3f)
        assertTrue("累积量不应超过实际下降 7.2 hPa，实际 $accumulatedOffset", accumulatedOffset > -8f)
    }

    @Test
    fun `天气气压序列在下降过程中保持平稳`() {
        val samples = ArrayList<PressureSample>(flatSamples(24))
        var timestamp = 24 * 15_000L
        var pressure = 1010f
        repeat(8) {
            pressure -= 0.9f
            samples += PressureSample(timestamp, pressure, verticalAccel = 1.4f)
            timestamp += 15_000L
        }

        // 这就是关联视图要做的事：raw - 累计偏移 = 天气分量
        var offset = 0f
        val weatherSeries = ArrayList<Float>()
        for (end in 1..samples.size) {
            val result = engine.compute(samples.subList(0, end))
            result.lastElevationStepHpa?.let { offset += it }
            weatherSeries += samples[end - 1].pressureHpa - offset
        }

        val rawSpan = samples.maxOf { it.pressureHpa } - samples.minOf { it.pressureHpa }
        val weatherSpan = weatherSeries.max() - weatherSeries.min()

        assertTrue("原始序列确实降了 7 hPa 以上", rawSpan > 6f)
        assertTrue(
            "解耦后应几乎不动，实际跨度 $weatherSpan（原始 $rawSpan）",
            weatherSpan < 1f,
        )
    }
}
