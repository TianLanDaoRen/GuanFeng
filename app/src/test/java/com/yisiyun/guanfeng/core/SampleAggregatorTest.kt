package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** 采样聚合器的单测：三个通道各有各的正确统计量，不能混用。 */
class SampleAggregatorTest {

    @Test
    fun `中位数_空列表返回零`() {
        assertEquals(0f, SampleAggregator.median(emptyList()), 1e-6f)
    }

    @Test
    fun `中位数_奇数个取中间值`() {
        assertEquals(1003f, SampleAggregator.median(listOf(1001f, 1003f, 1005f)), 1e-6f)
    }

    @Test
    fun `中位数_偶数个取中间两者均值`() {
        // [1001, 1003, 1004, 1005] 的中位数是 (1003+1004)/2 = 1003.5
        assertEquals(1003.5f, SampleAggregator.median(listOf(1001f, 1003f, 1004f, 1005f)), 1e-6f)
    }

    @Test
    fun `中位数能抵抗单点尖峰`() {
        // 若用均值，一个 1200 的尖峰会把结果拉到 1029；中位数不受影响
        val values = listOf(1002f, 1003f, 1003f, 1004f, 1200f)
        assertEquals(1003f, SampleAggregator.median(values), 1e-6f)
    }

    @Test
    fun `聚合口径_气压取中位_加速度取峰值_步数求和`() {
        val aggregator = SampleAggregator()
        aggregator.addPressure(1001f)
        aggregator.addPressure(1010f)
        aggregator.addPressure(1002f)
        aggregator.addVerticalAccel(0.1f)
        aggregator.addVerticalAccel(2.4f)
        aggregator.addVerticalAccel(0.3f)
        aggregator.addStep()
        aggregator.addStep()

        val sample = aggregator.flush(1_700_000_000_000L)

        assertEquals(1002f, sample!!.pressureHpa, 1e-6f)
        assertEquals("加速度峰值不能被平均掉，否则抬腕与电梯脉冲会消失", 2.4f, sample.verticalAccel, 1e-6f)
        assertEquals(2, sample.stepsInWindow)
    }

    @Test
    fun `flush 之后状态清零_不污染下一个样本`() {
        val aggregator = SampleAggregator()
        aggregator.addPressure(1000f)
        aggregator.addVerticalAccel(3f)
        aggregator.addStep()
        aggregator.flush(1L)

        assertEquals(0f, aggregator.currentAccelPeak, 1e-6f)
        assertEquals(0, aggregator.currentSteps)
        assertNull("没有新的气压读数就不该造出样本", aggregator.flush(2L))
    }
}
