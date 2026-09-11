package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * AI 上报摘要的单测。
 *
 * 最重要的一条是**隐私边界**：摘要里绝不能出现逐条原始记录
 * （备注原文、逐条时间戳、体征逐点值）。这条一旦破了，
 * 「只发聚合统计」的承诺就是假的。
 */
class AiDigestTest {

    private val hour = 60L * 60L * 1000L
    private val day = 24 * hour
    private val offset = 8 * hour

    private fun bucket(hourStartMs: Long, avg: Float, min: Float = avg, max: Float = avg) =
        HourlyBucket(hourStartMs, avg, min, max, 720)

    private fun summaryWith(checkIns: List<CheckInRecord>): AssociationSummary {
        val dayStart = 20_454L * day - offset // 2026-01-01 本地
        val hourly = listOf(
            bucket(dayStart + hour, 1000f, 1000f, 1000f),
            bucket(dayStart + 2 * hour, 996f, 996f, 1000f), // 当天落差 4.0 → 大变化日
        )
        return AssociationAnalyzer.analyze(hourly, checkIns, 7, offset)
    }

    @Test
    fun `摘要里绝不能出现逐条原始记录`() {
        val record = CheckInRecord(
            timestampMs = 20_454L * day - offset + 3 * hour,
            tags = "头痛",
            intensity = "中",
            pressureHpa = 996f,
            note = "起床后右侧发紧",
        )
        val digest = AiDigest.build(summaryWith(listOf(record)), listOf(record), offset)

        assertFalse("备注原文不能出现在上报摘要里", digest.contains("起床后右侧发紧"))
        assertFalse("不应出现逐条打卡时间戳", digest.contains(record.timestampMs.toString()))
        assertTrue("但聚合计数必须带上", digest.contains("\"check_in_count\":1"))
        assertTrue("标签分布要带上", digest.contains("\"头痛\":1"))
    }

    @Test
    fun `摘要结构包含分析所需的关键字段`() {
        val record = CheckInRecord(20_454L * day - offset + 3 * hour, "头痛", "中", 996f, "")
        val digest = AiDigest.build(summaryWith(listOf(record)), listOf(record), offset)

        assertTrue(digest.contains("\"period_days\":7"))
        assertTrue(digest.contains("\"big_swing_days\":1"))
        assertTrue(digest.contains("\"big_swing_dates\":[\"1/1\"]"))
        assertTrue(digest.contains("\"check_in_on_big_swing_days\":1"))
        assertTrue(digest.contains("\"daily_pressure_hpa\":[{\"date\":\"1/1\""))
        assertTrue("必须是合法 JSON 的开头与结尾", digest.startsWith("{") && digest.endsWith("}"))
    }

    @Test
    fun `没有打卡时字段仍然合法`() {
        val digest = AiDigest.build(summaryWith(emptyList()), emptyList(), offset)

        assertTrue(digest.contains("\"check_in_count\":0"))
        assertTrue(digest.contains("\"check_in_tags\":{}"))
        assertTrue(digest.contains("\"check_in_on_big_swing_days\":0"))
    }

    @Test
    fun `系统指令必须带上四条约束`() {
        val prompt = AiDigest.buildSystemInstruction(7)

        assertTrue("禁止医学结论", prompt.contains("禁止任何医学诊断"))
        assertTrue("必须点明样本量小", prompt.contains("样本量很小"))
        assertTrue("限定篇幅", prompt.contains("150 字以内"))
        assertTrue("禁止表格与代码块（腕上放不下）", prompt.contains("不要使用表格、代码块"))
        assertTrue("带上观察周期", prompt.contains("最近 7 天"))
        assertEquals(prompt, prompt.trim())
    }

    @Test
    fun `用户内容只带聚合统计`() {
        val content = AiDigest.buildUserContent("""{"check_in_count":1}""")

        assertTrue(content.contains("统计（JSON）"))
        assertTrue(content.contains("\"check_in_count\":1"))
    }

    @Test
    fun `标签里的特殊字符要被转义_不破坏 JSON`() {
        val record = CheckInRecord(20_454L * day - offset, "头痛\"引号", "中", 1000f, "")
        val digest = AiDigest.build(summaryWith(listOf(record)), listOf(record), offset)

        // 键内部的引号必须转义成 \"；键之后那个引号是 JSON 自身的闭合引号，不能转义。
        // 完整片段应为：  "头痛\"引号":1
        assertTrue("内部引号必须转义", digest.contains("\\\"引号"))
        assertTrue("转义后仍是合法键值", digest.contains("\\\"引号\":1"))
    }
}
