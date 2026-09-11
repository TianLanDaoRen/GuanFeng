package com.yisiyun.guanfeng.log

import com.yisiyun.guanfeng.core.BodyStats
import com.yisiyun.guanfeng.core.HourlyRow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** 小时归档的解析与去重，以及体感概要的单测。 */
class HourlyArchiveTest {

    private val hour = 60L * 60L * 1000L

    private fun row(
        index: Long,
        weather: Float = 1000f,
        samples: Int = 720,
        temp: Float? = 33.0f,
        hr: Float? = 62f,
        light: Float? = 120f,
    ) = HourlyRow(
        hourStartMs = index * hour,
        weatherAvgHpa = weather,
        weatherMinHpa = weather - 1f,
        weatherMaxHpa = weather + 1f,
        rawAvgHpa = weather + 8f,
        samples = samples,
        heartRateAvg = hr,
        restingHeartRate = hr,
        wristTempAvg = temp,
        wristTempMin = temp?.minus(0.4f),
        wristTempMax = temp?.plus(0.4f),
        lightAvgLux = light,
        lightMinLux = light?.minus(20f),
    )

    @Test
    fun `解析按表头定位列_坏行跳过`() {
        val lines = listOf(
            HourlyArchive.HEADER,
            "1789000000000,1000.50,999.50,1001.50,1008.50,720,62,60,33.10,32.70,33.50,120,100",
            "这不是一行有效的归档",
            ",,,,,,,,,,,,",
            "1789003600000,1001.00,1000.00,1002.00,1009.00,700,63,61,33.20,32.80,33.60,130,110",
        )

        val rows = HourlyArchive.parse(lines)

        assertEquals(2, rows.size)
        assertEquals(1789000000000L, rows[0].hourStartMs)
        assertEquals(1000.5f, rows[0].weatherAvgHpa, 0.01f)
        assertEquals(720, rows[0].samples)
        assertEquals(33.1f, rows[0].wristTempAvg!!, 0.01f)
        assertEquals(130f, rows[1].lightAvgLux!!, 0.01f)
    }

    @Test
    fun `缺列或空单元格时体感字段为空_不崩`() {
        val lines = listOf(
            HourlyArchive.HEADER,
            "1789000000000,1000.50,999.50,1001.50,1008.50,720,,,,,,,",
        )

        val rows = HourlyArchive.parse(lines)

        assertEquals(1, rows.size)
        assertNull(rows[0].heartRateAvg)
        assertNull(rows[0].wristTempAvg)
        assertNull(rows[0].lightAvgLux)
    }

    @Test
    fun `同一整点重复落盘时保留样本数最多的一行`() {
        // 场景：应用在整点前后各重启一次，同一小时被落盘两次
        val rows = listOf(
            row(10, weather = 1000f, samples = 200),
            row(10, weather = 1002f, samples = 720),
            row(11, weather = 1003f, samples = 700),
        )

        val deduped = HourlyArchive.dedupeByHour(rows)

        assertEquals(2, deduped.size)
        assertEquals("同一小时只留一行", 1, deduped.count { it.hourStartMs == 10 * hour })
        assertEquals("保留样本更多的那一行", 1002f, deduped.first().weatherAvgHpa, 0.01f)
    }

    @Test
    fun `体感概要取中位数_离群不污染`() {
        val rows = listOf(
            row(1, temp = 33.0f),
            row(2, temp = 33.1f),
            row(3, temp = 36.8f), // 袖子翻上去的离群
            row(4, temp = 33.2f),
            row(5, temp = 33.1f),
        )

        val summary = BodyStats.summarize(rows)

        assertEquals(5, summary.hourlyRows)
        assertEquals("中位数不受离群点影响", 33.1f, summary.wristTemperature!!.median, 0.01f)
        assertEquals("但极值要保留，它是判断环境影响的线索", 36.8f, summary.wristTemperature.max, 0.01f)
        assertEquals(33.0f, summary.wristTemperature.min, 0.01f)
    }

    @Test
    fun `没有体感数据时概要为 null_不用零冒充`() {
        val rows = listOf(
            HourlyRow(
                hourStartMs = hour,
                weatherAvgHpa = 1000f,
                weatherMinHpa = 999f,
                weatherMaxHpa = 1001f,
                rawAvgHpa = 1008f,
                samples = 720,
                heartRateAvg = null,
                restingHeartRate = null,
                wristTempAvg = null,
                wristTempMin = null,
                wristTempMax = null,
                lightAvgLux = null,
                lightMinLux = null,
            )
        )

        val summary = BodyStats.summarize(rows)

        assertNull(summary.heartRate)
        assertNull(summary.wristTemperature)
        assertNull(summary.light)
        assertEquals(1, summary.hourlyRows)
    }

    @Test
    fun `二元中位数取中间两数均值`() {
        assertEquals(2.5f, BodyStats.median(listOf(1f, 4f))!!, 0.001f)
        assertEquals(2f, BodyStats.median(listOf(1f, 2f, 3f))!!, 0.001f)
        assertNull(BodyStats.median(emptyList()))
        assertTrue(BodyStats.median(listOf(5f)) == 5f)
    }
}
