package com.yisiyun.guanfeng.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.yisiyun.guanfeng.core.HourAccumulator
import com.yisiyun.guanfeng.core.WeatherEpisode
import com.yisiyun.guanfeng.core.WeatherEpisodeTracker
import com.yisiyun.guanfeng.core.WeatherRule
import com.yisiyun.guanfeng.core.HourlyRow
import com.yisiyun.guanfeng.core.PressureSample
import com.yisiyun.guanfeng.core.RainLikelihood
import com.yisiyun.guanfeng.core.PressureTrendEngine
import com.yisiyun.guanfeng.core.SampleAggregator
import com.yisiyun.guanfeng.core.TrendResult
import com.yisiyun.guanfeng.log.CsvSessionLogger
import com.yisiyun.guanfeng.log.HourlyArchive
import com.yisiyun.guanfeng.log.PowerLogger
import com.yisiyun.guanfeng.service.TrendNotifier
import com.yisiyun.guanfeng.log.SessionHistory
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 采集器对界面暴露的全部状态。界面只读它，不再自己接传感器。 */
data class RecorderState(
    val recording: Boolean = false,
    val pressureHpa: Float? = null,
    /**
     * 解耦掉高度之后的「天气分量」气压。
     * 长视图、打卡快照都必须用它——用原始读数会把坐电梯的 9 hPa 算成一次天气剧变。
     */
    val weatherPressureHpa: Float? = null,
    /** 正式趋势（3 小时窗口）。 */
    val trend: TrendResult? = null,
    /** 速评趋势（5 分钟窗口）：3 小时窗口还没铺满时先给一个结论。 */
    val trendFast: TrendResult? = null,
    /** 是否处于静止（无步数且加速度低）。健身中不做体感提示，见 CorroborationEngine。 */
    val isResting: Boolean = true,
    /**
     * 最近 [RECENT_FALL_WINDOW_MS] 内「天气分量」的累计净降幅（≤0）。
     * 用于记住"曾经降过"，避免气压降完转平后就被判成「无需带伞」。
     */
    val recentFallHpa: Float = 0f,
    /** 天气过程状态：进入「可能下雨」后一直挂着，直到气压自最低点明显回升。 */
    val episode: WeatherEpisode? = null,
    /** 传感器此刻的瞬时心率（`TYPE_HEART_RATE` 直接给的值，不是静息心率）。 */
    val heartRateBpm: Float? = null,
    /**
     * 静息基线：只采「没有垂直运动、没有步数」的静止样本，取其低分位数。
     * 这才是「静息心率」该有的来历——瞬时值直接当静息心率是错的。
     */
    val restingHeartRateBpm: Float? = null,
    val wristTemperatureC: Float? = null,
    /** 腕温的会话基线（慢速滑动均值）。用于看"偏离了多少"，而不是绝对温度。 */
    val wristTemperatureBaselineC: Float? = null,
    val lightLux: Float? = null,
    /** 环境光在 10 分钟尺度上的变化（lux）。用于将来分析它与天气的关系，现在只记录。 */
    val lightDelta10Min: Float? = null,
    val loggedRows: Int = 0,
    val elapsedSeconds: Long = 0,
    val logFileName: String = "",
    val logHealthy: Boolean = true,
    /** 启动时从历史 CSO 续接回来的样本数：>0 说明 3 小时窗口没从零开始。 */
    val restoredSamples: Int = 0,
    /** 系统步数传感器的累计读数（用于诊断步态是否可靠）。 */
    val stepPulses: Int = 0,
)

/**
 * 气压与体感的采集核心。
 *
 * 设计要点：
 *  1. **采集逻辑不放在界面里**。放 Activity/Compose 里，界面一被回收记录就断，
 *     而且每次重开都会新建一个 CSV，把一个连续动作切成碎片会话。
 *  2. **两个时间尺度**：界面按 2 秒刷新实时读数；样本按 15 秒聚合落盘并喂给引擎。
 *     3 小时窗口下若按 2 秒落点，一天 4 万多行且相邻差异几乎全是噪声。
 *  3. **启动时从历史续接**：否则服务重启后 3 小时窗口要从零攒，等一小时才有可信读数。
 */
object PressureRecorder {

    private const val TAG = "GuanFengRecorder"

    /** 界面实时读数刷新节奏。 */
    private const val LIVE_TICK_MS = 2_000L

    /**
     * 样本聚合与落盘节奏：5 秒一个样本。
     *
     * 这个值是在两个方向上折中出来的：
     *   · 太密（原为 2 秒）：3 小时窗口下一天 4 万多行，且相邻差异几乎全是噪声；
     *   · 太疏（曾试 15 秒）：一趟 45 秒的电梯只剩 3 个样本，轨迹分辨率掉一个量级——
     *     而真机验证过的电梯/楼梯行为（−70.5 米、往返闭合 0.4 米）是在 2 秒采样下测的，
     *     不该在没重新验证的情况下把分辨率砍到 1/7。
     * 5 秒下：3 小时 = 2160 个样本、一天约 1.7 万行（~2 MB），电梯约 9 个样本。
     * 每个样本内部聚合约 40 个原始读数，噪声仍被中位数完全压掉。
     */
    private const val SAMPLE_INTERVAL_MS = 5_000L

    /** 正式趋势窗口。噪声在 3 小时内只折算约 0.013 hPa/h，远低于 0.5 hPa/h 的判定边界。 */
    private const val WINDOW_MS = 3L * 60L * 60L * 1000L

    private const val MIN_SAMPLES = 20

    /**
     * 速评引擎：5 分钟窗口。
     *
     * 主人的判断是对的——「3 小时窗口 + 覆盖率 30%」意味着要等约 54 分钟才有结论，
     * 那是矫枉过正。5 分钟数据确实容易误判，但误判的代价小、等待的代价大。
     * 因此做成两层：速评先给结论，正式窗口成熟后覆盖它。
     */
    private const val FAST_WINDOW_MS = 5L * 60L * 1000L
    private const val FAST_MIN_SAMPLES = 10

    /** 速评窗口短，绝对量门限必须按比例缩小，否则 5 分钟内永远达不到 0.5 hPa 而恒判「平稳」。 */
    private const val FAST_MIN_ABSOLUTE_DELTA_HPA = 0.15f

    /** 竖直积分的时间常数（秒）：足够长到能积累一次爬楼，又短到能抑制漂移。 */
    private const val VERTICAL_TAU_S = 3f

    /** 累计降幅的观察窗：足够长到能记住一场天气过程，又不至于记住上一天的旧账。 */
    private const val RECENT_FALL_WINDOW_MS = 6L * 60L * 60L * 1000L

    /** 少于这么多个样本（约 10 分钟）就不算累计降幅，避免刚启动时报出假降幅。 */
    private const val RECENT_FALL_MIN_SAMPLES = 120

    /** 跨会话状态的落盘节奏（不必每条样本都写盘）。 */
    private const val PERSIST_INTERVAL_MS = 5L * 60L * 1000L

    /** 恢复历史时：超过这个间隔视为断档，断档之前的数据一律不接（跨空洞拟合会造出假趋势）。 */
    private const val MAX_GAP_MS = 5L * 60L * 1000L

    /** 会话文件保留天数已由「保留最近 N 个文件」取代，见 SessionHistory.pruneOldSessions。 */

    /** 内存里保留的样本上限（4 小时容量，比窗口多留一档余量）。 */
    private val MAX_SAMPLES = (4 * 60 * 60 * 1000L / SAMPLE_INTERVAL_MS).toInt()

    /** 腕温是厂商自定义传感器，其 16 个通道的语义没有公开文档，这里取首通道并如实标注未标定。 */
    private const val TYPE_WRIST_TEMPERATURE = 69815

    private const val LIGHT_TREND_WINDOW_MS = 10 * 60 * 1000L
    private const val LIGHT_TREND_MIN_SPAN_MS = 5 * 60 * 1000L

    private val _state = MutableStateFlow(RecorderState())
    val state: StateFlow<RecorderState> = _state.asStateFlow()

    private var scope: CoroutineScope? = null
    private var liveJob: Job? = null
    private var sampleJob: Job? = null
    private var sensorManager: SensorManager? = null
    private var logger: CsvSessionLogger? = null
    /** 供采样子循环写回跨会话状态用（sampleLoop 里拿不到 start() 的局部变量）。 */
    private var appContext: Context? = null
    private var startedAtMs = 0L
    private var started = false

    private val engineFormal = PressureTrendEngine(windowMs = WINDOW_MS, minSamples = MIN_SAMPLES)
    private val engineFast = PressureTrendEngine(
        windowMs = FAST_WINDOW_MS,
        minSamples = FAST_MIN_SAMPLES,
        minAbsoluteDeltaHpa = FAST_MIN_ABSOLUTE_DELTA_HPA,
    )
    private val aggregator = SampleAggregator()
    private val samples = ArrayList<PressureSample>(MAX_SAMPLES + 1)

    /** 累计的高度偏移（hPa）：所有被引擎判为高度事件的步进之和，跨会话持久化。 */
    private var elevationOffsetHpa = 0f
    private var lastPersistMs = 0L

    /** 竖直积分的中间量（只在传感器回调里更新）。 */
    private var verticalVelocity = 0f
    private var verticalDisplacement = 0f
    private var lastAccelNs = 0L

    /** 腕温的慢速滑动均值：腕温绝对值没有天气含义（环境与衣袖混淆最大），只能与自身基线比较。 */
    private var wristTempBaseline: Float? = null
    private var wristTempReadings = 0

    /** 小时归档累加器：整点切换时把上一小时落盘（长期历史靠它，原始文件可以放心裁剪）。 */
    private val hourAccumulator = HourAccumulator()

    /** 天气分量的近期轨迹（只留 [RECENT_FALL_WINDOW_MS]），用于算累计降幅。 */
    private val weatherTrace = ArrayList<Pair<Long, Float>>(MAX_SAMPLES)

    /** 最近 [RECENT_FALL_WINDOW_MS] 内的累计净降幅。 */
    private var recentFallHpa = 0f

    /** 天气过程状态机（主人提出的模型，取代了原先的 6 小时滑动窗口）。 */
    private val episodeTracker = WeatherEpisodeTracker()

    // 传感器原始读数
    private var latestPressure: Float? = null
    private var heartRate: Float? = null
    private var wristTemperature: Float? = null
    private var lightLux: Float? = null
    private val gravity = FloatArray(3)

    // 静息候选：只在没有垂直运动、没有步数的静止样本上采心率
    private val restingCandidates = ArrayList<Float>()
    private var restingBaseline: Float? = null

    // 环境光慢趋势（10 分钟尺度），目前只记录不改判定——没有真实降水标注前不下结论
    private val lightHistory = ArrayDeque<Pair<Long, Float>>()
    private var lightDelta10Min: Float? = null

    @Synchronized
    fun start(context: Context) {
        if (started) {
            Log.i(TAG, "已在采集中，忽略重复启动")
            return
        }
        val applicationContext = context.applicationContext
        val manager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager = manager
        appContext = applicationContext
        logger = CsvSessionLogger(applicationContext)
        samples.clear()
        latestPressure = null
        heartRate = null
        wristTemperature = null
        lightLux = null
        restingCandidates.clear()
        restingBaseline = null
        lightHistory.clear()
        lightDelta10Min = null
        startedAtMs = System.currentTimeMillis()
        started = true
        _state.value = RecorderState(recording = true, logFileName = logger?.displayName ?: "")

        // 历史续接是磁盘 I/O，放到协程里做，别阻塞主线程
        val newScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        scope = newScope
        newScope.launch {
            // 【顺序很重要】先做归档迁移，再裁剪原始文件。
            // 反过来的话，首次运行新版会先把最旧的文件删掉，
            // 那些数据就永远进不了小时归档——等于静默丢失历史。
            if (HourlyArchive.loadAll(applicationContext).isEmpty()) {
                val seeded = HourlyArchive.seed(
                    applicationContext,
                    SessionHistory.loadHourlyRollup(
                        applicationContext,
                        SessionHistory.ALL_HISTORY_DAYS,
                        startedAtMs,
                        // 只迁移带天气分量的行：老数据的原始气压含高度变化，
                        // 混进归档会污染"气压大变化日"统计（宁可少一段历史）
                        onlyDecoupled = true,
                    ),
                )
                if (seeded > 0) Log.i(TAG, "小时归档迁移：从原始文件补齐 $seeded 个小时")
            }

            val restored = runCatching {
                // 单一文件 + 30MB 上限之后不再需要"删旧文件"：
                // 采样文件自己会在超限时紧凑化（丢最旧、留最新），
                // 而小时归档永不删除，长期趋势另有归属。
                SessionHistory.loadRecent(applicationContext, startedAtMs, WINDOW_MS, MAX_GAP_MS)
            }.getOrElse { error ->
                Log.w(TAG, "读取历史样本失败: $error")
                emptyList()
            }
            samples.addAll(restored)
            Log.i(TAG, "从历史续接 ${restored.size} 个样本")

            // 跨会话状态：静息基线 + 累计高度偏移（后者决定天气气压序列的连续性）
            val persisted = SessionHistory.loadRecorderState(applicationContext)
            elevationOffsetHpa = persisted.elevationOffsetHpa
            restingBaseline = persisted.restingHeartRateBpm
            persisted.restingHeartRateBpm?.let { restingCandidates += it }
            Log.i(TAG, "恢复状态：静息基线=${persisted.restingHeartRateBpm} 高度偏移=${persisted.elevationOffsetHpa}")

            // 光照趋势回填：读最近 10 分钟的历史读数，趋势立刻可用（不必再等 10 分钟）
            val since = startedAtMs - LIGHT_TREND_WINDOW_MS
            val seededLight = SessionHistory.loadRecentLight(applicationContext, since, startedAtMs)
            seededLight.forEach { (timestamp, lux) -> lightHistory.addLast(timestamp to lux) }
            seededLight.lastOrNull()?.let { lightLux = it.second }
            Log.i(TAG, "回填光照读数 ${seededLight.size} 个")

            registerSensors(manager)

            _state.value = _state.value.copy(restoredSamples = restored.size)
            liveJob = launch { liveLoop() }
            sampleJob = launch { sampleLoop() }
            Log.i(TAG, "采集已启动，日志文件 ${logger?.path}")
        }
    }

    @Synchronized
    fun stop() {
        if (!started) return
        liveJob?.cancel()
        sampleJob?.cancel()
        scope = null
        liveJob = null
        sampleJob = null
        sensorManager = null
        started = false
        // 停机前把当前这个不完整的小时也归档：否则每次重启都会丢掉最后一段，
        // 而那些正是用户刚刚经历的时间。
        flushCurrentHour()
        _state.value = _state.value.copy(recording = false)
        Log.i(TAG, "采集已停止，本次共落盘 ${logger?.rowCount ?: 0} 行")
    }

    /** 把当前未完成的小时写进归档（停机时调用）。 */
    @Synchronized
    private fun flushCurrentHour() {
        val row = hourAccumulator.snapshot() ?: return
        appContext?.let { HourlyArchive.append(it, row) }
    }

    /**
     * 当前尚未落盘的那个小时的归档行。
     * 关联视图与 AI 报告要用它补上"最后一小时"——归档只在整点切换时追加，
     * 不补的话最近一小时永远是空的。
     */
    fun currentHourRow(): HourlyRow? = hourAccumulator.snapshot()

    /**
     * 重置累计高度基准（一键）。
     *
     * 为什么需要它：累计偏移是持久化的，一旦被误判污染（例如 2026-09-11
     * 那次屋里活动被攒出 +10 米假位移），它会**一直留在磁盘上**。
     * 恒定偏移不影响"变化量"，所以对齐度没有危害；但绝对值显示会一直偏，
     * 而且下一次真实爬楼会从这个错误基准继续累加。给一个显式复位入口，
     * 比让它默默烂在那里好。
     */
    @Synchronized
    fun resetElevationBaseline() {
        elevationOffsetHpa = 0f
        verticalVelocity = 0f
        verticalDisplacement = 0f
        appContext?.let { context ->
            SessionHistory.saveRecorderState(
                context = context,
                restingHeartRateBpm = restingBaseline,
                elevationOffsetHpa = 0f,
            )
        }
        _state.value = _state.value.copy(weatherPressureHpa = _state.value.pressureHpa)
        Log.i(TAG, "高度基准已重置")
    }

    fun isRunning(): Boolean = started

    /**
     * 竖直方向的带泄漏二次积分，用来估**净位移**。
     *
     * 为什么要它：判断"人是否真的在垂直运动"不能看加速度峰值——
     * 挥一下手就能到 10 m/s² 以上，比爬楼还大；而位移是持续与否的差别：
     * 往复运动互相抵消、净位移趋近 0，爬楼/电梯则稳定累积。
     *
     * 带泄漏（时间常数 [VERTICAL_TAU_S] 秒）是为了抑制积分漂移：
     * 目的是"有没有持续位移"，不是精确轨迹，所以宁可让久远的历史衰减掉。
     */
    private fun integrateVertical(eventTimestampNs: Long, verticalAccel: Float) {
        val dt = if (lastAccelNs == 0L) {
            0f
        } else {
            ((eventTimestampNs - lastAccelNs) / 1_000_000_000.0).toFloat().coerceIn(0f, 0.2f)
        }
        lastAccelNs = eventTimestampNs
        if (dt <= 0f) return
        val leak = kotlin.math.exp(-dt / VERTICAL_TAU_S)
        verticalVelocity = (verticalVelocity + verticalAccel * dt) * leak
        verticalDisplacement = (verticalDisplacement + verticalVelocity * dt) * leak
        aggregator.setVerticalDisplacement(verticalDisplacement)
    }

    private fun registerSensors(manager: SensorManager) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_PRESSURE -> {
                        latestPressure = event.values[0]
                        aggregator.addPressure(event.values[0])
                    }

                    Sensor.TYPE_LIGHT -> lightLux = event.values[0]

                    Sensor.TYPE_HEART_RATE -> heartRate = event.values[0]

                    TYPE_WRIST_TEMPERATURE -> {
                        val values = event.values
                        if (values.isNotEmpty() && values[0] > 1f) wristTemperature = values[0]
                    }

                    Sensor.TYPE_GRAVITY -> event.values.copyInto(gravity)

                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        val gMagnitude = sqrt(
                            gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]
                        )
                        if (gMagnitude > 0.1f) {
                            // 投影到重力方向，只保留垂直分量：平地走路的水平摆动不算高度证据。
                            val vertical = (
                                event.values[0] * gravity[0] +
                                    event.values[1] * gravity[1] +
                                    event.values[2] * gravity[2]
                                ) / gMagnitude
                            aggregator.addVerticalAccel(abs(vertical))
                            integrateVertical(event.timestamp, vertical)
                        }
                    }

                    Sensor.TYPE_STEP_DETECTOR -> {
                        aggregator.addStep()
                        stepPulses++
                    }                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        val wanted = listOf(
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_HEART_RATE,
            TYPE_WRIST_TEMPERATURE,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_STEP_DETECTOR,
        )
        var registered = 0
        wanted.forEach { type ->
            val sensor = runCatching { manager.getDefaultSensor(type) }.getOrNull()
            if (sensor == null) {
                Log.w(TAG, "拿不到传感器 type=$type")
                return@forEach
            }
            val ok = runCatching {
                manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }.getOrElse { error ->
                Log.w(TAG, "注册 type=$type 抛异常: $error")
                false
            }
            if (ok) registered++ else Log.w(TAG, "注册 type=$type 失败")
        }
        Log.i(TAG, "已注册 $registered/${wanted.size} 个传感器")
    }

    private var stepPulses = 0

    /** 界面实时读数：只更新「此刻」的值，不碰引擎与落盘。 */
    private suspend fun liveLoop() {
        while (true) {
            delay(LIVE_TICK_MS)
            val now = System.currentTimeMillis()

            // 静息基线：只用「这一段聚合区间内没有垂直运动、没有步数」的静止样本，
            // 取低分位数。瞬时值直接当静息心率是错的（爬楼时也会读到 120）。
            val currentHeartRate = heartRate
            if (currentHeartRate != null && currentHeartRate > 20f &&
                aggregator.currentSteps == 0 && aggregator.currentAccelPeak < 0.35f &&
                // 硬上限：静止但心率 >100 的多半是组间休息/刚运动完，
                // 不能算静息样本。取 10 百分位已经能挡住大部分，这条是第二道闸。
                currentHeartRate < 100f
            ) {
                restingCandidates += currentHeartRate
                if (restingCandidates.size > 900) {
                    restingCandidates.removeAt(0)
                }
                restingBaseline = percentile(restingCandidates, 0.1f)
            }

            // 环境光 10 分钟慢趋势：将来用于考察「光照下降 + 气压下降」是否同时出现，
            // 但在有真实降水标注之前，它只记录、不参与任何判定。
            lightLux?.let { lux ->
                lightHistory.addLast(now to lux)
                while (lightHistory.isNotEmpty() &&
                    now - lightHistory.first().first > LIGHT_TREND_WINDOW_MS
                ) {
                    lightHistory.removeFirst()
                }
                val oldest = lightHistory.firstOrNull()
                lightDelta10Min = if (oldest != null && now - oldest.first >= LIGHT_TREND_MIN_SPAN_MS) {
                    lux - oldest.second
                } else {
                    null
                }
            }

            // 腕温基线：慢速滑动均值。绝对值没有天气含义（环境与衣袖是最大混淆项），
            // 所以只在攒够读数之后才给出基线，避免刚启动时报出一次假「偏离」。
            wristTemperature?.let { temperature ->
                wristTempReadings++
                val previous = wristTempBaseline
                wristTempBaseline = if (previous == null) temperature else previous * 0.98f + temperature * 0.02f
            }

            _state.value = _state.value.copy(
                recording = true,
                pressureHpa = latestPressure,
                weatherPressureHpa = latestPressure?.minus(elevationOffsetHpa),
                heartRateBpm = currentHeartRate,
                restingHeartRateBpm = restingBaseline,
                wristTemperatureC = wristTemperature,
                wristTemperatureBaselineC = wristTempBaseline?.takeIf { wristTempReadings >= 20 },
                lightLux = lightLux,
                lightDelta10Min = lightDelta10Min,
                elapsedSeconds = (now - startedAtMs) / 1000,
            )
        }
    }

    /** 样本节奏：聚合 → 喂引擎 → 落盘。3 小时窗口下的真正输入。 */
    private suspend fun sampleLoop() {
        var nextDueMs = System.currentTimeMillis() + SAMPLE_INTERVAL_MS
        while (true) {
            val wait = nextDueMs - System.currentTimeMillis()
            if (wait > 0) delay(wait)
            nextDueMs += SAMPLE_INTERVAL_MS

            val now = System.currentTimeMillis()
            val sample = aggregator.flush(now) ?: continue

            samples += sample
            while (samples.size > MAX_SAMPLES) {
                samples.removeAt(0)
            }

            val formal = engineFormal.compute(samples)
            val fast = engineFast.compute(samples)

            // 跨窗口累积高度偏移：引擎只报「最后一步」被归为高度事件的量，
            // 由调用方累加起来，就得到与窗口无关的、只含天气分量的气压。
            formal.lastElevationStepHpa?.let { elevationOffsetHpa += it }
            val weatherPressure = sample.pressureHpa - elevationOffsetHpa

            // 记住"最近降过多少"：取近期最高点与当前值之差。
            // 气压降完转入长时间平稳正是雨在下/雨将至的形态，只看窗口斜率会把它忘掉。
            weatherTrace.add(sample.timestampMs to weatherPressure)
            while (weatherTrace.size > 1 &&
                sample.timestampMs - weatherTrace[0].first > RECENT_FALL_WINDOW_MS
            ) {
                weatherTrace.removeAt(0)
            }
            recentFallHpa = if (weatherTrace.size >= RECENT_FALL_MIN_SAMPLES) {
                weatherPressure - weatherTrace.maxOf { it.second }
            } else {
                0f
            }
            // 过程状态机：只看天气分量，与高度无关
            val episode = episodeTracker.add(sample.timestampMs, weatherPressure)

            // 小时归档：整点切换时把上一小时落盘。
            // 体感数据也一并归档——AI 报告需要它们来判断混淆因素（例如头痛是否来自发热）。
            // 心率/腕温/光照取 liveLoop 写入的最新读数（它们比 5 秒聚合节奏快）。
            val latest = _state.value
            hourAccumulator.add(
                timestampMs = sample.timestampMs,
                weatherHpa = weatherPressure,
                rawHpa = sample.pressureHpa,
                heartRateBpm = latest.heartRateBpm,
                restingHeartRateBpm = restingBaseline,
                wristTempC = latest.wristTemperatureC,
                lightLux = latest.lightLux,
            )?.let { completed ->
                appContext?.let { HourlyArchive.append(it, completed) }
            }

            val restingNow = aggregator.currentSteps == 0 && aggregator.currentAccelPeak < 0.35f
            val written = logger?.append(
                timestampMs = sample.timestampMs,
                pressureHpa = sample.pressureHpa,
                verticalAccel = sample.verticalAccel,
                stepsInWindow = sample.stepsInWindow,
                trend = formal,
                restingHeartRateBpm = restingBaseline,
                lightDelta10Min = lightDelta10Min,
                weatherPressureHpa = weatherPressure,
                lightLux = lightLux,
                verticalDisplacementM = sample.verticalDisplacementM,
            ) ?: false

            if (now - lastPersistMs >= PERSIST_INTERVAL_MS) {
                lastPersistMs = now
                appContext?.let { context ->
                    SessionHistory.saveRecorderState(
                        context = context,
                        restingHeartRateBpm = restingBaseline,
                        elevationOffsetHpa = elevationOffsetHpa,
                    )
                    // 耗电自记录：电量 + 本进程 CPU 时间，供事后归因
                    PowerLogger.append(
                        context = context,
                        appElapsedMs = now - startedAtMs,
                        samplesLogged = logger?.rowCount ?: 0,
                    )
                }
            }

            // 主动提醒：转坏到「高」时发一条通知（声音与震动交给系统）。
            // 只在升级时发一次并带冷却，避免变成噪音源——被关掉通知的提醒等于不存在。
            runCatching {
                val formalAssessment = WeatherRule.assess(formal, recentFallHpa, episode)
                val fastAssessment = WeatherRule.assess(fast, recentFallHpa, episode)
                val active = if (formalAssessment.likelihood != RainLikelihood.UNKNOWN) {
                    formalAssessment to formal
                } else {
                    fastAssessment to fast
                }
                appContext?.let { context ->
                    TrendNotifier.maybeNotify(
                        context = context,
                        likelihood = active.first.likelihood,
                        assessment = active.first,
                        trend = active.second,
                    )
                }
            }

            _state.value = _state.value.copy(
                recording = true,
                trend = formal,
                trendFast = fast,
                isResting = restingNow,
                recentFallHpa = recentFallHpa,
                episode = episode,
                weatherPressureHpa = weatherPressure,
                loggedRows = logger?.rowCount ?: 0,
                logFileName = logger?.displayName ?: "",
                logHealthy = written,
            )
        }
    }

    /** 取低分位数：静息心率本质上是「心率分布的下沿」，不是平均值。 */
    private fun percentile(values: List<Float>, fraction: Float): Float {
        if (values.isEmpty()) return 0f
        val sorted = values.sorted()
        val index = ((sorted.size - 1) * fraction).toInt().coerceIn(0, sorted.size - 1)
        return sorted[index]
    }
}
