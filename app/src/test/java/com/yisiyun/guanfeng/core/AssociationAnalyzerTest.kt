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
}
