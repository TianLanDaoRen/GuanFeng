package com.yisiyun.guanfeng.log

import android.content.Context
import com.yisiyun.guanfeng.core.RainLikelihood
import com.yisiyun.guanfeng.core.TrendResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 人工上报的天气实况。刮风也算：它是"实际发生了什么"的一部分，校准需要真值。 */
enum class WeatherObservation(val label: String) {
    RAIN("下雨"),
    WIND("起风"),
    CLEAR("晴"),
    CLOUDY("阴"),
}

/**
 * 天气实况记录 —— **校准唯一的数据来源**。
 *
 * ## 为什么必须在记录实况的同时把当时的判断也写进去
 *
 * 「未校准」是这个项目当前最大的可信度缺口：所有阈值都有文献出处，
 * 但没有一条经过本地数据验证。要校准必须有"预测 vs 实际"的配对，
 * 所以这一行**不是只写"下雨了"**，而是把那一刻我们自己算出来的
 * 倾向、速率、变压一并落盘。之后回看就能直接统计：
 *
 *   · 报「高」的时候，实际有多少次真的下了雨？（命中率）
 *   · 下了雨的时候，我们之前报的是几？（漏报率）
 *
 * 如果只记"下雨了"，事后还得去别的时间线里对齐，既麻烦又容易对错。
 *
 * ## 关于延迟
 *
 * 主人说"下雨时人工上报可能有延迟，但无伤大雅"——对，因为我们记录的是
 * **上报那一刻的预测状态**，而不是"雨开始那一刻"的。统计时按 ±3 小时的
 * 窗口匹配即可，延迟几十分钟不影响判断。
 */
object WeatherObservationLogger {

    private const val FILE_NAME = "weather_observed.csv"

    const val HEADER =
        "timestamp_ms,clock,observation,weather_pressure_hpa,rate_hpa_per_hour," +
            "delta_hpa_3h,grade,likelihood,confidence"

    private fun file(context: Context): File {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        return File(directory, FILE_NAME)
    }

    fun append(
        context: Context,
        observation: WeatherObservation,
        weatherPressureHpa: Float?,
        trend: TrendResult?,
        likelihood: RainLikelihood,
    ): Boolean = runCatching {
        val target = file(context)
        if (!target.exists()) target.writeText(HEADER + "\n")
        val now = System.currentTimeMillis()
        val line = listOf(
            now.toString(),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(now)),
            observation.label,
            weatherPressureHpa?.let { "%.2f".format(it) } ?: "",
            trend?.rateHpaPerHour?.let { "%.3f".format(it) } ?: "",
            trend?.deltaHpaPer3h?.let { "%.2f".format(it) } ?: "",
            trend?.grade?.label ?: "",
            likelihood.name,
            trend?.confidence?.label ?: "",
        ).joinToString(",")
        target.appendText(line + "\n")
        true
    }.getOrDefault(false)

    /** 已有的实况记录，新到旧。回看用。 */
    fun loadAll(context: Context): List<String> = runCatching {
        val target = file(context)
        if (!target.isFile) emptyList() else target.readLines().drop(1).filter { it.isNotBlank() }
    }.getOrDefault(emptyList())

    fun countToday(context: Context): Int = runCatching {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        loadAll(context).count { line -> line.split(",").getOrNull(1)?.startsWith(today) == true }
    }.getOrDefault(0)
}
