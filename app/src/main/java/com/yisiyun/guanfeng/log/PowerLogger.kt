package com.yisiyun.guanfeng.log

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Process
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 耗电自记录。
 *
 * ## 为什么记这个
 *
 * 主人的需求很明确：手表的续航本来就短，**必须能判断是不是我们这个应用造成的**。
 * 而第三方的应用**没有任何接口能读到"自己耗了多少 mAh"**——那是系统权限
 * （BatteryStats 需要 privileged 权限）。所以这里采取能做的那部分：
 *
 *   · 定时记录**电量百分比**与是否在充电 → 可以画出"应用运行期间电量斜率"；
 *   · 记录**本进程累计 CPU 时间**→ 前后差值就是这段时间我们真正吃掉的 CPU；
 *   · 记录应用已运行时长 → 便于折算"每小时耗电"。
 *
 * ## 要拿到权威数字，用这条命令（写进 README）
 *
 * ```
 * adb shell dumpsys batterystats --charged com.yisiyun.guanfeng
 * ```
 * 系统会给出该应用的**估算耗电（mAh 或百分比）**，那才是可用于归因的数字。
 * 本文件负责提供辅助证据：如果我们 CPU 时间很低、电量却在掉，
 * 那掉的电就不是我们吃的。
 */
object PowerLogger {

    private const val FILE_NAME = "power.csv"

    const val HEADER =
        "timestamp_ms,clock,app_elapsed_min,cpu_time_ms,battery_pct,charging," +
            "temperature_c,samples_logged"

    private fun file(context: Context): File {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        return File(directory, FILE_NAME)
    }

    fun append(
        context: Context,
        appElapsedMs: Long,
        samplesLogged: Int,
    ): Boolean = runCatching {
        val target = file(context)
        if (!target.exists()) target.writeText(HEADER + "\n")

        val battery = readBattery(context)
        val line = listOf(
            System.currentTimeMillis().toString(),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()),
            csvNum(appElapsedMs / 60000.0, 1),
            Process.getElapsedCpuTime().toString(),
            battery?.first?.toString() ?: "",
            battery?.second?.toString() ?: "",
            csvNum(battery?.third, 1),
            samplesLogged.toString(),
        ).joinToString(",")
        target.appendText(line + "\n")
        true
    }.getOrDefault(false)

    /** 返回 (电量百分比, 是否充电, 电池温度℃)。 */
    private fun readBattery(context: Context): Triple<Int, Int, Float>? = runCatching {
        val intent: Intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
        ) ?: return null
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return null
        val percent = level * 100 / scale
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val charging = if (
            status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
        ) 1 else 0
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
        Triple(percent, charging, temperature)
    }.getOrNull()!!
}
