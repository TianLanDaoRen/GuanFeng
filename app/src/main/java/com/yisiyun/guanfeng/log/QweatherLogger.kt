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
            "cloud_cover,uv_index,location_source,http_ms"

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

    /**
     * `locationSource` 记的是**这个坐标怎么来的**：`amap:5`（高德 Wi-Fi 定位）、
     * `amap:1`（高德 GPS）、`network-ip:城市`（IP 推断）、`system:gps`（系统缓存）等。
     *
     * 为什么要单独一列：坐标本身看不出精度，而精度决定了这条数据该怎么参与校准。
     * 30 米的 Wi-Fi 定位和城市级的 IP 推断如果混在一列里，事后没法区分，
     * 也没法回答"IP 推断的坐标到底够不够用"这个问题——而那正是我们做这条链路的初衷。
     */
    fun formatNowRow(
        timestampMs: Long,
        lat: Double,
        lon: Double,
        now: QweatherClient.Now,
        httpMs: Long,
        locationSource: String = "",
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
        locationSource,
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

    /**
     * 表头与本文件现有表头不一致时，**必须轮转另起一个文件**。
     *
     * 为什么不能就地改表头：旧行是按旧列写的，改了表头它们会被按新列名重新解读——
     * 每个值都还在，只是意义全变了。**这是静默的数据损坏**，比崩溃危险得多：
     * 崩溃你会知道，这个只有事后分析时才发现"数据怎么这么怪"。
     *
     * 真机上就撞到过：给实时表加了 location_source 之后，旧文件还是 23 列表头、
     * 新行 24 列，用旧表头一读，http_ms 那一列显示的是 `amap:4`。
     *
     * **但优先就地迁移而不是另存**（主人强调的单文件原则）：
     * 如果旧表头是新表头的**前缀**（也就是我们只是往后加了列），那旧行只要补空值就能
     * 与新区头对齐，信息一点不丢——这时就地补齐、原子改名，**始终保持一个文件**。
     * 只有列的顺序/含义真的变了（旧表头不是前缀）才另存归档，因为那时旧行无法安全对齐。
     *
     * 抽成纯函数是为了能写 JVM 单测——文件操作要 Context，判断逻辑不需要。
     */
    fun needsRotation(existingHeader: String?, expectedHeader: String): Boolean =
        !existingHeader.isNullOrBlank() && existingHeader != expectedHeader

    /** 旧表头是不是新表头的前缀（即"只是往后加了列"）。这种情况可以无损就地补齐。 */
    fun canPadToMatch(existingHeader: String?, expectedHeader: String): Boolean {
        if (existingHeader.isNullOrBlank()) return false
        val old = existingHeader.split(",")
        val new = expectedHeader.split(",")
        return old.size < new.size && new.subList(0, old.size) == old
    }

    /** 追加一行；文件不存在则写表头，表头变了则把旧文件归档后另起。返回是否成功。 */
    fun append(context: Context, fileName: String, header: String, row: String): Boolean = runCatching {
        val target = rotateIfNeeded(context, fileName, header)
        if (!target.exists() || target.length() == 0L) {
            target.writeText(header + "\n")
        }
        target.appendText(row + "\n")
        true
    }.getOrDefault(false)

    /** 表头与文件不符时先处理，返回应当写入的目标文件。 */
    private fun rotateIfNeeded(context: Context, fileName: String, header: String): File {
        val target = File(directory(context), fileName)
        val existing = if (target.exists() && target.length() > 0L) {
            runCatching { target.bufferedReader().use { it.readLine() } }.getOrNull()
        } else {
            null
        }
        if (!needsRotation(existing, header)) return target

        if (canPadToMatch(existing, header)) {
            // 只是加了列：就地补齐每行的空值，换掉表头，**仍然只有一个文件**。
            migrateInPlace(target, header)
        } else {
            // 列的顺序或含义变了：旧行无法安全对齐，只能另存归档
            val archived = File(directory(context), "$fileName.${System.currentTimeMillis()}.old")
            target.renameTo(archived)
        }
        return target
    }

    /**
     * 就地迁移：给每一行补足空值并换成新表头。
     *
     * 先写临时文件再原子改名——中途断电也不会留下半截文件（那才是最坏的结果：
     * 文件还在、但内容被截断，而且你看不出来）。
     */
    private fun migrateInPlace(target: File, header: String) {
        val columns = header.split(",").size
        val temp = File(target.parentFile, target.name + ".migrating")
        temp.bufferedWriter().use { out ->
            out.write(header)
            out.newLine()
            target.bufferedReader().useLines { lines ->
                lines.drop(1).forEach { line ->
                    if (line.isBlank()) return@forEach
                    val cells = line.split(",")
                    val fixed = if (cells.size < columns) {
                        cells + List(columns - cells.size) { "" }
                    } else {
                        cells
                    }
                    out.write(fixed.joinToString(","))
                    out.newLine()
                }
            }
        }
        target.delete()
        temp.renameTo(target)
    }

    /**
     * 批量追加（逐小时数据一次 24 行）。
     * 逐行调用 [append] 会开 24 次文件——手表上存储慢，批一次写完更稳。
     */
    fun appendRows(context: Context, fileName: String, header: String, rows: List<String>): Boolean =
        runCatching {
            if (rows.isEmpty()) return true
            val target = rotateIfNeeded(context, fileName, header)
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
