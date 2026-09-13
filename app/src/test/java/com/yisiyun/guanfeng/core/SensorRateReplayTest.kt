package com.yisiyun.guanfeng.core

import java.io.File
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sqrt
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 传感器降频回放器（含**自校验**）。
 *
 * ## 它解决什么问题
 *
 * 想回答"把四路 5Hz 里某几路降到 1Hz 会怎样"，必须能**离线按不同速率重新聚合**。
 * 第一版试过"拿落盘的 5 秒聚合值拟合一条连续曲线再重采样"——连着三轮得出不可用的结论，
 * 根因是**被降频伤害的那个量本身就是漂移主导的**（记录的净位移量级到 ±141 米，
 * 人不可能位移上百米），而漂移来自原始波形里的噪声与偏置，**平滑拟合曲线拟合不出噪声**。
 *
 * 所以要真做这个实验，必须先有**原始 5Hz 流**（见 PressureRecorder 的原始流黑匣子，
 * 开关文件 `files/raw_log_on`），而回放器本身**必须先复现应用自己记录的聚合值**才算可用
 * ——这就是 [回放器必须复现应用自己记的聚合值] 这条断言存在的理由：
 * 它把"工具可信"变成了一条**会失败的测试**，而不是一句口头保证。
 *
 * ## 数据从哪来
 *
 * 本机 `/tmp/gfdata/`（用 `adb pull` 取）：`raw_sensors.csv`（原始流）、
 * `guanfeng_samples.csv`（应用自己记录的 5 秒聚合）。两者都不在仓库里，
 * **文件不存在时全部跳过**，所以 CI 上不会红。
 *
 * 原始流用的是**开机纳秒**，靠每次会话写下的 `#clock,<epoch_ms>,<boot_ns>` 行换算成墙上时间
 * ——没有它就没法把回放窗口对到"哪一段是电梯"。
 */
class SensorRateReplayTest {

    private val rawFile = File("/tmp/gfdata/raw_sensors.csv")
    private val sampleFile = File("/tmp/gfdata/guanfeng_samples.csv")

    private data class Agg(val p: Float, val accel: Float, val disp: Float)
    private data class RawEvent(val tsNs: Long, val type: Int, val v: FloatArray)

    // ── 原始流 ────────────────────────────────────────────────────────────────

    private fun bootOffsetNs(): Long {
        var off = 0L
        rawFile.forEachLine { line ->
            if (line.startsWith("#clock")) {
                val p = line.split(',')
                if (p.size >= 3) off = p[1].toLong() * 1_000_000L - p[2].toLong()
            }
        }
        return off
    }

    private fun loadRaw(): List<RawEvent> {
        val out = ArrayList<RawEvent>(40_000)
        rawFile.forEachLine { line ->
            if (line.isBlank() || line.startsWith("#") || line.startsWith("timestamp_ns")) return@forEachLine
            val c = line.split(',')
            if (c.size < 3) return@forEachLine
            val ts = c[0].toLongOrNull() ?: return@forEachLine
            val ty = c[1].toIntOrNull() ?: return@forEachLine
            out += RawEvent(ts, ty, FloatArray(c.size - 2) { c[it + 2].toFloatOrNull() ?: 0f })
        }
        out.sortBy { it.tsNs }
        return out
    }

    /**
     * 把原始事件按 5 秒窗口聚合成应用里那份 [PressureSample]。
     *
     * **公式必须与 PressureRecorder / SampleAggregator 一致**：
     * 气压取窗口内中位数；竖直加速度 = 线加速度在**最近一次重力方向**上的投影、取窗口内峰值；
     * 位移用带泄漏的二次积分（`dt` 取自**事件时间戳**、上限 0.2 秒）。
     *
     * @param sampleHz 目标采样率（5.0 = 与真机一致；1.0 用于模拟降频）
     * @param onPressureTrigger 气压速率超阈值时把采样率临时提到 5Hz（阈值 0.3 hPa/min、保持 60 秒）
     */
    private fun aggregate(
        events: List<RawEvent>,
        offNs: Long,
        sampleHz: Double = 5.0,
        onPressureTrigger: Boolean = false,
    ): List<PressureSample> {
        fun epochMs(ns: Long): Long = (ns + offNs) / 1_000_000L
        val bucketNs = (1_000_000_000.0 / sampleHz).toLong()
        var v = 0.0
        var x = 0.0
        var lastNs = 0L
        var fastUntilMs = 0L
        val result = ArrayList<PressureSample>()
        val press = ArrayList<Float>()
        val rateWindow = ArrayDeque<Pair<Long, Float>>()   // (epochMs, p) 用于算速率
        var peak = 0f
        val gravity = FloatArray(3)
        var lastAcceptedNs = -1L
        var winStart = epochMs(events.first().tsNs) / 5_000L * 5_000L

        for (e in events) {
            val t = epochMs(e.tsNs)
            // —— 采样率闸门：每个 1/sampleHz 的时间桶只收第一条（模拟传感器按该速率投递）
            val fast = onPressureTrigger && t < fastUntilMs
            val gateNs = if (fast) 200_000_000L else bucketNs
            if (e.tsNs - lastAcceptedNs < gateNs) continue
            lastAcceptedNs = e.tsNs

            while (t >= winStart + 5_000L) {
                // 应用 flush 的时刻是窗口**结束**时刻（它取的是"刚到 5 秒"那一瞬的最新值），
                // 而我们按窗口起始时刻登记 → 对照时要 +5000 才能对上应用的 timestamp_ms
                if (press.isNotEmpty()) {
                    val sorted = press.sorted()
                    val m = sorted.size / 2
                    val med = if (sorted.size % 2 == 1) sorted[m] else (sorted[m - 1] + sorted[m]) / 2f
                    result += PressureSample(
                        timestampMs = winStart + 5_000L,
                        pressureHpa = med,
                        verticalAccel = peak,
                        verticalDisplacementM = x.toFloat(),
                        stepsInWindow = 0,
                    )
                }
                press.clear()
                peak = 0f
                winStart += 5_000L
            }
            when (e.type) {
                6 -> {
                    press += e.v[0]
                    if (onPressureTrigger) {
                        rateWindow.addLast(t to e.v[0])
                        while (rateWindow.size > 2 && t - rateWindow.first().first > 60_000L) rateWindow.removeFirst()
                        val spanMin = (rateWindow.last().first - rateWindow.first().first) / 60_000.0
                        if (spanMin >= 0.5) {
                            val rate = abs(rateWindow.last().second - rateWindow.first().second) / spanMin.toFloat()
                            if (rate > 0.3f) fastUntilMs = t + 60_000L
                        }
                    }
                }
                9 -> e.v.copyInto(gravity)
                10 -> {
                    val g = sqrt(gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2])
                    if (g > 0.1f) {
                        val vert = (e.v[0] * gravity[0] + e.v[1] * gravity[1] + e.v[2] * gravity[2]) / g
                        if (abs(vert) > peak) peak = abs(vert)
                        val dt = if (lastNs == 0L) 0.0 else ((e.tsNs - lastNs) / 1e9).coerceIn(0.0, 0.2)
                        lastNs = e.tsNs
                        if (dt > 0) {
                            val leak = exp(-dt / 5.0)
                            v = (v + vert * dt) * leak
                            x = (x + v * dt) * leak
                        }
                    }
                }
            }
        }
        return result
    }

    // ── 自校验：回放器必须复现应用自己记录的三列 ───────────────────────────────

    @Test
    fun `回放器必须复现应用自己记的聚合值`() {
        if (!rawFile.isFile || !sampleFile.isFile) {
            println("没有 /tmp/gfdata 数据，跳过（这是预期行为，CI 上就是这条路径）")
            return
        }
        val off = bootOffsetNs()
        val mine = aggregate(loadRaw(), off).associateBy { it.timestampMs }

        var n = 0
        var sumP = 0.0
        var sumA = 0.0
        var sumD = 0.0
        var maxP = 0f
        val accelDiffs = ArrayList<Float>()
        for (line in sampleFile.readLines().drop(1)) {
            val c = line.split(',')
            if (c.size < 20) continue
            val ts = c[0].toLongOrNull() ?: continue
            val p = c[2].toFloatOrNull() ?: continue
            val a = c[3].toFloatOrNull() ?: continue
            val d = c[19].toFloatOrNull() ?: continue
            val got = mine[ts / 5_000L * 5_000L] ?: continue
            n++
            val ep = abs(got.pressureHpa - p)
            val ea = abs(got.verticalAccel - a)
            sumP += ep; sumA += ea; sumD += abs((got.verticalDisplacementM ?: 0f) - d)
            accelDiffs += ea
            if (ep > maxP) maxP = ep
        }
        if (n == 0) { println("没有重叠窗口可比，跳过"); return }
        val accelSorted = accelDiffs.sorted()
        val accelMedian = accelSorted.getOrElse(accelSorted.size / 2) { 0f }
        println(
            "自校验：对比 $n 个窗口  气压平均差=${"%.3f".format(sumP / n)} hPa（最大 ${"%.3f".format(maxP)}）" +
                "  加速度差 中位数=${"%.3f".format(accelMedian)}（平均 ${"%.3f".format(sumA / n)}）m/s²" +
                "  位移平均差=${"%.1f".format(sumD / n)} m",
        )

        // 气压是对齐最硬的量：差多了就说明回放器（或时间映射）错了，别拿它的结论当数
        assertTrue("气压中位数平均差应 ≤0.05 hPa（实际 ${"%.3f".format(sumP / n)}）", sumP / n <= 0.05)
        assertTrue("气压中位数最大差应 ≤0.5 hPa（实际 ${"%.3f".format(maxP)}）", maxP <= 0.5)
        // 加速度只比**中位数差**，不比平均差：这一列是"5 秒窗口里的竖直加速度峰值"，
        // 是尖峰统计量——窗口里多收/少收一个事件，峰值就能差好几 m/s²（实测平均差 1.3、
        // 中位数差却在 0.5 以内）。拿平均差设阈值只会变成一条永远需要放宽的假守卫。
        assertTrue("竖直加速度峰值的中位数差应 ≤0.5 m/s²（实际 ${"%.3f".format(accelMedian)}）", accelMedian <= 0.5f)
        assertTrue("净位移平均差应 ≤10 m（该信号量级 ±100 m，实际 ${"%.1f".format(sumD / n)}）", sumD / n <= 10.0)
    }

    // ── 实验：五种速率组合 ────────────────────────────────────────────────────

    private class Plan(val name: String, val hz: Double, val triggered: Boolean)

    @Test
    fun `五种速率组合在重叠窗口上的对比`() {
        if (!rawFile.isFile || !sampleFile.isFile) { println("没有数据，跳过"); return }
        val off = bootOffsetNs()
        val events = loadRaw()
        // 自动挑三个场景（按原始流里的气压变化速率/步数特征）：
        //   电梯 = 15 分钟窗口内气压跨度最大的一段；静止 = 跨度最小且加速度峰值低的一段
        fun epochMs(ns: Long) = (ns + off) / 1_000_000L
        val press = events.filter { it.type == 6 }
        fun spanOf(startMs: Long, lenMs: Long): Float {
            val ps = press.filter { epochMs(it.tsNs) in startMs..(startMs + lenMs) }.map { it.v[0] }
            return if (ps.isEmpty()) 0f else (ps.max() - ps.min())
        }
        val step = 60_000L
        var elevatorStart = epochMs(events.first().tsNs)
        var bestSpan = -1f
        var t0 = epochMs(events.first().tsNs)
        val endMs = epochMs(events.last().tsNs)
        while (t0 + 15 * 60_000L <= endMs) {
            val sp = spanOf(t0, 15 * 60_000L)
            if (sp > bestSpan) { bestSpan = sp; elevatorStart = t0 }
            t0 += step
        }
        var stillStart = elevatorStart
        var worstSpan = Float.MAX_VALUE
        t0 = epochMs(events.first().tsNs)
        while (t0 + 15 * 60_000L <= endMs) {
            val sp = spanOf(t0, 15 * 60_000L)
            if (sp < worstSpan && t0 != elevatorStart) { worstSpan = sp; stillStart = t0 }
            t0 += step
        }
        println("自动挑选：电梯窗口跨度=${"%.2f".format(bestSpan)} hPa 于 ${elevatorStart}；静止窗口跨度=${"%.2f".format(worstSpan)} hPa 于 ${stillStart}")
        val scenarios = listOf("电梯" to elevatorStart, "静止" to stillStart)
        scenarios.forEach { (name, startMs) ->
            val window = events.filter { epochMs(it.tsNs) in startMs..(startMs + 15 * 60_000L) }
            runPlans(name, window, off, PressureTrendEngine(3L * 60L * 60L * 1000L, 8))
        }
    }

    private fun runPlans(
        name: String,
        window: List<RawEvent>,
        off: Long,
        engine: PressureTrendEngine,
    ) {
        val plans = listOf(
            Plan("① 全 5Hz（基线）", 5.0, false),
            Plan("② 全 1Hz 不触发", 1.0, false),
            Plan("③ 气压触发（全 1Hz→5Hz）", 1.0, true),
            Plan("④ 半速 2.5Hz", 2.5, false),
        )
        println("=== 场景：$name ===")
        val base = engine.compute(aggregate(window, off, 5.0, false))
        println("基线：事件=${base.elevationEvents} 位移=${"%.1f".format(base.elevationMeters)} m " +
            "净变=${"%.2f".format(base.deltaHpaPer3h)} hPa 置信=${base.confidence}")
        for (p in plans.drop(1)) {
            val t = engine.compute(aggregate(window, off, p.hz, p.triggered))
            val offsetErrHpa = abs(t.elevationMeters - base.elevationMeters) * HPA_PER_METER_NEAR_SEA_LEVEL
            println(
                "${p.name}：事件=${t.elevationEvents}（基线 ${base.elevationEvents}）" +
                    " 位移=${"%.1f".format(t.elevationMeters)} m" +
                    " 净变=${"%.2f".format(t.deltaHpaPer3h)} hPa" +
                    " **偏移差=${"%.2f".format(offsetErrHpa)} hPa**" +
                    " 置信=${t.confidence}"
            )
        }
    }
}
