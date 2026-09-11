package com.yisiyun.guanfeng.log

import com.yisiyun.guanfeng.core.PressureSample
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 历史会话读取的单测：它决定「3 小时窗口能否在服务重启后接着算」，
 * 而一旦接错（尤其是跨断档拼接），会算出根本不存在的趋势——所以两条规则都要钉住。
 */
class SessionHistoryTest {

    private val header =
        "timestamp_ms,clock,pressure_hpa,vertical_accel,steps,rate_hpa_per_hour," +
            "delta_hpa_3h,grade,weather_samples,elevation_events,elevation_meters," +
            "r_squared,window_minutes,coverage_pct,confidence," +
            "resting_heart_rate_bpm,light_delta_10min"

    private fun row(ts: Long, pressure: Float, accel: Float = 0.2f, steps: Int = 0) =
        "$ts,x,%.2f,%.2f,$steps,0,0,平稳,1,0,0,0,0,0,可信,,".format(pressure, accel)

    @Test
    fun `解析_按表头定位列而不是写死下标`() {
        val lines = listOf(header, row(1000L, 1001.5f, 0.4f, 3), row(2000L, 1002.5f))

        val samples = SessionHistory.parse(lines)

        assertEquals(2, samples.size)
        assertEquals(1000L, samples[0].timestampMs)
        assertEquals(1001.5f, samples[0].pressureHpa, 1e-6f)
        assertEquals(0.4f, samples[0].verticalAccel, 1e-6f)
        assertEquals(3, samples[0].stepsInWindow)
        assertEquals(1002.5f, samples[1].pressureHpa, 1e-6f)
    }

    @Test
    fun `解析_坏行被跳过而不是毁掉整次恢复`() {
        val lines = listOf(
            header,
            row(1000L, 1001.0f),
            "这不是一行CSV",
            "1700000002000,x,",              // 列数不足
            "abc,x,1002.0,0.2,0",            // 时间戳非法
            row(3000L, 1003.0f),
        )

        val samples = SessionHistory.parse(lines)

        assertEquals("只有两行是合法数据", 2, samples.size)
    }

    @Test
    fun `解析_缺少可选列时也能读_加速度与步数取零`() {
        val shortHeader = "timestamp_ms,clock,pressure_hpa"
        val lines = listOf(shortHeader, "1000,x,1001.0")

        val samples = SessionHistory.parse(lines)

        assertEquals(1, samples.size)
        assertEquals(0f, samples[0].verticalAccel, 1e-6f)
        assertEquals(0, samples[0].stepsInWindow)
    }

    @Test
    fun `回填_超出窗口的旧样本被丢并保持时间升序`() {
        // 用真实采样节奏造数据（15 秒一个样本，连续 4 小时），
        // 否则样本间隔本身就会超过断档阈值、被设计正确地截断，测不到窗口裁剪。
        val now = 10_000_000_000L
        val step = 15_000L
        val hours4 = 4 * 60 * 60 * 1000L
        val total = (hours4 / step).toInt()
        val samples = (0 until total).map { index ->
            PressureSample(now - hours4 + index * step, 1000f + index * 0.001f)
        }

        val tail = SessionHistory.tailWithoutGaps(samples, now, 3 * 60 * 60 * 1000L, 5 * 60 * 1000L)

        assertEquals("3 小时 ÷ 15 秒 = 720 个样本", 720, tail.size)
        assertTrue("不得包含超窗样本", tail.first().timestampMs >= now - 3 * 60 * 60 * 1000L)
        assertTrue("必须按时间升序返回", tail[0].timestampMs < tail[1].timestampMs)
    }

    @Test
    fun `回填_遇到断档就停_绝不跨空洞拼接`() {
        val now = 10_000_000L
        val samples = listOf(
            PressureSample(now - 120 * 60 * 1000L, 1000f), // 断档之前
            PressureSample(now - 119 * 60 * 1000L, 1000.5f),
            PressureSample(now - 30 * 60 * 1000L, 1002f),  // 中间空了 89 分钟
            PressureSample(now - 29 * 60 * 1000L, 1002.5f),
        )

        val tail = SessionHistory.tailWithoutGaps(samples, now, 3 * 60 * 60 * 1000L, 5 * 60 * 1000L)

        assertEquals("只保留断档之后的连续段", 2, tail.size)
        assertEquals(1002f, tail[0].pressureHpa, 1e-6f)
    }

    @Test
    fun `回填_输入无序时先排序再取`() {
        val now = 10_000_000L
        val samples = listOf(
            PressureSample(now - 29 * 60 * 1000L, 1002.5f),
            PressureSample(now - 30 * 60 * 1000L, 1002f),
        )

        val tail = SessionHistory.tailWithoutGaps(samples, now, 3 * 60 * 60 * 1000L, 5 * 60 * 1000L)

        assertEquals(2, tail.size)
        assertEquals(1002f, tail[0].pressureHpa, 1e-6f)
    }

    // ---- 原始文件保留策略 ----

    @Test
    fun `保留最近 N 个文件_更旧的删掉`() {
        // 修改时间按降序传入（与调用方一致）
        val times = (0 until 50).map { 1_000_000L - it * 1000L }

        val doomed = SessionHistory.selectForDeletion(times, keepFiles = 30, keepSinceMs = 0L)

        assertEquals(20, doomed.size)
        assertTrue("删的必须是最旧的 20 个", doomed.all { it >= 30 })
    }

    @Test
    fun `个数不等于时间跨度_时间保护窗内的文件一律不删`() {
        // 模拟"重启极频繁"：40 个文件全挤在最近两小时内
        val now = 2_000_000_000L
        val times = (0 until 40).map { now - it * 60_000L }

        val doomed = SessionHistory.selectForDeletion(
            modifiedTimes = times,
            keepFiles = 30,
            keepSinceMs = now - 6L * 60 * 60 * 1000,
        )

        assertTrue("全在保护窗内，一个都不该删", doomed.isEmpty())
    }

    @Test
    fun `保护窗与个数规则取并集`() {
        val now = 2_000_000_000L
        // 前 35 个是旧文件（30 分钟前以前），后 5 个是刚才的
        val times = (0 until 35).map { now - 10L * 60 * 60 * 1000 - it * 60_000L } +
            (0 until 5).map { now - it * 60_000L }

        val doomed = SessionHistory.selectForDeletion(
            modifiedTimes = times,
            keepFiles = 30,
            keepSinceMs = now - 6L * 60 * 60 * 1000,
        )

        assertEquals("前 30 个受个数保护、后 5 个受时间保护，删中间那 5 个", 5, doomed.size)
        assertTrue(doomed.all { it in 30..34 })
    }
}
