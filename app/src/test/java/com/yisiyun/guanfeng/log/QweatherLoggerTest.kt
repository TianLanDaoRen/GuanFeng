package com.yisiyun.guanfeng.log

import com.yisiyun.guanfeng.data.QweatherClient
import java.io.File
import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 和风 CSV 的格式化测试。
 *
 * 两个断言值得单独说，因为它们守的是**不会在中文环境下暴露**的坑：
 *
 * 1. **小数点必须是点**。默认 Locale 在德语区等地方会把小数点写成逗号，
 *    那会直接把 CSV 的列切碎——而这类 bug 在中文本机永远测不出来，
 *    换台设备或换个人跑就炸。
 * 2. **列数必须与表头一致**。表头与格式化函数是两处独立维护的，
 *    加字段时漏改一边就会静默错位：数据看着正常，只是每个值都挪到了错误的列上。
 *    这类错误比崩溃危险得多，因为它不会报错。
 */
class QweatherLoggerTest {

    private val originalLocale = Locale.getDefault()

    @After
    fun restoreLocale() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun `空值输出空串而不是0或NaN`() {
        assertEquals("", QweatherLogger.num(null as Double?))
        assertEquals("", QweatherLogger.num(Double.NaN))
        assertEquals("", QweatherLogger.num(Double.POSITIVE_INFINITY))
    }

    @Test
    fun `按指定小数位格式化`() {
        assertEquals("1.23", QweatherLogger.num(1.2345, 2))
        assertEquals("1.2", QweatherLogger.num(1.24, 1))
        assertEquals("30", QweatherLogger.num(29.6, 0))
    }

    @Test
    fun `整数空值也是空串`() {
        assertEquals("", QweatherLogger.num(null as Int?))
        assertEquals("3", QweatherLogger.num(3))
    }

    @Test
    fun `德语区域下小数点仍是点`() {
        // 德语默认小数分隔符是逗号——若实现里用了 String.format 而不指定 Locale.US，
        // 这里会得到 "1,23"，CSV 随即被切碎。
        Locale.setDefault(Locale.GERMANY)
        assertEquals("1.23", QweatherLogger.num(1.2345, 2))
        assertEquals("1016.47", QweatherLogger.num(1016.47, 2))
    }

    @Test
    fun `实时行的列数与表头一致`() {
        val now = QweatherClient.Now(
            conditionCode = "101", conditionText = "多云",
            tempC = 28.14, feelsLikeC = 25.85, humidity = 0.32,
            windDegree = 206, windCompass = "ssw", windSpeedMs = 4.73, windScale = 3,
            windGustMs = 10.33, precipMm = 0.0, precipIntensityMmh = 0.0,
            precipType = "none", pressureHpa = 1016.47, visibilityM = 29980.0,
            dewPointC = 9.32, cloudCover = 0.17, uvIndex = 3,
        )
        val row = QweatherLogger.formatNowRow(1789195000000L, 39.92, 116.41, now, 935L, "amap:5")
        assertEquals(
            "表头与数据行的列数必须相同，否则整列错位（且不会报错）",
            QweatherLogger.NOW_HEADER.split(",").size,
            row.split(",").size,
        )
        assertTrue("气压必须落在行里", row.contains("1016.47"))
        assertTrue("坐标必须落盘：位置变了要能看出来", row.contains("39.9200"))
        assertTrue("定位来源必须落盘：精度不同，参与校准的方式也不同", row.contains("amap:5"))
    }

    @Test
    fun `逐小时行的列数与表头一致`() {
        val hour = QweatherClient.Hour(
            forecastTime = "2026-09-12T08:00Z",
            conditionCode = "102", conditionText = "少云",
            tempC = 28.26, feelsLikeC = 27.35, humidity = 0.32,
            windDegree = 188, windCompass = "s", windSpeedMs = 2.72, windScale = 2,
            windGustMs = 8.09, precipMm = 0.0, precipIntensityMmh = 0.0,
            precipProbability = 0.0, precipType = "none", pressureHpa = 1015.99,
            visibilityM = 19553.0, dewPointC = 9.87, cloudCover = 0.19, uvIndex = 1,
        )
        val row = QweatherLogger.formatHourRow(1789195000000L, 39.92, 116.41, hour)
        assertEquals(
            "表头与数据行的列数必须相同，否则整列错位（且不会报错）",
            QweatherLogger.HOURLY_HEADER.split(",").size,
            row.split(",").size,
        )
        assertEquals(
            "取数时刻与预报目标时刻是两个不同的量，必须分别落列",
            2,
            row.split(",").count { it.contains("2026") || it.toLongOrNull() == 1789195000000L },
        )
    }

    @Test
    fun `缺字段时留空而不是补零`() {
        // 全部可空字段都给 null：体现"上游没给这个值"而不是"这个值是 0"。
        val hour = QweatherClient.Hour(
            forecastTime = "2026-09-12T09:00Z",
            conditionCode = "", conditionText = "",
            tempC = null, feelsLikeC = null, humidity = null,
            windDegree = null, windCompass = "", windSpeedMs = null, windScale = null,
            windGustMs = null, precipMm = null, precipIntensityMmh = null,
            precipProbability = null, precipType = "", pressureHpa = null,
            visibilityM = null, dewPointC = null, cloudCover = null, uvIndex = null,
        )
        val cells = QweatherLogger.formatHourRow(1789195000000L, 39.92, 116.41, hour).split(",")
        assertEquals(QweatherLogger.HOURLY_HEADER.split(",").size, cells.size)
        // 气压那一列应当为空，而不是 0.00——把"没数据"写成 0 会让统计把它当成真实读数
        val pressureIndex = QweatherLogger.HOURLY_HEADER.split(",").indexOf("pressure_hpa")
        assertEquals("", cells[pressureIndex])
    }

    @Test
    fun `表头一致时不轮转_不一致时必须轮转`() {
        // 一致：正常追加
        assertEquals(false, QweatherLogger.needsRotation("a,b,c", "a,b,c"))
        // 不一致：必须归档另起——就地改表头会让旧行被按新列名解读
        assertEquals(true, QweatherLogger.needsRotation("a,b", "a,b,c"))
        // 空/缺失：新文件，不需要轮转（本来就会写表头）
        assertEquals(false, QweatherLogger.needsRotation(null, "a,b,c"))
        assertEquals(false, QweatherLogger.needsRotation("", "a,b,c"))
    }

    @Test
    fun `只是往后加了列_可以无损就地迁移_保持单文件`() {
        // 这正是真实的那个场景：给实时表加 location_source
        assertTrue(QweatherLogger.canPadToMatch("a,b,c", "a,b,c,d"))
        assertTrue(QweatherLogger.canPadToMatch("a,b,c", "a,b,c,d,e"))
    }

    @Test
    fun `列的顺序或含义变了_不可就地补齐_必须归档`() {
        // 旧表头不是新表头的前缀：旧行补空值也对不上，硬补就是静默错位
        assertFalse(QweatherLogger.canPadToMatch("a,b,c", "a,c,b,d"))
        assertFalse(QweatherLogger.canPadToMatch("a,b,c", "a,b,d"))
        // 反而变短了：更不该猜
        assertFalse(QweatherLogger.canPadToMatch("a,b,c", "a,b"))
        assertFalse(QweatherLogger.canPadToMatch(null, "a,b,c"))
    }

    private fun dayRow(ms: Long, date: String): String = listOf(
        ms.toString(), "20:39:33", date, "33.0040", "114.0120", "101", "多云",
        "28.66", "18.41", "6",
    ).joinToString(",")

    @Test
    fun `表头被挤出尾部窗口时_近七天仍要读得到`() {
        // 现场事故复现（2026-09-12）：文件 8325 字节，读取窗口是**尾部 8 KB**，
        // 表头（第 1 行 107 字节）被挤到窗口之外 133 字节处。
        // 旧实现先找表头、找不到就 `return emptyList()` ——
        // 于是"读不到列名"变成了"没有近七天数据"：数据在盘上一条不少，页面却是空的。
        // 每次采集追加 7 行约 595 字节，所以这是**必然会到的日子**（第 14 轮），不是偶发。
        val file = File.createTempFile("qweather_daily", ".csv")
        try {
            val text = StringBuilder(QweatherLogger.DAILY_HEADER).append('\n')
            var ms = 1789190000000L
            repeat(15) {
                repeat(7) { day ->
                    text.append(dayRow(ms, "2026-09-%02dT16:00Z".format(day + 11))).append('\n')
                }
                ms += 1_800_000L
            }
            file.writeText(text.toString())

            // 先把前提钉死：表头必须**整行**落在窗口之外，否则这个测试什么也没测到。
            // 注意度量单位必须与代码一致——代码读的是**字节**：
            // 用 `takeLast(8192)` 数**字符**是错的（"多云"占 2 字符 6 字节），
            // 那样会把整份文件都拿回来、前提形同没查。这个错我第一次就犯了。
            val headerEnd = QweatherLogger.DAILY_HEADER.toByteArray(Charsets.UTF_8).size + 1
            val windowStart = file.length() - 8192
            assertTrue(
                "前提：表头必须整行落在窗口外（文件 " + file.length() + " 字节、" +
                    "窗口起点 " + windowStart + "、表头结束于 " + headerEnd + "）",
                windowStart >= headerEnd,
            )

            val days = QweatherLogger.readDaysFrom(file)
            assertEquals("表头在窗口外也必须读到 7 天，而不是 0 天", 7, days.size)
            assertEquals("2026-09-11T16:00Z", days.first().dateUtc)
            assertEquals("2026-09-17T16:00Z", days.last().dateUtc)
            assertEquals("只能是最新一组：不许混进上一轮的同名日期", 7, days.map { it.dateUtc }.toSet().size)
        } finally {
            file.delete()
        }
    }

    @Test
    fun `空气质量表头在窗口外时也不能消失`() {
        // 同一个写法、同一个下场：空气质量每次追加 1 行约 65 字节，
        // 4 KB 窗口撑到第 62 轮采集就会把表头顶出去——约两天后突然"没有空气数据"。
        val file = File.createTempFile("qweather_air", ".csv")
        try {
            val text = StringBuilder(QweatherLogger.AIR_HEADER).append('\n')
            var ms = 1789190000000L
            repeat(80) { i ->
                text.append(
                    listOf(
                        ms.toString(), "20:39:33", "33.0040", "114.0120", (40 + i).toString(),
                        "良", "pm25", "35.00", "48.00",
                    ).joinToString(","),
                ).append('\n')
                ms += 1_800_000L
            }
            file.writeText(text.toString())

            assertTrue("前提：文件要长过 4 KB 窗口（实际 " + file.length() + "）", file.length() > 4096)
            val headerEnd = QweatherLogger.AIR_HEADER.toByteArray(Charsets.UTF_8).size + 1
            assertTrue(
                "前提：表头必须整行落在窗口外",
                file.length() - 4096 >= headerEnd,
            )

            val air = QweatherLogger.readAirFrom(file)
            assertTrue("表头在窗口外也必须读到空气数据", air != null)
            assertEquals("读到的必须是最新一条", "119", air?.aqi)
        } finally {
            file.delete()
        }
    }

    @Test
    fun `两个常量表头都必须能被认成表头`() {
        // headerOf 靠"第一行以 fetched_ms 开头"来判断这一行是不是表头；
        // 常量表头若不以它开头，读第一行会失败、只能退回常量，等于把兜底当成了主路。
        assertTrue(QweatherLogger.DAILY_HEADER.startsWith("fetched_ms"))
        assertTrue(QweatherLogger.AIR_HEADER.startsWith("fetched_ms"))
    }
}
