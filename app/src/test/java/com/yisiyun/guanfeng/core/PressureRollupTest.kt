package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Test

/** 小时级降采样的单测：它决定长视图能不能在手表上跑得动。 */
class PressureRollupTest {

    private val hour = 60L * 60L * 1000L

    @Test
    fun `整点向下取整`() {
        val base = 1_700_000_000_000L
        val floor = HourlyAccumulator.floorToHour(base)
        assertEquals(0L, floor % hour)
        assertEquals(floor, HourlyAccumulator.floorToHour(floor))
        assertEquals(floor, HourlyAccumulator.floorToHour(floor + hour - 1))
        assertEquals(floor + hour, HourlyAccumulator.floorToHour(floor + hour))
    }

    @Test
    fun `同一小时内的样本折叠成一个桶_均值与极值都对`() {
        val base = 1_700_000_000_000L
        val floor = HourlyAccumulator.floorToHour(base)
        val accumulator = HourlyAccumulator()
        accumulator.add(floor + 1_000L, 1000f)
        accumulator.add(floor + 2_000L, 1006f)
        accumulator.add(floor + 3_000L, 1002f)

        val buckets = accumulator.buckets()

        assertEquals(1, buckets.size)
        assertEquals(1002.6667f, buckets[0].avgHpa, 0.001f)
        assertEquals(1000f, buckets[0].minHpa, 1e-6f)
        assertEquals(1006f, buckets[0].maxHpa, 1e-6f)
        assertEquals(3, buckets[0].sampleCount)
        assertEquals(6f, buckets[0].swingHpa, 1e-6f)
    }

    @Test
    fun `跨小时的样本分成多个桶并按时间升序`() {
        val base = 1_700_000_000_000L
        val floor = HourlyAccumulator.floorToHour(base)
        val accumulator = HourlyAccumulator()
        accumulator.add(floor + 2 * hour, 1002f)
        accumulator.add(floor, 1000f)
        accumulator.add(floor + hour, 1001f)

        val buckets = accumulator.buckets()

        assertEquals(3, buckets.size)
        assertEquals(listOf(floor, floor + hour, floor + 2 * hour), buckets.map { it.hourStartMs })
        assertEquals(1000f, buckets[0].avgHpa, 1e-6f)
        assertEquals(1002f, buckets[2].avgHpa, 1e-6f)
    }
}
