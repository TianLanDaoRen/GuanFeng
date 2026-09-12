package com.yisiyun.guanfeng.log

import android.content.Context
import com.yisiyun.guanfeng.core.CATEGORY_COMFORT
import com.yisiyun.guanfeng.core.CATEGORY_SYMPTOM
import com.yisiyun.guanfeng.core.CheckInRecord
import java.io.File

/**
 * 体感打卡的读取。与 [CsvSessionLogger] 共用同一个 external files 目录，
 * 因此一次 `adb pull` 就能把手表上的全部数据取走。
 */
object CheckInHistory {

    private const val FILE_NAME = "checkins.csv"

    /** 纯解析：列位置由表头决定，坏行跳过——不让一行脏数据毁掉整张关联图。 */
    fun parse(lines: List<String>): List<CheckInRecord> {
        if (lines.isEmpty()) return emptyList()
        val header = lines.first().split(',')
        val iTimestamp = header.indexOf("timestamp_ms")
        val iPressure = header.indexOf("pressure_hpa")
        val iWeather = header.indexOf("weather_pressure_hpa")
        val iTags = header.indexOf("tags")
        val iIntensity = header.indexOf("intensity")
        val iNote = header.indexOf("note")
        val iCategory = header.indexOf("category")
        if (iTimestamp < 0) return emptyList()

        val result = ArrayList<CheckInRecord>()
        for (line in lines.drop(1)) {
            if (line.isBlank()) continue
            val cells = line.split(',')
            val timestamp = cells.getOrNull(iTimestamp)?.toLongOrNull() ?: continue
            result += CheckInRecord(
                timestampMs = timestamp,
                tags = cells.getOrNull(iTags) ?: "",
                intensity = cells.getOrNull(iIntensity) ?: "",
                pressureHpa = if (iPressure >= 0) cells.getOrNull(iPressure)?.toFloatOrNull() else null,
                note = cells.getOrNull(iNote) ?: "",
                weatherPressureHpa = if (iWeather >= 0) {
                    cells.getOrNull(iWeather)?.toFloatOrNull()
                } else {
                    null
                },
                // 缺列或空值 → 症状。旧文件没有这一列，而那时这一页只记不适。
                category = if (iCategory >= 0) {
                    cells.getOrNull(iCategory)?.takeIf { it == CATEGORY_COMFORT }
                        ?: CATEGORY_SYMPTOM
                } else {
                    CATEGORY_SYMPTOM
                },
            )
        }
        return result
    }

    /** 读取 [days] 天内的打卡记录（按时间升序）。 */
    fun loadRecent(context: Context, days: Int, nowMs: Long): List<CheckInRecord> {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        val file = File(directory, FILE_NAME)
        if (!file.isFile) return emptyList()
        val cutoff = nowMs - days.toLong() * 24 * 60 * 60 * 1000
        return runCatching {
            parse(file.readLines())
                .filter { it.timestampMs >= cutoff }
                .sortedBy { it.timestampMs }
        }.getOrElse { emptyList() }
    }
}
