package com.yisiyun.guanfeng.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.yisiyun.guanfeng.core.PressureSample
import com.yisiyun.guanfeng.core.PressureTrendEngine
import com.yisiyun.guanfeng.core.SampleAggregator
import com.yisiyun.guanfeng.core.TrendResult
import com.yisiyun.guanfeng.log.CsvSessionLogger
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
    val trend: TrendResult? = null,
    /** 传感器此刻的瞬时心率（`TYPE_HEART_RATE` 直接给的值，不是静息心率）。 */
    val heartRateBpm: Float? = null,
    /**
     * 静息基线：只采「没有垂直运动、没有步数」的静止样本，取其低分位数。
     * 这才是「静息心率」该有的来历——瞬时值直接当静息心率是错的。
     */
    val restingHeartRateBpm: Float? = null,
    val wristTemperatureC: Float? = null,
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

    /** 样本聚合与落盘节奏：15 秒一个样本，3 小时 = 720 个。 */
    private const val SAMPLE_INTERVAL_MS = 15_000L

    /** 正式趋势窗口。噪声在 3 小时内只折算约 0.013 hPa/h，远低于 0.5 hPa/h 的判定边界。 */
    private const val WINDOW_MS = 3L * 60L * 60L * 1000L

    private const val MIN_SAMPLES = 20

    /** 恢复历史时：超过这个间隔视为断档，断档之前的数据一律不接（跨空洞拟合会造出假趋势）。 */
    private const val MAX_GAP_MS = 5L * 60L * 1000L

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
    private var startedAtMs = 0L
    private var started = false

    private val engine = PressureTrendEngine(windowMs = WINDOW_MS, minSamples = MIN_SAMPLES)
    private val aggregator = SampleAggregator()
    private val samples = ArrayList<PressureSample>(MAX_SAMPLES + 1)

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
            val restored = runCatching {
                SessionHistory.loadRecent(applicationContext, startedAtMs, WINDOW_MS, MAX_GAP_MS)
            }.getOrElse { error ->
                Log.w(TAG, "读取历史样本失败: $error")
                emptyList()
            }
            samples.addAll(restored)
            Log.i(TAG, "从历史续接 ${restored.size} 个样本")

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
        _state.value = _state.value.copy(recording = false)
        Log.i(TAG, "采集已停止，本次共落盘 ${logger?.rowCount ?: 0} 行")
    }

    fun isRunning(): Boolean = started

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
                aggregator.currentSteps == 0 && aggregator.currentAccelPeak < 0.35f
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

            _state.value = _state.value.copy(
                recording = true,
                pressureHpa = latestPressure,
                heartRateBpm = currentHeartRate,
                restingHeartRateBpm = restingBaseline,
                wristTemperatureC = wristTemperature,
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

            val trend = engine.compute(samples)

            val written = logger?.append(
                timestampMs = sample.timestampMs,
                pressureHpa = sample.pressureHpa,
                verticalAccel = sample.verticalAccel,
                stepsInWindow = sample.stepsInWindow,
                trend = trend,
                restingHeartRateBpm = restingBaseline,
                lightDelta10Min = lightDelta10Min,
            ) ?: false

            _state.value = _state.value.copy(
                recording = true,
                trend = trend,
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
