package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 极简 Markdown 的单测。
 * 用**真实接口返回的原样文本**做夹具（含服务端标语帧），
 * 免得拿自己编的漂亮样例测过了、真机上还是一排星号。
 */
class MarkdownLiteTest {

    /** 来自 plain 口实测返回：正文 + 服务端追加的标语帧。 */
    private val realResponse =
        "数据显示，在7天周期内仅有1天记录到气压数据及头痛标签，气压波动范围在1004.3至1014.0百帕之间。\n\n" +
            "数据显示，气压大幅波动当天伴随记录了头痛，二者在时间上可能有关联。\n\n" +
            "需特别指出，本次样本量很小，相关结论不可靠；建议继续观察气压变化与身体感受的日常记录。" +
            "\n\n---\n\n✨  **Powered by 卦灵AI · 融古通今，智解天机**  "

    @Test
    fun `剥掉服务端标语_正文一个字不少`() {
        val stripped = MarkdownLite.stripSlogan(realResponse)

        assertFalse("标语必须被吃掉", stripped.contains("Powered by"))
        assertFalse("连它上面的分隔线也要吃掉", stripped.trimEnd().endsWith("---"))
        assertTrue("正文必须完整保留", stripped.contains("1004.3至1014.0百帕"))
        assertTrue(stripped.contains("样本量很小"))
    }

    @Test
    fun `没有标语时原样返回`() {
        val plain = "只有一段正文，没有任何标记。"
        assertEquals(plain, MarkdownLite.stripSlogan(plain))
    }

    @Test
    fun `真实返回被解析成三段正文`() {
        val blocks = MarkdownLite.parse(realResponse)

        val paragraphs = blocks.filterIsInstance<MarkdownLite.Block.Paragraph>()
        assertEquals(3, paragraphs.size)
        assertTrue(paragraphs[0].text.startsWith("数据显示"))
        assertTrue(paragraphs[2].text.contains("建议继续观察"))
        assertTrue("不应残留分隔线块", blocks.none { it is MarkdownLite.Block.Rule })
    }

    @Test
    fun `标题_分隔线_项目符号分别成块`() {
        val raw = "# 本周概况\n\n气压平稳。\n\n---\n\n- 头痛 2 次\n- 疲劳 1 次\n\n1. 第三条"

        val blocks = MarkdownLite.parse(raw)

        assertEquals(MarkdownLite.Block.Heading(1, "本周概况"), blocks[0])
        assertEquals(MarkdownLite.Block.Paragraph("气压平稳。"), blocks[1])
        assertEquals(MarkdownLite.Block.Rule, blocks[2])
        assertEquals(MarkdownLite.Block.Bullet("头痛 2 次"), blocks[3])
        assertEquals(MarkdownLite.Block.Bullet("疲劳 1 次"), blocks[4])
        assertEquals("有序列表也按项目符号处理", MarkdownLite.Block.Bullet("第三条"), blocks[5])
    }

    @Test
    fun `加粗被拆成独立段`() {
        val segments = MarkdownLite.inline("本周 **气压** 变化明显，**建议观察**。")

        assertEquals(listOf(false, true, false, true, false), segments.map { it.bold })
        assertEquals("气压", segments[1].text)
        assertEquals("建议观察", segments[3].text)
        assertEquals("本周 ", segments[0].text)
    }

    @Test
    fun `未闭合的加粗不当成错误_原样保留`() {
        val segments = MarkdownLite.inline("这里有个 **没闭合的星号")

        assertEquals(1, segments.size)
        assertFalse(segments[0].bold)
        assertEquals("这里有个 **没闭合的星号", segments[0].text)
    }

    @Test
    fun `中文硬换行不补空格_英文补`() {
        val chinese = MarkdownLite.parse("第一行\n第二行")
        val english = MarkdownLite.parse("first\nsecond")

        assertEquals("第一行第二行", (chinese[0] as MarkdownLite.Block.Paragraph).text)
        assertEquals("first second", (english[0] as MarkdownLite.Block.Paragraph).text)
    }
}
