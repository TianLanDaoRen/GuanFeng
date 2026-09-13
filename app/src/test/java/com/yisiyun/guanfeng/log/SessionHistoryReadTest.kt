package com.yisiyun.guanfeng.log

import java.io.File
import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * CSV 读写的两条硬规矩。
 *
 * ## 一、数值必须固定 Locale.US
 *
 * CSV 的列分隔符是半角逗号，而默认 Locale 在德语等区域会把小数点写成逗号：
 * `1013.25` 变成 `1013,25`，那一行凭空多出一列，整行**静默错位**。
 * 这类 bug 在中文手表上永远复现不了。
 *
 * ## 二、读尾部时必须保留**最新**的那一段
 *
 * [SessionHistory.readTailLines] 的窗口上限一旦写错方向（留最旧、丢最新），
 * 后果是"3 小时窗口续接"和"归档修补"都用不到最近的样本，而且**不报错**。
 */
class SessionHistoryReadTest {

    private val originalLocale = Locale.getDefault()
    private val tempFiles = mutableListOf<File>()

    @After
    fun cleanUp() {
        Locale.setDefault(originalLocale)
        tempFiles.forEach { it.delete() }
    }

    private fun tempFile(name: String): File =
        File.createTempFile(name, ".csv").also { tempFiles += it }

    // ---------- 规矩一：Locale ----------

    @Test
    fun `德语区域下小数点是点而不是逗号`() {
        Locale.setDefault(Locale.GERMANY)
        assertEquals("1013.25", csvNum(1013.25, 2))
        assertEquals("1013.250", csvNum(1013.25f, 3))
        assertEquals("1001", csvNum(1001.4f, 0))
    }

    @Test
    fun `空值与NaN输出空串而不是0`() {
        // 0 是一个真实读数；"上游没给"和"就是零"在事后统计里必须能区分
        assertEquals("", csvNum(null as Double?))
        assertEquals("", csvNum(null as Float?))
        assertEquals("", csvNum(Double.NaN))
        assertEquals("", csvNum(Float.NaN))
        assertEquals("", csvNum(Double.POSITIVE_INFINITY))
        assertEquals("0.00", csvNum(0.0))
    }

    @Test
    fun `Float 不要经过 Double 转换`() {
        // 1001.40f 转 Double 会变成 1001.4000244140625——保留两位侥幸还对，
        // 但这是运气，不是设计。这条用例把"不许 toDouble()"钉住。
        Locale.setDefault(Locale.GERMANY)
        assertEquals("1001.40", csvNum(1001.40f, 2))
        assertEquals("999.99", csvNum(999.99f, 2))
    }

    // ---------- 规矩二：尾部读取保留最新一段 ----------

    @Test
    fun `尾部行数超过上限时保留的是最新的那些行`() {
        val file = tempFile("tail")
        // 每行形如 "行号,其余列"：行号从 1 数到 25000（超过 MAX_ROWS_SCANNED=20000）
        val rows = 25_000
        file.writeText(
            buildString {
                for (i in 1..rows) append(i).append(",paddingpaddingpadding\n")
            },
        )

        val lines = SessionHistory.readTailLines(file)
        assertEquals("必须装满上限为止", 20_000, lines.size)
        assertEquals("最后一行必须是文件最后一行", rows.toString(), lines.last().substringBefore(','))
        assertEquals(
            "第一行必须是窗口里最旧的那一行（25000-20000+1），而不是文件第 2 行——" +
                "第一版留的是最旧的两万行，等于把最新的一万小时数据全丢了",
            (rows - 20_000 + 1).toString(),
            lines.first().substringBefore(','),
        )
    }

    @Test
    fun `滚成长文件时最新一小时也必须进得了小时桶`() {
        // 旧实现的 `scanned > MAX_ROWS_SCANNED` 是**从文件开头**数到两万行就 break，
        // 所以文件一旦超过两万行，最近的数据永远进不了桶（首次归档迁移只搬了最旧的段）。
        val rows = 25_000
        val lines = generateSequence(1) { it + 1 }.take(rows).map { i ->
            // 每行相隔 5 秒；行号越大时间越新
            "${1_700_000_000_000L + i * 5_000L},${i}.00"
        }
        val accumulator = com.yisiyun.guanfeng.core.HourlyAccumulator()
        SessionHistory.feedRollup(
            lines = lines,
            iTimestamp = 0,
            iPressure = 1,
            cutoff = 0L,
            accumulator = accumulator,
        )

        val buckets = accumulator.buckets().sortedBy { it.hourStartMs }
        val newestTimestamp = 1_700_000_000_000L + rows * 5_000L
        val newestHour = (newestTimestamp / 3_600_000L) * 3_600_000L
        assertTrue(
            "最新那一小时必须出现在桶里（桶数=${buckets.size}，最后一桶=${buckets.lastOrNull()?.hourStartMs}）",
            buckets.any { it.hourStartMs == newestHour },
        )
        // 窗口外的行要被廉价筛选掉：cutoff 之后只剩最后这一小段
        val nearCutoff = newestTimestamp - 60_000L
        val filtered = com.yisiyun.guanfeng.core.HourlyAccumulator()
        SessionHistory.feedRollup(
            lines = generateSequence(1) { it + 1 }.take(rows).map { i ->
                "${1_700_000_000_000L + i * 5_000L},${i}.00"
            },
            iTimestamp = 0,
            iPressure = 1,
            cutoff = nearCutoff,
            accumulator = filtered,
        )
        assertEquals(
            "cutoff 取最新样本前 60 秒（含两端：12 个间隔 = 13 个样本，同属一小时），" +
                "说明窗口外的两万多个样本确实被筛掉了",
            13,
            filtered.buckets().sumOf { it.sampleCount },
        )
    }

    @Test
    fun `列序变了也能按表头解析_而不是写死第一列`() {
        // timestamp 不在第一列时必须退回"整行 split 后按列名取"，不能拿行首当时间戳
        val accumulator = com.yisiyun.guanfeng.core.HourlyAccumulator()
        SessionHistory.feedRollup(
            lines = sequenceOf(
                "clock,pressure_hpa,timestamp_ms",
                "23:00:00,1013.25,1700000000000",
            ),
            iTimestamp = 2,
            iPressure = 1,
            cutoff = 0L,
            accumulator = accumulator,
        )
        val buckets = accumulator.buckets()
        assertEquals(1, buckets.size)
        assertEquals("1013.25", csvNum(buckets[0].avgHpa.toDouble(), 2))
    }

    @Test
    fun `行数统计不读整份文件_且边界情况正确`() {
        // 行数决定"紧凑化丢掉了多少行"，算错会让记录页与丢弃计数一起漂
        val empty = tempFile("empty")
        empty.writeText("")
        assertEquals("空文件没有数据行", 0, countDataRows(empty))

        val headerOnly = tempFile("header")
        headerOnly.writeText("a,b,c\n")
        assertEquals("只有表头时是 0 行", 0, countDataRows(headerOnly))

        val normal = tempFile("normal")
        normal.writeText("a,b,c\n1,2,3\n4,5,6\n")
        assertEquals("表头之外的都算数据行", 2, countDataRows(normal))

        val truncated = tempFile("truncated")
        // 上次写到一半被杀：末行没有换行，它仍然是一条（不完整的）记录，要算进去
        truncated.writeText("a,b,c\n1,2,3\n4,5")
        assertEquals("末行没有换行也要算一行", 2, countDataRows(truncated))

        val missing = File("/tmp/不存在的文件-${System.nanoTime()}")
        assertEquals("文件不存在时是 0", 0, countDataRows(missing))
    }
}
