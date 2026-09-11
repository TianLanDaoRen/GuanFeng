package com.yisiyun.guanfeng.log

import android.content.Context
import com.yisiyun.guanfeng.core.HourlyRow
import java.io.File

/**
 * 小时级长期归档。
 *
 * ## 为什么要有这个文件（它解决的问题）
 *
 * 原始会话 CSV 有两个毛病：一是**碎片化**（每次应用/服务重启就新开一个文件，
 * 实测 11 小时产生了 14 个），二是**体积**（5 秒采样约 2 MB/天）。
 * 于是「保留多少原始数据」与「能不能看长期历史」被绑死在一起：
 * 删文件就没历史，留历史就吃满 ROM。
 *
 * 拆开的办法：**小时归档只留汇总，永不删除**（约 1 KB/天，一年 400 KB），
 * 原始文件则可以按主人的建议只留最近 30 个。这样：
 *   · 长期趋势（关联视图、AI 报告）读归档，永不受裁剪影响；
 *   · 3 小时趋势窗口仍读原始样本，保留全部细节；
 *   · **没有任何数据被重复存放**——归档是汇总，原始是明细，各司其职。
 *
 * 特别注意：**不要把旧文件里的数据"提取后抄进新会话文件"**。
 * 那会让同一批样本在分析时被算两遍（例如当天的气压落差被计入两次），
 * 而且很难事后发现。跨文件读取才是正确做法。
 */
object HourlyArchive {

    private const val FILE_NAME = "hourly.csv"

    const val HEADER =
        "hour_start_ms,weather_avg,weather_min,weather_max,raw_avg,samples," +
            "hr_avg,resting_hr,wrist_temp_avg,wrist_temp_min,wrist_temp_max," +
            "light_avg,light_min"

    private fun file(context: Context): File {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        return File(directory, FILE_NAME)
    }

    /** 追加一行（只用于**已完成**的小时）。 */
    fun append(context: Context, row: HourlyRow): Boolean = runCatching {
        val target = file(context)
        if (!target.exists()) target.writeText(HEADER + "\n")
        target.appendText(toLine(row) + "\n")
        true
    }.getOrDefault(false)

    /**
     * 读出全部归档行。
     *
     * 按整点去重：应用重启时会把"当前不完整的小时"也落盘，若同一小时被两次落盘
     * （例如整点前后各重启一次），就去重保留样本数更多的那一行——
     * 否则同一天会被算两遍，图表上还会出现重叠点。
     */
    fun loadAll(context: Context): List<HourlyRow> {
        val target = file(context)
        if (!target.isFile) return emptyList()
        return runCatching { parse(target.readLines()) }.getOrElse { emptyList() }
    }

    /** 同一整点只保留样本数最多的一行（跨重启的重复落盘兜底）。 */
    fun dedupeByHour(rows: List<HourlyRow>): List<HourlyRow> = rows
        .groupBy { it.hourStartMs }
        .map { (_, group) -> group.maxBy { it.samples } }
        .sortedBy { it.hourStartMs }

    /**
     * 一次性迁移：用已有的原始会话文件把归档补齐。
     *
     * 旧文件里**只有气压**（没有逐点心率与腕温），所以体感字段一律留空——
     * 宁可在报告里显示"该时段无数据"，也不要拿 0 冒充。
     */
    fun seed(
        context: Context,
        buckets: List<com.yisiyun.guanfeng.core.HourlyBucket>,
    ): Int {
        if (buckets.isEmpty()) return 0
        val target = file(context)
        if (target.exists() && target.length() > 0L) return 0
        return runCatching {
            target.writeText(HEADER + "\n")
            buckets.forEach { bucket ->
                target.appendText(
                    toLine(
                        HourlyRow(
                            hourStartMs = bucket.hourStartMs,
                            weatherAvgHpa = bucket.avgHpa,
                            weatherMinHpa = bucket.minHpa,
                            weatherMaxHpa = bucket.maxHpa,
                            rawAvgHpa = bucket.avgHpa,
                            samples = bucket.sampleCount,
                            heartRateAvg = null,
                            restingHeartRate = null,
                            wristTempAvg = null,
                            wristTempMin = null,
                            wristTempMax = null,
                            lightAvgLux = null,
                            lightMinLux = null,
                        )
                    ) + "\n"
                )
            }
            buckets.size
        }.getOrDefault(0)
    }

    /** 纯解析：按表头定位列，坏行跳过——不让一行脏数据毁掉整段历史。 */
    fun parse(lines: List<String>): List<HourlyRow> {
        if (lines.isEmpty()) return emptyList()
        val header = lines.first().split(',')
        fun index(name: String) = header.indexOf(name)
        val iStart = index("hour_start_ms")
        if (iStart < 0) return emptyList()

        val result = ArrayList<HourlyRow>(lines.size)
        for (line in lines.drop(1)) {
            if (line.isBlank()) continue
            val cells = line.split(',')
            fun text(name: String): String? =
                index(name).takeIf { it >= 0 }?.let { cells.getOrNull(it) }
            fun number(name: String): Float? = text(name)?.toFloatOrNull()
            val start = cells.getOrNull(iStart)?.toLongOrNull() ?: continue
            result += HourlyRow(
                hourStartMs = start,
                weatherAvgHpa = number("weather_avg") ?: continue,
                weatherMinHpa = number("weather_min") ?: continue,
                weatherMaxHpa = number("weather_max") ?: continue,
                rawAvgHpa = number("raw_avg") ?: continue,
                samples = text("samples")?.toIntOrNull() ?: 0,
                heartRateAvg = number("hr_avg"),
                restingHeartRate = number("resting_hr"),
                wristTempAvg = number("wrist_temp_avg"),
                wristTempMin = number("wrist_temp_min"),
                wristTempMax = number("wrist_temp_max"),
                lightAvgLux = number("light_avg"),
                lightMinLux = number("light_min"),
            )
        }
        return result.sortedBy { it.hourStartMs }
    }

    private fun toLine(row: HourlyRow): String = listOf(
        row.hourStartMs.toString(),
        "%.2f".format(row.weatherAvgHpa),
        "%.2f".format(row.weatherMinHpa),
        "%.2f".format(row.weatherMaxHpa),
        "%.2f".format(row.rawAvgHpa),
        row.samples.toString(),
        row.heartRateAvg?.let { "%.0f".format(it) } ?: "",
        row.restingHeartRate?.let { "%.0f".format(it) } ?: "",
        row.wristTempAvg?.let { "%.2f".format(it) } ?: "",
        row.wristTempMin?.let { "%.2f".format(it) } ?: "",
        row.wristTempMax?.let { "%.2f".format(it) } ?: "",
        row.lightAvgLux?.let { "%.0f".format(it) } ?: "",
        row.lightMinLux?.let { "%.0f".format(it) } ?: "",
    ).joinToString(",")
}
