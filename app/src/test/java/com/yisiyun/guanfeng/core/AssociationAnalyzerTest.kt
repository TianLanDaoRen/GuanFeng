package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** 关联统计的单测。重点钉两件事：本地日界不能错、样本量小时不编造比例。 */
class AssociationAnalyzerTest {

    private val hour = 60L * 60L * 1000L
    private val day = 24 * hour
    private val offset = 8 * hour // 东八区

    private fun bucket(hourStartMs: Long, avg: Float, min: Float = avg, max: Float = avg) =
        HourlyBucket(hourStartMs, avg, min, max, 720)

    @Test
    fun `本地日界按偏移切_不能按 UTC 切`() {
        // 本地午夜 = N*day - offset（本地时间 = 绝对时刻 + offset）
        val localMidnight = 20_000L * day - offset

        assertEquals(localMidnight, AssociationAnalyzer.dayStartOf(localMidnight, offset))
        assertEquals("同一天内应归入同一天", localMidnight, AssociationAnalyzer.dayStartOf(localMidnight + 5 * hour, offset))
        assertEquals("差 1 毫秒应落到前一天", localMidnight - day, AssociationAnalyzer.dayStartOf(localMidnight - 1, offset))
    }

    @Test
    fun `日标签能被正确推算_2026年1月1日`() {
        // 1970-01-01 起 56 年：56*365 + 14 个闰日 = 20454 天
        val dayStart = 20_454L * day - offset

        val summary = AssociationAnalyzer.analyze(
            hourly = listOf(bucket(dayStart + hour, 1000f, 995f, 1000f)),
            checkIns = emptyList(),
            periodDays = 7,
            zoneOffsetMs = offset,
        )

        assertEquals(listOf("1/1"), summary.bigSwingDayLabels)
    }

    @Test
    fun `气压落差达到阈值才算大变化日`() {
        val dayStart = 20_454L * day - offset
        val flat = listOf(bucket(dayStart + hour, 1000f, 999f, 1000f)) // 落差 1.0
        val swinging = listOf(
            bucket(dayStart + 2 * hour, 1000f, 1000f, 1000f),
            bucket(dayStart + 3 * hour, 996f, 996f, 1000f), // 该日落差 4.0
        )

        val flatSummary = AssociationAnalyzer.analyze(flat, emptyList(), 7, offset)
        val swingSummary = AssociationAnalyzer.analyze(swinging, emptyList(), 7, offset)

        assertEquals(0, flatSummary.bigSwingDays)
        assertEquals(1, swingSummary.bigSwingDays)
    }

    @Test
    fun `打卡落在变化日上的次数与比例`() {
        val dayStart = 20_454L * day - offset
        val hourly = listOf(
            bucket(dayStart + hour, 1000f, 1000f, 1000f),
            bucket(dayStart + 2 * hour, 996f, 996f, 1000f), // 当天落差 4.0 → 大变化日
            bucket(dayStart + day + hour, 1002f, 1002f, 1002f), // 次日平稳
        )
        val checkIns = listOf(
            CheckInRecord(dayStart + 3 * hour, "头痛", "中", 996f, ""),
            CheckInRecord(dayStart + day + 2 * hour, "疲劳", "轻", 1002f, ""),
        )

        val summary = AssociationAnalyzer.analyze(hourly, checkIns, 7, offset)

        assertEquals(2, summary.checkInCount)
        assertEquals(1, summary.bigSwingDays)
        assertEquals("只有第一天那次打卡落在大变化日", 1, summary.checkInsOnBigSwingDays)
        assertEquals(0.5f, summary.overlapRatio!!, 1e-6f)
    }

    @Test
    fun `没有打卡时不编造比例`() {
        val dayStart = 20_454L * day - offset
        val summary = AssociationAnalyzer.analyze(
            hourly = listOf(bucket(dayStart + hour, 1000f)),
            checkIns = emptyList(),
            periodDays = 7,
            zoneOffsetMs = offset,
        )

        assertEquals(0, summary.checkInCount)
        assertNull("没有打卡就该是 null，而不是 0%——0% 会被读成「没有关联」", summary.overlapRatio)
    }

    @Test
    fun `完全没数据时不崩且计数为零`() {
        val summary = AssociationAnalyzer.analyze(emptyList(), emptyList(), 7, offset)

        assertEquals(0, summary.daysWithData)
        assertEquals(0, summary.bigSwingDays)
        assertEquals(0, summary.checkInCount)
    }

    @Test
    fun `舒适打卡是对照组_不得混进不适的比例`() {
        val dayStart = 20_454L * day - offset
        // 这一天是大变化日（落差 4 hPa）
        val swingDay = listOf(bucket(dayStart, 1000f, 998f, 1002f))
        // 另一天不是（落差 0.5 hPa）
        val calmDay = listOf(bucket(dayStart + day, 1000f, 1000f, 1000.5f))
        val hourly = swingDay + calmDay

        fun record(daysAhead: Long, category: String) = CheckInRecord(
            timestampMs = dayStart + daysAhead * day + hour,
            tags = "",
            intensity = "",
            pressureHpa = 1000f,
            note = "",
            category = category,
        )

        val summary = AssociationAnalyzer.analyze(
            hourly = hourly,
            checkIns = listOf(
                record(0, CATEGORY_SYMPTOM),   // 不适 · 落在变化日
                record(1, CATEGORY_SYMPTOM),   // 不适 · 不在变化日
                record(0, CATEGORY_COMFORT),   // 舒适 · 落在变化日
                record(1, CATEGORY_COMFORT),   // 舒适 · 不在变化日
            ),
            periodDays = 7,
            zoneOffsetMs = offset,
        )

        assertEquals("症状组只数不适的", 2, summary.checkInCount)
        assertEquals(1, summary.checkInsOnBigSwingDays)
        assertEquals(0.5f, summary.overlapRatio!!, 0.001f)

        assertEquals("对照组单独算", 2, summary.comfortCount)
        assertEquals(1, summary.comfortOnBigSwingDays)
        assertEquals(0.5f, summary.comfortRatio!!, 0.001f)
    }

    @Test
    fun `没有对照组时比例为 null_不编造 0%`() {
        val dayStart = 20_454L * day - offset
        val summary = AssociationAnalyzer.analyze(
            hourly = listOf(bucket(dayStart, 1000f, 998f, 1002f)),
            checkIns = listOf(
                CheckInRecord(
                    timestampMs = dayStart + hour,
                    tags = "头痛",
                    intensity = "中",
                    pressureHpa = 1000f,
                    note = "",
                ),
            ),
            periodDays = 7,
            zoneOffsetMs = offset,
        )

        assertEquals(1, summary.checkInCount)
        assertNull("一条舒适打卡都没有时不许写 0%——那是编的", summary.comfortRatio)
    }
}
