package com.yisiyun.guanfeng.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 「发给 AI 的提示词落盘」的单测。
 *
 * 这个功能唯一要保证的事情就是**逐字**——多一个换行、少一段内容，
 * 它就从一个证据退化成一个容易骗人的摘要。所以这里全部在比对原文，
 * 而不是比对"看起来差不多"。
 */
class AiPromptDumpTest {

    private val nowMs = 1_760_000_000_000L // 固定时刻：断言时间戳行存在，但不绑定时区

    @Test
    fun `落盘内容逐字保留_system_与_user_都不许被加工`() {
        val system = "你是气象分析助手。\n只依据给到的数据作答，不要编造。\n"
        val user = """{"check_in_count":1,"note":"起床后右侧发紧"}"""

        val text = AiPromptDump.render(nowMs, system, user)

        assertTrue("system 段必须一字不差", text.contains("=== system ===\n$system"))
        assertTrue("user 段必须一字不差", text.contains("=== user ===\n$user\n"))
    }

    @Test
    fun `第一行是时间戳_两段按 system_user 顺序排列`() {
        val text = AiPromptDump.render(nowMs, "S", "U")
        val lines = text.split("\n")

        assertTrue(
            "第一行必须是时间戳（yyyy-MM-dd HH:mm:ss.SSS Z）",
            Regex("""^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{3} [+-]\d{4}$""").matches(lines[0]),
        )
        assertEquals("=== system ===", lines[1])
        assertEquals("S", lines[2])
        assertEquals("=== user ===", lines[3])
        assertEquals("U", lines[4])
    }

    @Test
    fun `不截断_长文本长度守恒`() {
        // 40KB：比任何"顺手截断一下"的阈值都长，用来钉住"不许省略"
        val long = "气压趋势段落。".repeat(5_000)
        val text = AiPromptDump.render(nowMs, long, long)

        assertEquals("内容长度必须与原文完全相同", 2, Regex(Regex.escape(long)).findAll(text).count())
    }

    @Test
    fun `末尾没有换行时补一个_已经有就不重复加`() {
        val noTail = AiPromptDump.render(nowMs, "S", "U")
        assertTrue("缺换行会让最后一行和提示符黏在一起", noTail.endsWith("U\n"))

        val withTail = AiPromptDump.render(nowMs, "S", "U\n")
        assertTrue("原本就有换行时不许再加一个空行", withTail.endsWith("U\n") && !withTail.endsWith("U\n\n"))
    }

    @Test
    fun `文件名与落盘目录约定_外层_files_便于 adb pull`() {
        assertEquals("ai_prompt.txt", AiPromptDump.FILE_NAME)
    }
}
