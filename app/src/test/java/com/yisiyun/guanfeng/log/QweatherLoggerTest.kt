package com.yisiyun.guanfeng.log

import com.yisiyun.guanfeng.data.QweatherClient
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
}
