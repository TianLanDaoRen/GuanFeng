package com.yisiyun.guanfeng.log

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 归档体检的判定口径。
 *
 * 它守的是**唯一永不删除**的那份数据（`hourly.csv`）：原始采样文件只留最近 30 MB
 * 就会被裁掉，归档要是悄悄停止增长，几周后才发现就再也补不回来了。
 * 所以这里的口径必须写死成测试：**当前这个没走完的小时不算缺口**，
 * 而"原始文件有、归档没有"的整点必须一个不漏地报出来。
 */
class ArchiveHealthTest {

    private val hour = 3_600_000L
    private val now = 1_789_000_000_000L
    private val currentHour = now / hour * hour

    private fun health(
        archiveHours: List<Long>,
        sampleHours: List<Long>,
        sampleBytes: Long = 1_000L,
        sampleLimitBytes: Long = 30L * 1024L * 1024L,
        sampleEarliestMs: Long? = null,
    ) = evaluateArchiveHealth(
        archiveHours = archiveHours,
        sampleRecentHours = sampleHours,
        nowMs = now,
        sampleBytes = sampleBytes,
        sampleLimitBytes = sampleLimitBytes,
        sampleEarliestMs = sampleEarliestMs,
    )

    @Test
    fun `归档覆盖了原始文件近段的所有整点就算健康`() {
        val sample = (0..5).map { currentHour - (it + 1) * hour }
        val result = health(archiveHours = sample, sampleHours = sample)

        assertTrue("一个整点都不缺，应该判为健康", result.healthy)
        assertEquals(0, result.missingRecentHours.size)
        assertEquals(6, result.recentHourCount)
        assertEquals(6, result.archiveHours)
    }

    @Test
    fun `当前这个没走完的小时不算缺口`() {
        // 归档只记**已完成**的小时，正在走的这一小时本来就还没落盘——
        // 把它算成缺口，这一页就会永远显示"缺 1 个整点"，报警变成噪声。
        val archived = (0..2).map { currentHour - (it + 1) * hour }
        val sample = archived + currentHour

        val result = health(archiveHours = archived, sampleHours = sample)
        assertTrue("当前小时不该被算成缺口", result.healthy)
        assertEquals("计数的整点里不含当前小时", 3, result.recentHourCount)
    }

    @Test
    fun `原始文件有而归档缺的整点必须一个不漏地报出来`() {
        val archived = listOf(currentHour - hour, currentHour - 4 * hour)
        val sample = listOf(
            currentHour - hour,
            currentHour - 2 * hour,
            currentHour - 3 * hour,
            currentHour - 4 * hour,
            currentHour,
        )

        val result = health(archiveHours = archived, sampleHours = sample)
        assertFalse("缺了两个整点，不能判为健康", result.healthy)
        assertEquals(
            "缺口要按时间升序、去重、且不含当前小时",
            listOf(currentHour - 3 * hour, currentHour - 2 * hour),
            result.missingRecentHours,
        )
    }

    @Test
    fun `重复落盘的整点被统计出来但不影响健康判定`() {
        // 应用重启会把当前小时再落一次盘，读取时按整点去重；这里只把重复数报出来
        val archived = listOf(currentHour - hour, currentHour - hour, currentHour - 2 * hour)
        val result = health(archiveHours = archived, sampleHours = archived)

        assertEquals("去重后的整点数", 2, result.archiveHours)
        assertEquals("多出来的那一行就是重复落盘", 1, result.duplicateArchiveHours)
        assertTrue(result.healthy)
    }

    @Test
    fun `覆盖范围取最早与最新`() {
        val result = health(
            archiveHours = listOf(currentHour - 5 * hour, currentHour - hour, currentHour - 3 * hour),
            sampleHours = emptyList(),
        )
        assertEquals(currentHour - 5 * hour, result.earliestArchiveHourMs)
        assertEquals(currentHour - hour, result.latestArchiveHourMs)
    }

    @Test
    fun `归档是空的时候不能装作健康`() {
        val sample = listOf(currentHour - hour, currentHour - 2 * hour)
        val result = health(archiveHours = emptyList(), sampleHours = sample)

        assertFalse("归档一行都没有，必须报出来", result.healthy)
        assertEquals(2, result.missingRecentHours.size)
        assertEquals(null, result.earliestArchiveHourMs)
    }

    @Test
    fun `体积占比不会除零`() {
        assertEquals(0f, health(emptyList(), emptyList(), sampleLimitBytes = 0L).sampleUsage, 0.0001f)
        assertEquals(0.5f, health(emptyList(), emptyList(), sampleBytes = 15L, sampleLimitBytes = 30L).sampleUsage, 0.0001f)
        // 超出上限也不该报出大于 1 的比例（紧凑化是异步的，界面别显示 103%）
        assertEquals(1f, health(emptyList(), emptyList(), sampleBytes = 31L, sampleLimitBytes = 30L).sampleUsage, 0.0001f)
    }
}
