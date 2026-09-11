package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

/**
 * 观风算法内核的 JVM 单测：不依赖手表、不依赖 Android，
 * 直接在开发机上证明「天气分量」与「高度分量」能被正确拆开。
 */
class PressureTrendEngineTest {

    private val minuteMs = 60_000L
    private val startMs = 1_700_000_000_000L

    /** 造一段纯天气序列：按给定 hPa/h 匀速变化，1 分钟一个样本。 */
    private fun weatherSeries(
        minutes: Int,
        hPaPerHour: Float = 0f,
        startHpa: Float = 1000f,
        verticalAccel: Float = 0f,
        stepsPerWindow: Int = 0
    ): List<PressureSample> = (0 until minutes).map { index ->
        PressureSample(
            timestampMs = startMs + index * minuteMs,
            pressureHpa = startHpa + (hPaPerHour / 60f) * index,
            verticalAccel = verticalAccel,
            stepsInWindow = stepsPerWindow
        )
    }

    @Test
    fun `气压平稳时判定为平稳`() {
        val result = PressureTrendEngine().compute(weatherSeries(180))

        assertEquals(TrendGrade.STEADY, result.grade)
        assertEquals(0f, result.rateHpaPerHour, 0.05f)
        assertEquals(0, result.elevationEvents)
        assertEquals(180, result.weatherSamples)
    }

    @Test
    fun `气压缓降时判定为缓降并给出正确速率`() {
        val result = PressureTrendEngine().compute(weatherSeries(180, hPaPerHour = -1.2f))

        assertEquals(TrendGrade.FALLING, result.grade)
        assertEquals(-1.2f, result.rateHpaPerHour, 0.05f)
        assertEquals(-3.6f, result.deltaHpaPer3h, 0.2f)
        assertEquals(0, result.elevationEvents)
    }

    @Test
    fun `急降映射为高降水倾向且明确标记未校准`() {
        val result = PressureTrendEngine().compute(weatherSeries(180, hPaPerHour = -2.5f))

        assertEquals(TrendGrade.FALLING_FAST, result.grade)

        val assessment = WeatherRule.assess(result)
        assertEquals(RainLikelihood.HIGH, assessment.likelihood)
        assertTrue("规则是启发式的，必须诚实标记为未校准", !assessment.calibrated)
    }

    @Test
    fun `爬五层楼被识别为高度事件而不是天气剧变`() {
        val samples = ArrayList<PressureSample>(180)
        var pressure = 1000f
        for (index in 0 until 180) {
            val climbing = index in 60..62
            if (climbing) pressure -= 0.6f // 每步约 5 米，三步共 15 米
            samples += PressureSample(
                timestampMs = startMs + index * minuteMs,
                pressureHpa = pressure,
                verticalAccel = if (climbing) 2.0f else 0f,
                stepsInWindow = if (climbing) 30 else 0
            )
        }

        val result = PressureTrendEngine().compute(samples)

        assertEquals("爬楼不得污染天气趋势", TrendGrade.STEADY, result.grade)
        assertEquals(3, result.elevationEvents)
        assertEquals(15f, result.elevationMeters, 0.3f)
        assertEquals(0f, result.rateHpaPerHour, 0.1f)
    }

    @Test
    fun `天气在缓降同时爬楼_两个分量互不污染`() {
        val samples = ArrayList<PressureSample>(180)
        var weatherPressure = 1000f
        var climbedHpa = 0f
        for (index in 0 until 180) {
            weatherPressure -= 0.02f // -1.2 hPa/h
            val climbing = index in 60..62
            if (climbing) climbedHpa += 0.6f
            samples += PressureSample(
                timestampMs = startMs + index * minuteMs,
                pressureHpa = weatherPressure - climbedHpa,
                verticalAccel = if (climbing) 2.0f else 0f,
                stepsInWindow = if (climbing) 30 else 0
            )
        }

        val result = PressureTrendEngine().compute(samples)

        assertEquals("爬楼不得把缓降放大成急降", TrendGrade.FALLING, result.grade)
        assertEquals(-1.2f, result.rateHpaPerHour, 0.2f)
        assertEquals(3, result.elevationEvents)
        assertEquals(15.5f, result.elevationMeters, 1f)
    }

    @Test
    fun `电梯快速升降同样被剔除`() {
        val samples = ArrayList<PressureSample>(180)
        var pressure = 1000f
        for (index in 0 until 180) {
            val inElevator = index in 60..61
            if (inElevator) pressure -= 3f // 单次 3 hPa，约 25 米
            samples += PressureSample(
                timestampMs = startMs + index * minuteMs,
                pressureHpa = pressure,
                verticalAccel = if (inElevator) 1.2f else 0f,
                stepsInWindow = 0
            )
        }

        val result = PressureTrendEngine().compute(samples)

        assertEquals(TrendGrade.STEADY, result.grade)
        assertEquals(2, result.elevationEvents)
        assertEquals(50f, result.elevationMeters, 0.5f)
    }

    @Test
    fun `电梯匀速段加速度为零也必须被剔除`() {
        // 真实电梯：起步有一下加速度脉冲，随后匀速（加速度≈0、无步数），
        // 但气压仍以约 1.8 hPa/min 持续变化。只认瞬时证据会漏掉匀速段。
        val samples = ArrayList<PressureSample>(200)
        var pressure = 1000f
        val tickMs = 2_000L
        for (index in 0 until 200) {
            val riding = index in 60..90
            if (riding) pressure -= 0.06f // 每 2 秒 0.06 hPa，即 1.8 hPa/min
            samples += PressureSample(
                timestampMs = startMs + index * tickMs,
                pressureHpa = pressure,
                verticalAccel = if (index == 60) 1.5f else 0f,
                stepsInWindow = 0
            )
        }

        val result = PressureTrendEngine().compute(samples)

        assertEquals("匀速段不得被当成天气变化", TrendGrade.STEADY, result.grade)
        assertEquals(31, result.elevationEvents)
        assertEquals(15.5f, result.elevationMeters, 1f)
    }

    @Test
    fun `平地走路带步数也不会被误判为高度事件`() {
        // 加速度已超阈值、步数也非零，但变压速率只有天气量级，因此不该剔除。
        val samples = weatherSeries(
            minutes = 180,
            hPaPerHour = -1.0f,
            verticalAccel = 0.5f,
            stepsPerWindow = 12
        )

        val result = PressureTrendEngine().compute(samples)

        assertEquals(0, result.elevationEvents)
        assertEquals(TrendGrade.FALLING, result.grade)
        assertEquals(-1.0f, result.rateHpaPerHour, 0.1f)
    }

    @Test
    fun `秒级采样下噪声不得伪造成高度事件`() {
        // 复刻真机静置实测：2 秒采样、气压抖动约 ±0.02 hPa，中途被人碰一下产生垂直加速度。
        // 若用相邻步算速率，0.02 hPa / 2s = 0.6 hPa/min 会直接越过 0.1 阈值；
        // 30 秒基线把同一份数据压到 0.00 hPa/min，所以事件数必须为 0。
        val tickMs = 2_000L
        val jitter = floatArrayOf(0f, 0.01f, -0.01f, 0.015f, -0.015f, 0.005f, -0.005f)
        val samples = (0 until 300).map { index ->
            PressureSample(
                timestampMs = startMs + index * tickMs,
                pressureHpa = 1000f + jitter[index % jitter.size],
                verticalAccel = if (index == 100) 0.6f else 0.1f,
                stepsInWindow = 0
            )
        }

        val result = PressureTrendEngine().compute(samples)

        assertEquals("噪声不得被记成垂直运动", 0, result.elevationEvents)
        assertEquals(0f, result.elevationMeters, 0.5f)
    }

    @Test
    fun `窗口内绝对变压低于噪声门限时一律按平稳处理`() {
        // 复刻真机实测：6 分钟窗口内气压只在 0.02 hPa 内缓慢单向漂移。
        // 这种序列能被回归算出 -0.56 hPa/h 且 R² 0.52，速率与拟合优度都拦不住，
        // 只能靠绝对量门限拦住。
        val samples = (0 until 180).map { index ->
            PressureSample(
                timestampMs = startMs + index * 2_000L,
                pressureHpa = 1000f - 0.02f * index / 179f
            )
        }

        val result = PressureTrendEngine(windowMs = 6 * 60 * 1000L).compute(samples)

        assertEquals("绝对量不够就不该谈方向", TrendGrade.STEADY, result.grade)
        assertEquals(0f, result.rateHpaPerHour, 0.01f)
        assertEquals(-0.02f, result.observedDeltaHpa, 0.005f)
    }

    @Test
    fun `样本不足时不下结论`() {
        val result = PressureTrendEngine().compute(weatherSeries(3))

        assertEquals(TrendGrade.INSUFFICIENT, result.grade)
        assertEquals(TrendConfidence.INSUFFICIENT, result.confidence)
        assertEquals(RainLikelihood.UNKNOWN, WeatherRule.assess(result).likelihood)
    }

    @Test
    fun `窗口覆盖不足时即使斜率很大也不给结论`() {
        // 只有 10 分钟样本，却配 3 小时窗口：覆盖率约 5%，远低于 30% 闸门。
        val samples = (0 until 10).map { index ->
            PressureSample(
                timestampMs = startMs + index * minuteMs,
                pressureHpa = 1000f - 2f * index // 荒谬的 -120 hPa/h
            )
        }

        val result = PressureTrendEngine().compute(samples)

        assertEquals(TrendConfidence.SHORT_WINDOW, result.confidence)
        assertEquals(RainLikelihood.UNKNOWN, WeatherRule.assess(result).likelihood)
    }

    @Test
    fun `叠加振荡造成的斜率不可信时判为趋势不稳`() {
        // 线性缓降叠加强振荡：斜率看着有 1.2 hPa/h，但 R² 很低，不该据此报天气。
        val samples = (0 until 180).map { index ->
            PressureSample(
                timestampMs = startMs + index * minuteMs,
                pressureHpa = 1000f - 0.02f * index +
                    4f * sin(2.0 * PI * index / 10.0).toFloat()
            )
        }

        val result = PressureTrendEngine().compute(samples)

        assertEquals(TrendConfidence.NOISY, result.confidence)
        assertTrue("R² 应当很低", result.fitRSquared < PressureTrendEngine.DEFAULT_MIN_R_SQUARED)
        assertEquals(RainLikelihood.UNKNOWN, WeatherRule.assess(result).likelihood)
    }
}
