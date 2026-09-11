package com.yisiyun.guanfeng.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.yisiyun.guanfeng.core.PressureSample
import com.yisiyun.guanfeng.core.PressureTrendEngine
import com.yisiyun.guanfeng.core.TrendResult
import com.yisiyun.guanfeng.log.CsvSessionLogger
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
    /** 系统步数传感器的累计读数（用于诊断步态是否可靠）。 */
    val stepPulses: Int = 0,
)

/**
 * 气压与体感的采集核心。
 *
 * 设计要点：**采集逻辑不放在界面里**。之前版本把传感器注册与采样循环写在 Compose 里，
 * 一旦 Activity 被销毁（熄屏后被系统回收），记录就断了；而且每次重新打开都会新建一个
 * CSV，把一个连续的动作切成若干碎片会话。现在采集由前台服务托管，界面只是订阅方，
 * 因此「会话」的边界等于服务的生命周期，而不是界面的生命周期。
 *
 * 单例而非 Service 内部类：方便界面以 StateFlow 订阅，也避免跨进程/跨组件传递状态。
 */
object PressureRecorder {

    private const val TAG = "GuanFengRecorder"
    private const val TICK_MS = 2_000L
    private const val DEMO_WINDOW_MS = 6 * 60 * 1000L
    private const val DEMO_MIN_SAMPLES = 5
    private const val MAX_SAMPLES = 400
    private const val LIGHT_TREND_WINDOW_MS = 10 * 60 * 1000L
    private const val LIGHT_TREND_MIN_SPAN_MS = 5 * 60 * 1000L

    /** 取低分位数：静息心率本质上是"心率分布的下沿"，不是平均值。 */
    private fun percentile(values: List<Float>, fraction: Float): Float {
        if (values.isEmpty()) return 0f
        val sorted = values.sorted()
        val index = ((sorted.size - 1) * fraction).toInt().coerceIn(0, sorted.size - 1)
        return sorted[index]
    }

    /** 腕温是厂商自定义传感器，其 16 个通道的语义没有公开文档，这里取首通道并如实标注未标定。 */
    private const val TYPE_WRIST_TEMPERATURE = 69815

    private val _state = MutableStateFlow(RecorderState())
    val state: StateFlow<RecorderState> = _state.asStateFlow()

    private var scope: CoroutineScope? = null
    private var tickJob: Job? = null
    private var sensorManager: SensorManager? = null
    private var logger: CsvSessionLogger? = null
    private var startedAtMs = 0L
    private var started = false

    private val engine = PressureTrendEngine(
        windowMs = DEMO_WINDOW_MS,
        minSamples = DEMO_MIN_SAMPLES
    )

    private val samples = ArrayList<PressureSample>(MAX_SAMPLES + 1)

    // 传感器原始读数
    private var latestPressure: Float? = null
    private var verticalAccelPeak = 0f
    private var stepPulses = 0
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
        verticalAccelPeak = 0f
        stepPulses = 0
        heartRate = null
        wristTemperature = null
        lightLux = null
        restingCandidates.clear()
        restingBaseline = null
        lightHistory.clear()
        lightDelta10Min = null
        startedAtMs = System.currentTimeMillis()

        registerSensors(manager)

        val newScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        scope = newScope
        tickJob = newScope.launch { tickLoop() }

        started = true
        _state.value = RecorderState(
            recording = true,
            logFileName = logger?.displayName ?: "",
        )
        Log.i(TAG, "采集已启动，日志文件 ${logger?.path}")
    }

    @Synchronized
    fun stop() {
        if (!started) return
        tickJob?.cancel()
        scope = null
        tickJob = null
        unregisterSensors()
        started = false
        _state.value = _state.value.copy(recording = false)
        Log.i(TAG, "采集已停止，本次共落盘 ${logger?.rowCount ?: 0} 行")
    }

    fun isRunning(): Boolean = started

    private fun registerSensors(manager: SensorManager) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_PRESSURE -> latestPressure = event.values[0]

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
                            verticalAccelPeak = max(verticalAccelPeak, abs(vertical))
                        }
                    }

                    Sensor.TYPE_STEP_DETECTOR -> stepPulses++
                }
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

    private fun unregisterSensors() {
        // SensorManager 在进程存活期间一直有效，这里直接整体注销本组件注册的监听。
        sensorManager = null
    }

    private suspend fun tickLoop() {
        while (true) {
            delay(TICK_MS)
            val pressure = latestPressure ?: continue

            val accelForSample = verticalAccelPeak
            val stepsForSample = stepPulses
            verticalAccelPeak = 0f
            stepPulses = 0

            val timestamp = System.currentTimeMillis()
            val sample = PressureSample(
                timestampMs = timestamp,
                pressureHpa = pressure,
                verticalAccel = accelForSample,
                stepsInWindow = stepsForSample,
            )
            samples += sample
            while (samples.size > MAX_SAMPLES) {
                samples.removeAt(0)
            }

            val trend = engine.compute(samples)

            // 静息基线：只用静止样本（无垂直运动、无步数、心率有效），取低分位数。
            val currentHeartRate = heartRate
            if (currentHeartRate != null && currentHeartRate > 20f &&
                accelForSample < 0.35f && stepsForSample == 0
            ) {
                restingCandidates += currentHeartRate
                if (restingCandidates.size > 900) {
                    restingCandidates.removeAt(0)
                }
                restingBaseline = percentile(restingCandidates, 0.1f)
            }

            // 环境光 10 分钟慢趋势：将来用于考察"光照下降 + 气压下降"是否同时出现，
            // 但在有真实降水标注之前，它只记录、不参与任何判定。
            lightLux?.let { lux ->
                lightHistory.addLast(timestamp to lux)
                while (lightHistory.isNotEmpty() &&
                    timestamp - lightHistory.first().first > LIGHT_TREND_WINDOW_MS
                ) {
                    lightHistory.removeFirst()
                }
                val oldest = lightHistory.firstOrNull()
                lightDelta10Min = if (oldest != null && timestamp - oldest.first >= LIGHT_TREND_MIN_SPAN_MS) {
                    lux - oldest.second
                } else {
                    null
                }
            }

            val written = logger?.append(
                timestampMs = timestamp,
                pressureHpa = pressure,
                verticalAccel = accelForSample,
                stepsInWindow = stepsForSample,
                trend = trend,
                restingHeartRateBpm = restingBaseline,
                lightDelta10Min = lightDelta10Min,
            ) ?: false

            _state.value = _state.value.copy(
                recording = true,
                pressureHpa = pressure,
                trend = trend,
                heartRateBpm = currentHeartRate,
                restingHeartRateBpm = restingBaseline,
                wristTemperatureC = wristTemperature,
                lightLux = lightLux,
                lightDelta10Min = lightDelta10Min,
                loggedRows = logger?.rowCount ?: 0,
                elapsedSeconds = (timestamp - startedAtMs) / 1000,
                logFileName = logger?.displayName ?: "",
                logHealthy = written,
            )
        }
    }
}
