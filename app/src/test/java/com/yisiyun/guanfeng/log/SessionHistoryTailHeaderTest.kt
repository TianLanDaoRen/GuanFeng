package com.yisiyun.guanfeng.log

import java.io.File
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 采样文件的**续接**不能因为文件长大而失效。
 *
 * ## 真机事故（2026-09-13）
 *
 * 主人装了一次 App，发现**3 小时窗口没了**（每次重启都从零开始攒），
 * 日志里写着 `从历史续接 0 个样本`。
 *
 * 根因：续接用的 `readTail()` 写成 `parse(readTailLines(file, maxBytes))`，
 * 而 `parse` 把**列表第一行**当表头 —— 尾部窗口（2MB）里根本没有表头。
 * 采样文件前一天还是 1.77MB（窗口内包含表头，因此一直好用），
 * 夜里连续采样后涨到 2.7MB，越过 2MB 窗口，表头被挤出去 → 一行数据被当表头 →
 * 认不出列名 → **静默返回空列表**。
 *
 * 这与 09-12"近七天数据消失"（和风逐日表的尾部窗口）是**同一个 bug 类**。
 * 所以这条守卫故意把文件造到 **超过 2MB**，并**先断言"表头确实在窗口之外"** ——
 * 否则测试就只是在测空气。
 */
class SessionHistoryTailHeaderTest {

    private val files = mutableListOf<File>()

    @After
    fun cleanUp() {
        files.forEach { it.delete() }
    }

    private fun tempFile(name: String): File =
        File.createTempFile(name, ".csv").also { files += it }

    /** 造一个"表头在 2MB 窗口之外"的真实形态采样文件。 */
    private fun bigSampleFile(rows: Int): File {
        val file = tempFile("samples")
        val header = CsvSessionLogger.HEADER
        file.writeText(
            buildString {
                append(header).append('\n')
                for (i in 1..rows) {
                    append(1_789_000_000_000L + i * 5_000L).append(',')
                    append("10:00:00,")
                    append("1005.00,")
                    append("0.10,")
                    append("0,")
                    append("0.000,0.000,平稳,100,0,0.00,0.900,180.0,100,可信,70,,,,0.50")
                    append('\n')
                }
            },
        )
        return file
    }

    @Test
    fun `文件长过尾部窗口之后_续接仍要读得到样本`() {
        val file = bigSampleFile(rows = 30_000)
        val window = 2L * 1024L * 1024L
        val headerBytes = CsvSessionLogger.HEADER.toByteArray(Charsets.UTF_8).size + 1

        // 先把前提钉死：文件长过窗口，且表头整行都不在窗口里
        assertTrue("前提：文件要长过 2MB（实际 ${file.length()} 字节）", file.length() > window)
        assertTrue(
            "前提：表头必须整行落在窗口之外（窗口起点 ${file.length() - window}，表头结束于 $headerBytes）",
            file.length() - window >= headerBytes,
        )

        val restored = SessionHistory.readTail(file)
        assertTrue(
            "续接必须读得到样本——读不到就等于每次重启丢掉整个 3 小时窗口",
            restored.isNotEmpty(),
        )
        assertEquals("气压要解析正确", 1005.00f, restored.last().pressureHpa, 0.01f)
        assertTrue("尾部窗口内应该有几千个样本，不是只有一两行", restored.size > 1000)
    }

    @Test
    fun `小文件本来就能读到（防止修过头）`() {
        val file = bigSampleFile(rows = 20)
        assertEquals(20, SessionHistory.readTail(file).size)
    }

    @Test
    fun `文件不存在或只有表头时给空列表而不是崩`() {
        assertEquals(emptyList<Any>(), SessionHistory.readTail(File("/tmp/不存在-${System.nanoTime()}")))
        val headerOnly = tempFile("header-only")
        headerOnly.writeText(CsvSessionLogger.HEADER + "\n")
        assertTrue(SessionHistory.readTail(headerOnly).isEmpty())
    }
}
