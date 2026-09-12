package com.yisiyun.guanfeng.log

import com.yisiyun.guanfeng.core.CATEGORY_COMFORT
import com.yisiyun.guanfeng.core.CATEGORY_SYMPTOM
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

    @Test
    fun `解析_旧文件没有 category 列_一律按症状读`() {
        // 旧文件（上表 header 里就没有 category）：那时这一页叫"不适打卡"，可以断定
        val lines = listOf(
            header,
            "1789091922183,2026-09-11 09:58:42,1005.84,0.00,0.000,-0.0,0,32.3,73,头痛,中,",
        )

        val records = CheckInHistory.parse(lines)

        assertEquals(CATEGORY_SYMPTOM, records[0].category)
        assertEquals(false, records[0].isComfort)
    }

    @Test
    fun `解析_带 category 列时按它分组`() {
        // 注意：类里那个共享 header 到 note 为止，**不含 weather_pressure_hpa**。
        // 我第一次写这个用例时直接接了个 ",category"，于是 category 落到了
        // weather_pressure_hpa 的位置上，读出来当然是 symptom——测试错、代码没错。
        // 这里的表头必须与真实文件完全一致（见 CheckInLogger.HEADER）。
        val withCategory = "$header,weather_pressure_hpa,category"
        val lines = listOf(
            withCategory,
            "1789091922183,2026-09-11 09:58:42,1005.84,0.00,0.000,-0.0,0,32.3,73,头痛,中,备注,1005.84,symptom",
            "1789140692867,2026-09-11 23:31:32,1002.85,0.00,0.000,-0.0,0,32.3,78,,,舒服，完全无不适!,1002.85,comfort",
            "1789140692868,2026-09-11 23:32:00,1002.85,0.00,0.000,-0.0,0,32.3,78,,,没有这一列的脏行,1002.85",
        )

        val records = CheckInHistory.parse(lines)

        assertEquals(3, records.size)
        assertEquals(false, records[0].isComfort)
        assertEquals("空 tag + 空强度也要能读", "", records[1].tags)
        assertEquals(CATEGORY_COMFORT, records[1].category)
        assertEquals(true, records[1].isComfort)
        assertEquals("category 列为空 → 按症状", CATEGORY_SYMPTOM, records[2].category)
    }
}
