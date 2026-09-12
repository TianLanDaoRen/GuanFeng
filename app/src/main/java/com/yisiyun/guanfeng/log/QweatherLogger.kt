package com.yisiyun.guanfeng.log

import android.content.Context
import com.yisiyun.guanfeng.data.QweatherClient
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 和风天气的落盘 —— 校准用的**外部真值**。
 *
 * ## 为什么存这么细
 *
 * 主人原话："分小时天气情况、降水、风力、湿度等尽量是完全的信息。"
 * 理由不只是好奇：观风现在**只有人工上报这一路真值**，稀疏且滞后，
 * 所以「报高但没下」和「下了但没报」这两类错误率根本算不出来。
 * 有了每 30 分钟一条的完整气象记录，这两类错误可以按天、按气压变化幅度分组统计。
 *
 * 而逐小时预报里的 `precipProbability` 与 `precipType` 尤其关键：
 * 它们能把"下了点雨"和"只是预报有概率"分开——没有这一列就会把预报的概率
 * 当成实况，统计出来的命中率会是假的。
 *
 * ## 两个文件而不是一个
 *
 * `current` 一次一行，`hourly` 一次 24 行。混在一个文件里会让"取数次数"
 * 和"预报小时数"两个量纠缠在一起，回看时极易算错。分开之后：
 *   · qweather_now.csv     —— 每 30 分钟一行，是真正的时间序列
 *   · qweather_hourly.csv  —— 每次取数 24 行，是预报快照
 *
 * ## 与和风条款的关系
 *
 * 和风允许缓存天气数据（只有**地理信息数据**不得批量缓存/建索引），
 * 而这里**一个字节的地理数据都没有**：坐标是手表自己算出来的，
 * 不是从和风 GeoAPI 拿的（新 v1 API 直接把坐标写进路径，根本没有反查这一步）。
 */
object QweatherLogger {

    const val NOW_FILE = "qweather_now.csv"
    const val HOURLY_FILE = "qweather_hourly.csv"

    /**
     * 列的顺序两处（表头与格式化函数）必须一致，改名时一起改。
     * `lat`/`lon` 也落盘：位置变了要能看出来，否则"同一地点的气压差"这个前提就不成立。
     */
    const val NOW_HEADER =
        "timestamp_ms,clock,lat,lon,condition_code,condition_text,temp_c,feels_like_c,humidity," +
            "wind_degree,wind_compass,wind_speed_ms,wind_scale,wind_gust_ms," +
            "precip_mm,precip_intensity_mmh,precip_type,pressure_hpa,visibility_m,dew_point_c," +
            "cloud_cover,uv_index,http_ms"

    const val HOURLY_HEADER =
        "fetched_ms,fetched_clock,forecast_time,lat,lon,condition_code,condition_text," +
            "temp_c,feels_like_c,humidity,wind_degree,wind_compass,wind_speed_ms,wind_scale," +
            "wind_gust_ms,precip_mm,precip_intensity_mmh,precip_probability,precip_type," +
            "pressure_hpa,visibility_m,dew_point_c,cloud_cover,uv_index"

    private val clockFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    /**
     * 数值格式化：**固定 Locale.US**。
     * 默认 Locale 在某些区域会把小数点写成逗号，那会直接把 CSV 的列切碎——
     * 这类 bug 在中文环境里测不出来，换台设备就爆。
     */
    fun num(value: Double?, digits: Int = 2): String {
        if (value == null || value.isNaN() || value.isInfinite()) return ""
        return String.format(Locale.US, "%.${digits}f", value)
    }

    fun num(value: Int?): String = value?.toString() ?: ""

    fun formatNowRow(
        timestampMs: Long,
        lat: Double,
        lon: Double,
        now: QweatherClient.Now,
        httpMs: Long,
    ): String = listOf(
        timestampMs.toString(),
        clockFormat.format(Date(timestampMs)),
        num(lat, 4),
        num(lon, 4),
        now.conditionCode,
        now.conditionText,
        num(now.tempC),
        num(now.feelsLikeC),
        num(now.humidity, 3),
        num(now.windDegree),
        now.windCompass,
        num(now.windSpeedMs),
        num(now.windScale),
        num(now.windGustMs),
        num(now.precipMm),
        num(now.precipIntensityMmh),
        now.precipType,
        num(now.pressureHpa),
        num(now.visibilityM, 0),
        num(now.dewPointC),
        num(now.cloudCover, 3),
        num(now.uvIndex),
        httpMs.toString(),
    ).joinToString(",")

    fun formatHourRow(
        fetchedMs: Long,
        lat: Double,
        lon: Double,
        hour: QweatherClient.Hour,
    ): String = listOf(
        fetchedMs.toString(),
        clockFormat.format(Date(fetchedMs)),
        hour.forecastTime,
        num(lat, 4),
        num(lon, 4),
        hour.conditionCode,
        hour.conditionText,
        num(hour.tempC),
        num(hour.feelsLikeC),
        num(hour.humidity, 3),
        num(hour.windDegree),
        hour.windCompass,
        num(hour.windSpeedMs),
        num(hour.windScale),
        num(hour.windGustMs),
        num(hour.precipMm),
        num(hour.precipIntensityMmh),
        num(hour.precipProbability, 3),
        hour.precipType,
        num(hour.pressureHpa),
        num(hour.visibilityM, 0),
        num(hour.dewPointC),
        num(hour.cloudCover, 3),
        num(hour.uvIndex),
    ).joinToString(",")

    /** 追加一行；表头不存在时先写表头。返回是否成功。 */
    fun append(context: Context, fileName: String, header: String, row: String): Boolean = runCatching {
        val target = File(directory(context), fileName)
        if (!target.exists() || target.length() == 0L) {
            target.writeText(header + "\n")
        }
        target.appendText(row + "\n")
        true
    }.getOrDefault(false)

    /**
     * 批量追加（逐小时数据一次 24 行）。
     * 逐行调用 [append] 会开 24 次文件——手表上存储慢，批一次写完更稳。
     */
    fun appendRows(context: Context, fileName: String, header: String, rows: List<String>): Boolean =
        runCatching {
            if (rows.isEmpty()) return true
            val target = File(directory(context), fileName)
            val needsHeader = !target.exists() || target.length() == 0L
            target.appendText(buildString {
                if (needsHeader) append(header).append('\n')
                rows.forEach { append(it).append('\n') }
            })
            true
        }.getOrDefault(false)

    /** 现有行数（不含表头），用于在界面上显示"已采多少条"。 */
    fun rowCount(context: Context, fileName: String): Int = runCatching {
        val target = File(directory(context), fileName)
        if (!target.exists()) 0 else target.readLines().count { it.isNotBlank() } - 1
    }.getOrDefault(0)

    private fun directory(context: Context): File =
        context.getExternalFilesDir(null) ?: context.filesDir
}
