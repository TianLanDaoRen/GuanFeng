package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * AI 上报摘要的单测。
 *
 * 隐私边界已按主人的判断调整：**备注原文要发**。
 * 理由：标签只能归成六类，而"起床后右侧发紧"这种手写描述才包含可分析的细节，
 * 挡掉它等于让 AI 失去最有用的一路输入。是否上传由用户在同意弹窗里决定，
 * 而不是由代码替他决定。
 * 仍然不发的是：设备标识、用户身份、体征逐点读数（那些既无用也无必要）。
 */
class AiDigestTest {

    private val hour = 60L * 60L * 1000L
    private val day = 24 * hour
    private val offset = 8 * hour

    private fun bucket(hourStartMs: Long, avg: Float, min: Float = avg, max: Float = avg) =
        HourlyBucket(hourStartMs, avg, min, max, 720)

    private fun summaryWith(checkIns: List<CheckInRecord>, swingHpa: Float = 4f): AssociationSummary {
        val dayStart = 20_454L * day - offset // 2026-01-01 本地
        val hourly = listOf(
            bucket(dayStart + hour, 1000f, 1000f, 1000f),
            bucket(dayStart + 2 * hour, 1000f - swingHpa, 1000f - swingHpa, 1000f),
        )
        return AssociationAnalyzer.analyze(hourly, checkIns, 7, offset)
    }

    @Test
    fun `备注原文必须发出_它是最有分析价值的输入`() {
        val record = CheckInRecord(
            timestampMs = 20_454L * day - offset + 3 * hour,
            tags = "头痛",
            intensity = "中",
            pressureHpa = 996f,
            note = "起床后右侧发紧",
        )
        val summary = summaryWith(listOf(record))
        val digest = AiDigest.build(summary, summary, listOf(record), offset)

        assertTrue("备注原文必须带上——那才是可分析的细节", digest.contains("起床后右侧发紧"))
        assertTrue("逐条明细要能对上时间", digest.contains(record.timestampMs.toString()))
        assertTrue("聚合计数仍然保留（字段名已改为 symptom_ 前缀，见下一条测试）",
            digest.contains("\"symptom_check_in_count\":1"))
        assertTrue("标签分布仍然保留", digest.contains("\"头痛\":1"))
        assertTrue("明细里要有 note 字段", digest.contains("\"note\":\"起床后右侧发紧\""))
    }

    @Test
    fun `摘要分全部历史与近七天天两段口径`() {
        val record = CheckInRecord(20_454L * day - offset + 3 * hour, "头痛", "中", 996f, "")
        val recent = summaryWith(listOf(record))
        // 全部历史：数据更多、有大变化日
        val all = summaryWith(listOf(record))

        val digest = AiDigest.build(all, recent, listOf(record), offset)

        assertTrue("必须有全部历史段", digest.contains("\"all_history\":{"))
        assertTrue("必须有近 7 天段", digest.contains("\"recent_7_days\":{"))
        assertTrue("近 7 天段带每日气压极值", digest.contains("\"daily_pressure_hpa\":["))
        assertTrue("日期要带上", digest.contains("\"date\":\"1/1\""))
        assertTrue("阈值要带上，便于模型理解口径", digest.contains("\"big_change_threshold_hpa\":3.0"))
        assertTrue("必须是合法 JSON 的开头与结尾", digest.startsWith("{") && digest.endsWith("}"))
    }

    @Test
    fun `只有近期段才带每日气压明细_避免重复上传`() {
        val record = CheckInRecord(20_454L * day - offset + 3 * hour, "头痛", "中", 996f, "")
        val summary = summaryWith(listOf(record))

        val digest = AiDigest.build(summary, summary, listOf(record), offset)

        // all_history 段不重复带 daily，控制请求体大小
        val allSection = digest.substringAfter("\"all_history\":{").substringBefore("\"recent_7_days\"")
        assertFalse("全部历史段不应重复每日明细", allSection.contains("daily_pressure_hpa"))
    }

    @Test
    fun `没有打卡时字段仍然合法`() {
        val summary = summaryWith(emptyList())
        val digest = AiDigest.build(summary, summary, emptyList(), offset)

        assertTrue(digest.contains("\"symptom_check_in_count\":0"))
        assertTrue(digest.contains("\"check_in_tags\":{}"))
    }

    @Test
    fun `系统指令必须带上四条约束与两段口径`() {
        val prompt = AiDigest.buildSystemInstruction()

        assertTrue("禁止医学结论", prompt.contains("禁止任何医学诊断"))
        assertTrue("必须点明样本量小", prompt.contains("样本量很小"))
        assertTrue("不再限定字数（主人要求放开篇幅）", prompt.contains("不限制字数"))
        assertTrue("要求温和但专业", prompt.contains("温和") && prompt.contains("专业"))
        assertTrue("禁止表格与代码块（腕上放不下）", prompt.contains("不要使用表格、代码块"))
        assertTrue("要求分两段口径作答", prompt.contains("all_history"))
        assertTrue(prompt.contains("recent_7_days"))
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
        val summary = summaryWith(listOf(record))
        val digest = AiDigest.build(summary, summary, listOf(record), offset)

        // 键内部的引号必须转义成 \"；键之后那个引号是 JSON 自身的闭合引号，不能转义。
        assertTrue("内部引号必须转义", digest.contains("\\\"引号"))
        assertTrue("转义后仍是合法键值", digest.contains("\\\"引号\":1"))
    }
    @Test
    fun `导出的打卡计数必须自洽`() {
        // 真机事故（2026-09-13，主人一眼看出）：JSON 里写 "check_in_count": 1，
        // 而同一份 JSON 的 check_in_category 写着 comfort: 4 / symptom: 1 —— 两个数自相矛盾。
        // 成因：summary.checkInCount 其实是**不适**的次数（AssociationAnalyzer 里
        // "原先假定打卡都是症状事件"这个前提被「舒适」标签打破了），字段名却留在原地。
        // 这条测试不假设标签怎么分类，只钉住**不变式**：总数 = 不适 + 舒适。
        val dayStart = 20_454L * day - offset
        val records = listOf(
            // 一条「舒适」+ 两条「不适」：类别是**字段**（不是标签推出来的），必须显式给
            CheckInRecord(dayStart + hour, "睡得好", "", 1002f, "", category = CATEGORY_COMFORT),
            CheckInRecord(dayStart + 2 * hour, "疲劳", "轻", 1001f, ""),
            CheckInRecord(dayStart + 3 * hour, "头痛", "中", 1000f, ""),
        )
        val summary = summaryWith(records)
        val json = AiDigest.build(summary, summary, records, offset)

        fun num(key: String): Int =
            Regex("\"$key\":(\\d+)").find(json)?.groupValues?.get(1)?.toInt() ?: -1

        assertTrue("语义含糊的 check_in_count 不许再出现", !json.contains("\"check_in_count\""))
        val symptom = num("symptom_check_in_count")
        val total = num("check_in_total_count")
        val comfort = num("comfort")
        assertTrue("三个数都得写出来（symptom=$symptom total=$total comfort=$comfort）",
            symptom == 2 && total == 3 && comfort == 1)
        assertEquals("总数必须等于不适 + 舒适（曾经 1 与 comfort:4 打架）", symptom + comfort, total)
        assertTrue("总数不能小于不适数", total >= symptom)
    }
}
