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
        // 但气压仍在持续变化。只认瞬时证据会漏掉匀速段。
        //
        // 速率用真机实测值：OWW221 上电梯单趟 −70.5 米 ≈ 11 hPa/min。
        // 原先这里用 1.8 hPa/min（合成值），在把高度门限提到 0.3 hPa/min 之后
        // 就掉出了判据——但那是夹具不真实，不是引擎的问题：
        // 1.8 hPa/min 会在前 26 秒里低于门限，而真实电梯 15 秒内就已越过。
        val samples = ArrayList<PressureSample>(200)
        var pressure = 1000f
        val tickMs = 2_000L
        for (index in 0 until 200) {
            val riding = index in 60..90
            if (riding) pressure -= 0.367f // 每 2 秒 0.367 hPa，即 11 hPa/min（真机实测值）
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
        // 31 步 × 0.367 hPa = 11.4 hPa ÷ 0.12 hPa/米 ≈ 95 米（真机电梯量级）
        assertEquals(95f, result.elevationMeters, 2f)
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
    fun `屋里活动加缓慢天气变化_不得攒出假的垂直位移`() {
        // 2026-09-11 真机事故的复现夹具。
        //
        // 实况：主人在屋里活动（躺下、起来、走动）整个下午，气压缓慢下降
        // （3.75 小时约 0.9 hPa）。旧门限 0.1 hPa/min 之下，
        // 手腕每动一下就可能把当步气压差记成"垂直位移"，累计攒出 +10 米，
        // 进而吃掉真实天气降幅，把一场转雨过程记成了「平稳」。
        //
        // 真机数据里 30 秒气压变化的中位数只有 0.010 hPa、最大 0.120 hPa，
        // 而旧门限相当于 30 秒 0.05 hPa——有 4.6% 的窗口越过它。
        // 门限提到 0.3 hPa/min（30 秒 0.15 hPa）之后越界窗口为 0。
        val tickMs = 5_000L
        val samples = ArrayList<PressureSample>(2700)
        var pressure = 1004.1f
        for (index in 0 until 2700) {
            // 缓慢天气下降：0.01 hPa / 15 秒 ≈ 0.04 hPa/min，远低于门限
            if (index % 3 == 0) pressure -= 0.01f
            samples += PressureSample(
                timestampMs = startMs + index * tickMs,
                pressureHpa = pressure,
                // 手腕一直在动：躺下、起来、走动的竖直加速度远超阈值
                verticalAccel = if (index % 8 == 0) 12f else 0.1f,
                stepsInWindow = if (index % 20 < 4) 6 else 0
            )
        }

        val result = PressureTrendEngine().compute(samples)

        assertEquals("屋里活动不该被记为高度事件", 0, result.elevationEvents)
        assertEquals("累计垂直位移必须接近 0", 0f, result.elevationMeters, 0.5f)
        // 注意窗口是 3 小时，而序列有 3.75 小时，所以窗口内净变是 7.2 hPa 而不是全部 9.0
        assertEquals("真实的气压下降必须完整留在天气分量里", -7.2f, result.observedDeltaHpa, 0.5f)
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
    fun `平地段噪声不得把事件计数虚高`() {
        // 复刻真机实测：26 楼到 1 楼的电梯往返共 4 段行程、真实电梯单步约 0.40 hPa，
        // 而平地段噪声单步只有 0.01–0.02 hPa。若不给单步设门限，
        // 平地段会被计入大量无意义事件（真机上曾从 4 段虚高到 145 次）。
        val tickMs = 2_000L
        // 抖动幅度对齐真机实测：相邻样本最大差 0.02 hPa（实测口径），低于 0.03 的单步门限。
        val jitter = floatArrayOf(0f, 0.01f, -0.01f, 0.01f, -0.01f)
        val samples = ArrayList<PressureSample>()
        var pressure = 1000f

        // 平地段 60 个样本
        for (index in 0 until 60) {
            samples += PressureSample(startMs + index * tickMs, pressure + jitter[index % jitter.size],
                verticalAccel = 0.2f)
        }
        // 电梯下行 30 个样本，每步 +0.40 hPa（气压缩小即海拔降低方向相反）
        for (index in 60 until 90) {
            pressure += 0.40f
            samples += PressureSample(startMs + index * tickMs, pressure + jitter[index % jitter.size],
                verticalAccel = 0.9f)
        }
        // 平地段再 60 个样本
        for (index in 90 until 150) {
            samples += PressureSample(startMs + index * tickMs, pressure + jitter[index % jitter.size],
                verticalAccel = 0.2f)
        }

        val result = PressureTrendEngine().compute(samples)

        assertEquals("只该计出电梯那 30 步", 30, result.elevationEvents)
        assertEquals(-100f, result.elevationMeters, 3f) // 12 hPa / 0.12 = 100 米
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
