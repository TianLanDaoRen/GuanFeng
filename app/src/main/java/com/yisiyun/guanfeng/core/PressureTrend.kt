package com.yisiyun.guanfeng.core

import kotlin.math.abs

/*
 * 观风 · 算法内核（纯 Kotlin，零 Android 依赖，可直接在 JVM 上单测）
 *
 * 核心命题：气压计读到的是「天气 + 高度」的叠加信号。
 * 绝大多数气压天气小工具的死穴，是把爬楼误读成天气剧变——
 * 爬一层楼（约 3 米）就有约 0.36 hPa，一次五层楼接近 1.8 hPa，
 * 而那已经是一整天天气变化的量级了。
 *
 * 本文件用双重判据把两者拆开：变化率超阈值【且】存在垂直运动证据。
 */

/** 海平面附近标准大气：气压随高度约 -0.12 hPa/m（近似值，随海拔与气温变化）。 */
const val HPA_PER_METER_NEAR_SEA_LEVEL = 0.12f

/** 平稳/变化的边界（hPa/h）。气象惯例里 ΔP(3h) ≥ 1.6 hPa 视为显著变化，即约 0.53 hPa/h。 */
const val STEADY_BOUNDARY_HPA_PER_HOUR = 0.5f

/** 缓变/急变的边界（hPa/h）。 */
const val RAPID_BOUNDARY_HPA_PER_HOUR = 1.5f

data class PressureSample(
    val timestampMs: Long,
    val pressureHpa: Float,
    /** 去重力后的垂直加速度（m/s²），静止时接近 0。 */
    val verticalAccel: Float = 0f,
    /**
     * 带泄漏二次积分得到的**竖直净位移**（米）。
     *
     * 这是判断"人是否真的在垂直运动"的正确量：挥手的往复运动净位移≈0，
     * 而爬楼/坐电梯是持续位移、会稳定累积。用加速度峰值判断则两者无法区分
     * （真机上挥手很容易到 10 m/s² 以上，比爬楼还大）。
     */
    val verticalDisplacementM: Float? = null,
    /** 该采样窗口内累计步数（step_detector 差值），0 表示没在走。 */
    val stepsInWindow: Int = 0
)

enum class TrendGrade(val label: String) {
    FALLING_FAST("急降"),
    FALLING("缓降"),
    STEADY("平稳"),
    RISING("缓升"),
    RISING_FAST("急升"),
    INSUFFICIENT("数据不足")
}

/**
 * 趋势置信度。只有 OK 时才允许对外给风雨结论。
 *
 * 为什么必须在引擎层设闸门：实测日志里出现过「窗口 0.14 分钟、R² 0.029，却报出
 * 缓升 1.382 hPa/h」的假警报。短窗口里噪声太容易凑出一个像样的斜率，
 * 让 UI 去判读已经太晚——错在源头就得在源头拦。
 */
enum class TrendConfidence(val label: String) {
    OK("可信"),
    SHORT_WINDOW("窗口太短"),
    NOISY("趋势不稳"),
    INSUFFICIENT("数据不足")
}

data class TrendResult(
    val grade: TrendGrade,
    /** 回归斜率换算的每小时变压，单位 hPa/h。 */
    val rateHpaPerHour: Float,
    /** 按上述速率外推的 3 小时变压。 */
    val deltaHpaPer3h: Float,
    /** 参与回归的「天气样本」数（已剔除高度事件）。 */
    val weatherSamples: Int,
    /** 被判定为高度事件并计入解耦的样本数（诊断量：它数的是样本，不是行程次数）。 */
    /**
     * **当前 3 小时窗口内**被计为高度事件的步数，**不是历史累计**。
     *
     * 一趟散步的事件会随窗口滑动而增加、再随窗口滑走而减少（真机：同一趟散步
     * 在窗口里时 37、滑走一半后 18）。要在界面上显示，标签必须写明"本窗口"。
     */
    val elevationEvents: Int,
    /** 窗口末尾那一刻是否仍处于垂直运动中。UI 用它给用户一个直观的状态，而不是给易误读的计数。 */
    val isInVerticalTransit: Boolean,
    /** 期间累计垂直位移，正值为上升，单位米（按 0.12 hPa/m 近似换算）。 */
    /**
     * **当前 3 小时窗口内**累加出来的高度变化（米），**不是累计位移**。
     *
     * 它是 `elevationOffset`（compute() 的局部变量）换算的：窗口里那段电梯/坡道
     * 的进进出出都会改变它，所以同一段真实往返，在窗口不同相位上可以显示 +70 米或 −70 米。
     * 2026-09-12 的真机事故就是这样：18:50 那段 8.7 hPa（约 70 米）的真实往返，
     * 在 20:56 显示 −69.8 米、21:56 显示 −0.1 米、22:18 又显示 +69.8 米。
     *
     * **要显示"累计垂直位移"，用 `RecorderState.elevationOffsetMeters`**
     * （跨窗口累计，与解耦、与落盘状态同一把尺子）。
     */
    val elevationMeters: Float,
    /** 线性拟合优度，用于判断这段趋势可不可信。 */
    val fitRSquared: Float,
    /** 窗口内实测的绝对变压（末样本 − 首样本），单位 hPa。 */
    val observedDeltaHpa: Float,

    /**
     * **本窗口的绝对变压没到判据门限**（`minAbsoluteDeltaHpa`，默认 0.5 hPa），
     * 于是速率被强制为 0、按平稳处理。
     *
     * 为什么要单独标出来：界面上写"外推 +0.0 hPa"会被读成"气压不变"，
     * 而真相是"确实变了 −0.45，但幅度不足以支撑一个方向"——
     * 2026-09-13 主人正是被这个显示误导（关联页曲线明明在往下）。
     * 有了这个标志，界面可以在被压平时改说"变化不足 0.5 hPa"。
     */
    val belowThreshold: Boolean = false,
    /**
     * 窗口内气压曲线走过的总路程（相邻步变化量绝对值之和，hPa）。
     *
     * 用途：与 |observedDeltaHpa| 相除得到**路径效率**，用来区分两种"R² 都很低"的形态：
     *   · 先急降后转平——净降 4 hPa、路程 4 hPa → 效率 ≈ 1（真实的降水过程）
     *   · 来回振荡——净变 0 hPa、路程 30 hPa → 效率 ≈ 0（噪声）
     * 只看净变幅会把后者也当成急降报出去，这是捷径式实现的典型坑。
     */
    val pathLengthHpa: Float,
    /** 窗口末尾那一步若被归为高度事件，这里是它从原始气压里剔出去的变化量（hPa）；否则为 null。 */
    val lastElevationStepHpa: Float?,
    /** 实际覆盖的时间跨度（分钟）。 */
    val windowMinutes: Float,
    /** 窗口覆盖比例（0–1）。太低说明样本还没铺满，斜率不可信。 */
    val coverageFraction: Float,
    /** 这张读数到底能不能信；只有 OK 时才允许对外给风雨倾向。 */
    val confidence: TrendConfidence
) {
    companion object {
        fun insufficient(samples: Int) = TrendResult(
            grade = TrendGrade.INSUFFICIENT,
            rateHpaPerHour = 0f,
            deltaHpaPer3h = 0f,
            weatherSamples = samples,
            elevationEvents = 0,
            isInVerticalTransit = false,
            elevationMeters = 0f,
            fitRSquared = 0f,
            observedDeltaHpa = 0f,
            pathLengthHpa = 0f,
            lastElevationStepHpa = null,
            windowMinutes = 0f,
            coverageFraction = 0f,
            confidence = TrendConfidence.INSUFFICIENT
        )
    }
}

/**
 * 高度事件判定。
 *
 * 阈值 0.1 hPa/min 的来历（可用本仓库的物理底数复核）：
 *   人的垂直速度即便很慢也有 0.05 m/s → 0.12 × 0.05 × 60 = 0.36 hPa/min；
 *   而极端天气上限约 10 hPa/3h = 0.056 hPa/min。
 *   两者相差约 13 倍，0.1 卡在中间：比极端天气高 1.8 倍，比最慢爬楼低 7.2 倍。
 *   余量偏窄的那一侧由「必须有垂直运动证据」这条与条件兜住——
 *   台风逼来时人通常不在爬楼。
 */
object ElevationClassifier {

    /**
     * 触发高度事件所需的变压速率。
     *
     * ## 为什么从 0.1 提到 0.3（2026-09-11 真机事故）
     *
     * 主人整个下午只在屋里活动（躺下、起来、走动），却被累计判出 **+10 米**的
     * 垂直位移，进而吃掉 1.2 hPa 的真实气压下降——结果把一场真实的转雨过程
     * 记成了「平稳」。
     *
     * 复盘（真机数据，13:11–16:56 共 2679 个 30 秒窗口）：
     *   30 秒气压变化的中位数只有 **0.010 hPa**，90 分位 0.030，最大值 0.120；
     *   而旧门限 0.1 hPa/min 相当于 30 秒变化 0.05 hPa —— **有 123 个窗口（4.6%）越过它**。
     * 由于"垂直运动证据"在手腕上几乎恒为真（手一动就有加速度），
     * 每一次越过都会把当步气压差记成"垂直位移"，累积成假的高度变化。
     * 提到 0.3 hPa/min（30 秒 0.15 hPa）后，这段数据里越界窗口为 **0**。
     *
     * 提到 0.3 不影响已被真机验证的两种场景：
     *   电梯单趟 −70.5 米 ≈ **11 hPa/min**；三层楼梯 −8.2 米 ≈ **0.94 hPa/min**，
     * 都远在门限之上。换句话说，真正的垂直运动根本不需要贴着噪声底的门限。
     *
     * 残留风险（已知、未解）：强对流天气本身可以达到 0.5~1 hPa/min，
     * 若此时手正在动，仍可能被误记为高度。要根治需要"运动是否是连贯的垂直位移"
     * 这一层判断（例如对垂直加速度做带泄漏的二次积分，看净位移而不是看峰值），
     * 但那属于另一个量级的工作，先记录在此。
     */
    const val RATE_THRESHOLD_HPA_PER_MIN = 0.3f

    /** 去重力垂直加速度的显著阈值（m/s²）。 */
    const val VERTICAL_ACCEL_EPSILON = 0.35f

    /**
     * 瞬时证据：这一采样点上是否看得出垂直运动。
     *
     * 注意它**只是点火条件，不是判决依据**——真实电梯的匀速段加速度接近 0、
     * 也没有步数，但气压仍在持续变化；只认瞬时证据会把匀速段误判成天气剧变。
     * 判决由引擎里的「垂直位移状态机」完成：瞬时证据点火，之后靠持续的变化率维持。
     */
    /**
     * 净位移达到此值即认为"人真的在垂直移动"（米）。
     *
     * 取值依据：
     *   · 挥手/走路：带泄漏二次积分后净位移约 0.1~0.5 米；
     *   · 爬一层楼：约 3 米；坐电梯：几十米。
     * 1.5 米落在两个量级之间，且高于积分漂移（偏差 0.03 m/s² 时约 0.14 米）。
     */
    const val DISPLACEMENT_EVIDENCE_M = 1.5f

    /**
     * 瞬时证据：这一采样点上是否看得出**真正的垂直位移**。
     *
     * 曾经这里用的是竖直加速度峰值，那是错的：真机上挥一下手就能到 10 m/s² 以上，
     * 比爬楼时的加速度还大，于是"有垂直运动"几乎恒为真——手一动就把气压变化
     * 记成高度变化。2026-09-11 主人在屋里活动一下午被攒出 +10 米假位移，
     * 根因就在这里。
     *
     * 换成净位移之后：往复运动互相抵消，只有持续的单向位移才会积累。
     */
    fun hasInstantEvidence(sample: PressureSample): Boolean {
        val displacement = sample.verticalDisplacementM
        if (displacement != null) {
            // 新路径：净位移。挥手的往复运动互相抵消，只有持续单向位移才会积累。
            return kotlin.math.abs(displacement) >= DISPLACEMENT_EVIDENCE_M
        }
        // 旧路径（仅在读历史数据时走到）：早期落盘的 CSV 没有净位移列，
        // 只能退回"加速度峰值或步数"这条旧判据。它对静止场景够用，
        // 但会在手腕活动时几乎恒为真——这正是 2026-09-11 那次事故的根源，
        // 所以**新写入的数据一律走净位移**，旧路径只服务回放与单测夹具。
        return abs(sample.verticalAccel) > VERTICAL_ACCEL_EPSILON ||
            sample.stepsInWindow > 0
    }
}

/**
 * 气压趋势引擎。
 *
 * 算法：按时间窗口取样本 → 逐点判定高度事件并把该段变化记入 offset →
 * 后续样本统一减去 offset，得到只含天气分量的校正序列 → 对校正序列做最小二乘拟合。
 *
 * 已知取舍：爬楼期间若天气同时在变，那一小段天气变化会被一并计入 offset。
 * 量级可接受——爬楼持续几分钟，天气变化约 0.02 hPa/min，5 分钟也只误吸 0.1 hPa。
 */
class PressureTrendEngine(
    private val windowMs: Long = DEFAULT_WINDOW_MS,
    private val minSamples: Int = DEFAULT_MIN_SAMPLES,
    private val rateThresholdHpaPerMin: Float = ElevationClassifier.RATE_THRESHOLD_HPA_PER_MIN,
    /** 要求窗口至少被样本覆盖到这个比例，否则不给趋势结论。 */
    private val minCoverageFraction: Float = DEFAULT_MIN_COVERAGE_FRACTION,
    /** 一旦 |速率| 达到「变化」量级，就要求拟合优度不低于此值，否则判为趋势不稳。 */
    private val minRSquaredForTrend: Float = DEFAULT_MIN_R_SQUARED,
    /** 计算速率所需的最小基线跨度。见 DEFAULT_MIN_BASELINE_MS 的由来。 */
    private val minBaselineMs: Long = DEFAULT_MIN_BASELINE_MS,
    /** 窗口内绝对变压的噪声门限，低于它一律按平稳处理。见 DEFAULT_MIN_ABSOLUTE_DELTA_HPA。 */
    private val minAbsoluteDeltaHpa: Float = DEFAULT_MIN_ABSOLUTE_DELTA_HPA,
    /** 单步气压变化的最小有效幅度。见 DEFAULT_MIN_STEP_DELTA_HPA。 */
    private val minStepDeltaHpa: Float = DEFAULT_MIN_STEP_DELTA_HPA
) {

    fun compute(samples: List<PressureSample>): TrendResult {
        if (samples.isEmpty()) return TrendResult.insufficient(0)

        val endMs = samples.maxOf { it.timestampMs }
        val windowed = samples
            .filter { it.timestampMs > endMs - windowMs }
            .sortedBy { it.timestampMs }

        if (windowed.size < minSamples) return TrendResult.insufficient(windowed.size)

        // 第一步：高度解耦（30 秒基线 + 垂直位移状态机）。
        //
        // 为什么要拉长基线：真机静置数据实测——相邻 2 秒的气压抖动可达 0.02 hPa，
        // 折算 0.6 hPa/min，是 0.1 阈值的 6 倍，噪声自己就能把状态机点亮；
        // 而同一份数据在 30 秒尺度上的最大变化是 0.00 hPa。
        // 结论：短基线量的是噪声，长基线量的才是位移，所以速率一律基于 ≥30 秒的基线来算。
        //
        // 为什么还要状态机：真实电梯匀速段加速度接近 0、也没有步数，
        // 只看瞬时证据会把它误判成天气剧变——因此由一次瞬时垂直证据点火，
        // 之后靠持续超阈值的速率维持，直到速率回落才退出。
        // 状态机不会失控：天气根本无法维持 0.1 hPa/min 以上的持续变压。
        var elevationOffset = 0f
        var elevationEvents = 0
        var inVerticalTransit = false
        var baselineIndex = 0
        var lastElevationStepHpa: Float? = null
        var pathLength = 0f
        val corrected = ArrayList<Pair<Long, Float>>(windowed.size)
        corrected += windowed[0].timestampMs to windowed[0].pressureHpa

        for (index in 1 until windowed.size) {
            val sample = windowed[index]
            val previous = windowed[index - 1]
            val stepDelta = sample.pressureHpa - previous.pressureHpa
            pathLength += abs(stepDelta)

            // 基线指针只前进不回退，保持 O(n)。
            while (baselineIndex + 1 < index &&
                sample.timestampMs - windowed[baselineIndex + 1].timestampMs >= minBaselineMs
            ) {
                baselineIndex++
            }
            val baselineSpanMs = sample.timestampMs - windowed[baselineIndex].timestampMs
            val baselineDelta = sample.pressureHpa - windowed[baselineIndex].pressureHpa
            val rate = if (baselineSpanMs >= minBaselineMs) {
                baselineDelta / (baselineSpanMs / MS_PER_MINUTE)
            } else {
                0f // 基线还不够长，本步不参与判定
            }
            val fastChange = abs(rate) > rateThresholdHpaPerMin

            if (ElevationClassifier.hasInstantEvidence(sample)) {
                inVerticalTransit = true
            }
            if (!fastChange) {
                inVerticalTransit = false
            }

            // 高度增量按「相邻步」累加而不是按基线增量，否则基线之前的变化会被重复计入；
            // 同时要求本步幅度超过噪声底——真机实测平地段噪声单步 ≤0.02 hPa，
            // 若不设这道门限，平地段会被计入大量 0.01 hPa 级的「事件」，
            // 让 elevation_events 从真实 4 段虚高到 145 次。
            if (fastChange && inVerticalTransit && abs(stepDelta) >= minStepDeltaHpa) {
                elevationOffset += stepDelta
                elevationEvents++
                // 只把「最后一步」报出去：调用方据此跨窗口累积偏移，
                // 从而得到一条与窗口无关的、真正只含天气分量的气压序列。
                if (index == windowed.lastIndex) {
                    lastElevationStepHpa = stepDelta
                }
            }

            corrected += sample.timestampMs to (sample.pressureHpa - elevationOffset)
        }

        // 第二步：对校正序列做最小二乘拟合，斜率即天气性变压速率。
        val baseMs = corrected.first().first
        val n = corrected.size
        var sumX = 0.0
        var sumY = 0.0
        var sumXy = 0.0
        var sumXx = 0.0
        var sumYy = 0.0
        for ((timestampMs, pressure) in corrected) {
            val x = (timestampMs - baseMs).toDouble()
            val y = pressure.toDouble()
            sumX += x
            sumY += y
            sumXy += x * y
            sumXx += x * x
            sumYy += y * y
        }

        val denominator = n * sumXx - sumX * sumX
        val slopePerMs = if (denominator != 0.0) (n * sumXy - sumX * sumY) / denominator else 0.0
        val intercept = (sumY - slopePerMs * sumX) / n

        val meanY = sumY / n
        val totalSumSquares = sumYy - n * meanY * meanY
        var residualSumSquares = 0.0
        for ((timestampMs, pressure) in corrected) {
            val x = (timestampMs - baseMs).toDouble()
            val predicted = intercept + slopePerMs * x
            val residual = pressure - predicted
            residualSumSquares += residual * residual
        }
        val rSquared = if (totalSumSquares > 1e-9) {
            (1.0 - residualSumSquares / totalSumSquares).toFloat().coerceIn(0f, 1f)
        } else {
            // 序列完全平坦：天气无变化，视为完美拟合「无趋势」。
            1f
        }

        val ratePerHour = (slopePerMs * MS_PER_HOUR).toFloat()
        val observedDelta = corrected.last().second - corrected.first().second
        val coveredMinutes = (corrected.last().first - baseMs).toFloat() / MS_PER_MINUTE
        val coverage = (corrected.last().first - baseMs).toFloat() / windowMs.toFloat()

        // 绝对量门限：真机实测静置 2–6 分钟的窗口内气压总跨度只有 0.04–0.05 hPa，
        // 却能被回归算出 -0.56 hPa/h、R² 还有 0.52。速率与拟合优度都拦不住它，
        // 因为那确实是一条很干净的微小直线——错在「绝对量根本不够」。
        // 所以窗口内绝对变压不到门限时，一律按平稳处理，而不是报一个看起来精确的方向。
        val meaningfulTrend = abs(observedDelta) >= minAbsoluteDeltaHpa
        val effectiveRate = if (meaningfulTrend) ratePerHour else 0f

        // 置信闸门：先看窗口够不够长，再看「有速率」时拟合稳不稳。
        val confidence = when {
            coverage < minCoverageFraction -> TrendConfidence.SHORT_WINDOW
            meaningfulTrend && rSquared < minRSquaredForTrend -> TrendConfidence.NOISY
            else -> TrendConfidence.OK
        }

        return TrendResult(
            grade = gradeOf(effectiveRate),
            rateHpaPerHour = effectiveRate,
            deltaHpaPer3h = effectiveRate * 3f,
            weatherSamples = corrected.size,
            elevationEvents = elevationEvents,
            isInVerticalTransit = inVerticalTransit,
            // 用气压高度公式换算（见 Barometric）：固定系数 0.12 在 26 层楼这种量级上
            // 实测差 0.4 米，而在低气压基准（山城/高层）会差更多。
            elevationMeters = Barometric.offsetToMeters(
                offsetHpa = elevationOffset,
                currentPressureHpa = windowed.lastOrNull()?.pressureHpa,
            ),
            fitRSquared = rSquared,
            observedDeltaHpa = observedDelta,
            belowThreshold = !meaningfulTrend,
            pathLengthHpa = pathLength,
            lastElevationStepHpa = lastElevationStepHpa,
            windowMinutes = coveredMinutes,
            coverageFraction = coverage.coerceIn(0f, 1f),
            confidence = confidence
        )
    }

    private fun gradeOf(rateHpaPerHour: Float): TrendGrade = when {
        rateHpaPerHour <= -RAPID_BOUNDARY_HPA_PER_HOUR -> TrendGrade.FALLING_FAST
        rateHpaPerHour <= -STEADY_BOUNDARY_HPA_PER_HOUR -> TrendGrade.FALLING
        rateHpaPerHour < STEADY_BOUNDARY_HPA_PER_HOUR -> TrendGrade.STEADY
        rateHpaPerHour < RAPID_BOUNDARY_HPA_PER_HOUR -> TrendGrade.RISING
        else -> TrendGrade.RISING_FAST
    }

    companion object {
        const val DEFAULT_WINDOW_MS = 3L * 60L * 60L * 1000L
        const val DEFAULT_MIN_SAMPLES = 8

        /** 窗口覆盖率下限：低于 30% 的窗口不值得算斜率。 */
        const val DEFAULT_MIN_COVERAGE_FRACTION = 0.3f

        /** 有速率时的拟合优度下限。此值目前是保守拍定，待真实数据校准。 */
        const val DEFAULT_MIN_R_SQUARED = 0.4f

        /**
         * 速率基线至少 30 秒。依据来自真机实测（OWW221 静置在底座上）：
         *   相邻 2 秒的最大气压变化 0.02 hPa → 0.6 hPa/min（是 0.1 阈值的 6 倍）
         *   30 秒基线内最大变化 0.00 hPa → 0.000 hPa/min
         * 而人爬楼时 30 秒内会产生 0.36–1.8 hPa（即 0.72–3.6 hPa/min），依然远高于阈值。
         * 于是 30 秒把「噪声」与「真实位移」彻底分开。
         */
        const val DEFAULT_MIN_BASELINE_MS = 30_000L

        /**
         * 绝对量门限（hPa）。依据同样来自真机实测：
         * 静置 2–6 分钟的窗口内气压总跨度只有 0.04–0.05 hPa，
         * 而一个能称为「缓降」的天气变化在 3 小时里应有 1.5 hPa 量级。
         * 取 0.5 hPa 作门限——短窗口里它意味着「你没有足够证据谈方向」，
         * 长窗口里真实天气变化远高于它，不会误伤。
         */
        const val DEFAULT_MIN_ABSOLUTE_DELTA_HPA = 0.5f

        /**
         * 单步幅度门限（hPa）。依据是真机实测的两组数：
         *   平地段相邻 2 秒的噪声幅度 ≤ 0.02 hPa；
         *   实测下楼单步 0.04–0.11 hPa、电梯单步约 0.40 hPa。
         * 取 0.03 卡在中间：噪声被滤掉，真实垂直运动完整保留。
         */
        const val DEFAULT_MIN_STEP_DELTA_HPA = 0.03f

        private const val MS_PER_MINUTE = 60_000f
        private const val MS_PER_HOUR = 3_600_000f
    }
}
