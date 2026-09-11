package com.yisiyun.guanfeng.log

import org.junit.Assert.assertEquals
import org.junit.Test

/** 打卡记录读取的单测。 */
class CheckInHistoryTest {

    private val header =
        "timestamp_ms,datetime,pressure_hpa,delta_hpa_3h,rate_hpa_per_hour," +
            "elevation_meters,heart_rate_bpm,wrist_temp_c,light_lux,tags,intensity,note"

    @Test
    fun `解析_按表头定位列`() {
        val lines = listOf(
            header,
            "1789091922183,2026-09-11 09:58:42,1005.84,0.00,0.000,-0.0,0,32.3,73,头痛,中,",
        )

        val records = CheckInHistory.parse(lines)

        assertEquals(1, records.size)
        assertEquals(1789091922183L, records[0].timestampMs)
        assertEquals("头痛", records[0].tags)
        assertEquals("中", records[0].intensity)
        assertEquals(1005.84f, records[0].pressureHpa!!, 0.001f)
    }

    @Test
    fun `解析_坏行跳过`() {
        val lines = listOf(
            header,
            "1789091922183,2026-09-11 09:58:42,1005.84,0,0,0,0,32.3,73,头痛,中,",
            "这不是CSV",
            ",,,,,",
            "1789091999999,2026-09-11 10:00:00,1005.00,0,0,0,0,32.3,73,关节,重,备注",
        )

        val records = CheckInHistory.parse(lines)

        assertEquals(2, records.size)
        assertEquals("关节", records[1].tags)
        assertEquals("备注", records[1].note)
    }

    @Test
    fun `解析_没有气压列时不崩_记为空`() {
        val lines = listOf("timestamp_ms,tags,intensity", "1000,头痛,中")

        val records = CheckInHistory.parse(lines)

        assertEquals(1, records.size)
        assertEquals(null, records[0].pressureHpa)
    }

    @Test
    fun `解析_空输入返回空`() {
        assertEquals(0, CheckInHistory.parse(emptyList()).size)
        assertEquals(0, CheckInHistory.parse(listOf(header)).size)
    }
}
