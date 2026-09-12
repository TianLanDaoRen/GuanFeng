package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 小时归档累加器的单测。
 *
 * 重点钉两条：
 *   ① 整点切换时必须**先把上一小时交出来**再重置——顺序错了会整整丢掉一小时；
 *   ② 没有数据的量必须留 null，不能用 0 冒充（0 bpm 的心率会让 AI 得出荒谬结论）。
 */
class HourlyArchiveModelTest {

    private val hour = 60L * 60L * 1000L
    private val base = 20_454L * 24L * hour // 某个整点

    private fun HourAccumulator.feed(
        offsetMs: Long,
        pressure: Float,
        hr: Float? = null,
        resting: Float? = null,
        temp: Float? = null,
        lux: Float? = null,
    ) = add(
        timestampMs = base + offsetMs,
        weatherHpa = pressure,
        rawHpa = pressure + 8f,
        heartRateBpm = hr,
        restingHeartRateBpm = resting,
        wristTempC = temp,
        lightLux = lux,
    )

    @Test
    fun `同一小时内累加_不产生滚动`() {
        val accumulator = HourAccumulator()

        assertNull(accumulator.feed(0, 1000f))
        assertNull(accumulator.feed(5_000, 1002f))
        assertNull(accumulator.feed(10_000, 1001f))

        val row = accumulator.snapshot()!!
        assertEquals(3, row.samples)
        assertEquals(1001f, row.weatherAvgHpa, 0.01f)
        assertEquals(1000f, row.weatherMinHpa, 0.01f)
        assertEquals(1002f, row.weatherMaxHpa, 0.01f)
    }

    @Test
    fun `整点切换时交出上一小时_且新小时从零开始`() {
        val accumulator = HourAccumulator()
        accumulator.feed(0, 1000f, hr = 60f)
        accumulator.feed(30_000, 1004f, hr = 70f)

        val completed = accumulator.feed(hour + 5_000, 990f)

        assertNotNull("跨整点必须交出上一小时", completed)
        assertEquals(2, completed!!.samples)
        assertEquals(1002f, completed.weatherAvgHpa, 0.01f)
        assertEquals(65f, completed.heartRateAvg!!, 0.01f)

        // 新小时必须是干净的，不能把上一小时的数据带进来
        val current = accumulator.snapshot()!!
        assertEquals(1, current.samples)
        assertEquals(990f, current.weatherAvgHpa, 0.01f)
    }

    @Test
    fun `缺失的体感数据留空_不用零冒充`() {
        val accumulator = HourAccumulator()
        accumulator.feed(0, 1000f, hr = null, temp = null, lux = null)

        val row = accumulator.snapshot()!!

        assertNull("没有心率就不能写 0", row.heartRateAvg)
        assertNull(row.wristTempAvg)
        assertNull(row.lightAvgLux)
        assertNull(row.wristTempMin)
    }

    @Test
    fun `腕温取中位数而非均值_一次脱袖子不该带偏整小时`() {
        val accumulator = HourAccumulator()
        accumulator.feed(0, 1000f, temp = 33.0f)
        accumulator.feed(5_000, 1000f, temp = 36.2f) // 脱袖子暴露的真实离群
        accumulator.feed(10_000, 1000f, temp = 33.2f)

        val row = accumulator.snapshot()!!

        // 均值会是 (33.0+36.2+33.2)/3 = 34.13；中位数是 33.2。
        // 这里若算出 34.13，就说明退回了均值——归档要长期保存并喂给 AI，不能被一次离群带偏。
        assertEquals("中位数不受 36.2 那次离群影响", 33.2f, row.wristTempAvg!!, 0.01f)
        assertEquals(33.0f, row.wristTempMin!!, 0.01f)
        assertEquals("极值要保留，那是判断环境影响的线索", 36.2f, row.wristTempMax!!, 0.01f)
    }

    @Test
    fun `原始读数与天气分量分别归档`() {
        val accumulator = HourAccumulator()
        accumulator.feed(0, 1000f)

        val row = accumulator.snapshot()!!

        assertEquals(1000f, row.weatherAvgHpa, 0.01f)
        assertEquals("原始读数要留档，便于回查解耦是否正确", 1008f, row.rawAvgHpa, 0.01f)
    }

    @Test
    fun `进程重启后回填本小时样本_那一行仍是完整的 720`() {
        // 真机实证的缺陷：22:14 重启后，22:00 那行只落了 545/720 个样本，
        // 少掉的正是重启前的 14 分钟——不会报错，只会悄悄改变长期结论。
        //
        // 这里直接走真实路径：backfillCarriedOver（启动时补） + 采集器继续喂。
        val beforeRestart = 168 // 14 分钟 × 12 个/分钟
        val total = 720

        // 启动前的样本：原始气压 1010，高度偏移 8 → 还原出的天气分量是 1002
        val restored = (0 until beforeRestart).map { i ->
            PressureSample(timestampMs = base + i * 5_000L, pressureHpa = 1010f)
        }
        // 不属于本小时的样本：必须被过滤掉，不能污染
        val previousHour = listOf(
            PressureSample(timestampMs = base - 60_000L, pressureHpa = 9999f),
        )

        val accumulator = HourAccumulator()
        val result = accumulator.backfillCarriedOver(
            samples = previousHour + restored,
            currentHourStartMs = base,
            elevationOffsetHpa = 8f,
        )

        assertEquals("上一小时的样本不得补进来", beforeRestart, result.fedSamples)
        assertTrue("正常不该有整点行被滚出来", result.completedRows.isEmpty())
        assertEquals(beforeRestart, accumulator.snapshot()!!.samples)

        // 重启后的样本接着喂，一路到下一小时
        var completed: HourlyRow? = null
        for (i in beforeRestart until total) {
            completed = accumulator.feed(i * 5_000L, 1002f, hr = 62f) ?: completed
        }
        completed = accumulator.feed(hour, 1000f) ?: completed

        assertNotNull(completed)
        assertEquals("重启前后必须合成完整的一小时，而不是各出一行", total, completed!!.samples)
        assertEquals("回填段用的是 pressureHpa − offset", 1002f, completed.weatherAvgHpa, 0.01f)
        assertEquals("原始读数照样留档", 1010f, completed.rawAvgHpa, 0.01f)
        // 体感在回填段是空的（原始 CSV 里没有），只能由重启后的样本决定——不是 0
        assertEquals(62f, completed.heartRateAvg!!, 0.01f)
        assertEquals(
            "不得因为回填段没有腕温就写出 0℃",
            null,
            completed.wristTempAvg,
        )
    }
}
