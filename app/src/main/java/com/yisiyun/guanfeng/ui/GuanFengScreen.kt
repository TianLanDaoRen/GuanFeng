package com.yisiyun.guanfeng.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.core.PressureSample
import com.yisiyun.guanfeng.core.PressureTrendEngine
import com.yisiyun.guanfeng.core.RainLikelihood
import com.yisiyun.guanfeng.core.TrendConfidence
import com.yisiyun.guanfeng.core.TrendGrade
import com.yisiyun.guanfeng.core.TrendResult
import com.yisiyun.guanfeng.core.WeatherRule
import com.yisiyun.guanfeng.log.CsvSessionLogger
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt
import kotlinx.coroutines.delay

/**
 * 观风主界面 —— MVP-2 真机验证版 + 会话落盘。
 *
 * 数据流：气压/重力/线性加速度/步数 → 每 TICK_MS 落一个样本 → 引擎算趋势与高度解耦
 * → 同时写进 CSV。落盘是刚需：戴上手表走动时 USB 会断开，logcat 靠不住。
 *
 * 注意演示窗口只取数分钟（正式版为 3 小时），界面上如实标注窗口跨度，
 * 不假装这是成品读数。
 */
private const val TICK_MS = 2_000L

/** 演示窗口取 6 分钟：够跨过 30% 覆盖率闸门（约 1.8 分钟），又不至于让测试等太久。 */
private const val DEMO_WINDOW_MS = 6 * 60 * 1000L
private const val DEMO_MIN_SAMPLES = 5

@Composable
fun GuanFengScreen() {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val engine = remember {
        PressureTrendEngine(windowMs = DEMO_WINDOW_MS, minSamples = DEMO_MIN_SAMPLES)
    }
    val logger = remember { CsvSessionLogger(context) }
    val startedAt = remember { System.currentTimeMillis() }

    var latestPressure by remember { mutableStateOf<Float?>(null) }
    // 采样窗口内的垂直加速度峰值：区间证据比瞬时读数更能代表「这一段时间里有没有动」。
    var verticalAccelPeak by remember { mutableStateOf(0f) }
    var stepTicks by remember { mutableStateOf(0) }
    var pressureCallbackCount by remember { mutableStateOf(0) }
    val samples = remember { mutableStateListOf<PressureSample>() }
    var trend by remember { mutableStateOf<TrendResult?>(null) }
    var loggedRows by remember { mutableStateOf(0) }
    var logHealthy by remember { mutableStateOf(true) }
    var elapsedSeconds by remember { mutableStateOf(0L) }

    DisposableEffect(Unit) {
        val gravity = FloatArray(3)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_PRESSURE -> {
                        latestPressure = event.values[0]
                        pressureCallbackCount++
                    }

                    Sensor.TYPE_GRAVITY -> {
                        event.values.copyInto(gravity)
                    }

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

                    Sensor.TYPE_STEP_DETECTOR -> stepTicks++
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        val registered = ArrayList<Sensor>()
        listOf(
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_STEP_DETECTOR
        ).forEach { type ->
            sensorManager.getDefaultSensor(type)?.let { sensor ->
                if (sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)) {
                    registered += sensor
                }
            }
        }

        onDispose {
            registered.forEach { sensorManager.unregisterListener(listener, it) }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(TICK_MS)
            val pressure = latestPressure ?: continue

            val accelForSample = verticalAccelPeak
            val stepsForSample = stepTicks
            verticalAccelPeak = 0f
            stepTicks = 0

            val timestamp = System.currentTimeMillis()
            samples += PressureSample(
                timestampMs = timestamp,
                pressureHpa = pressure,
                verticalAccel = accelForSample,
                stepsInWindow = stepsForSample
            )
            while (samples.size > 400) {
                samples.removeAt(0)
            }

            val computed = engine.compute(samples.toList())
            trend = computed
            elapsedSeconds = (timestamp - startedAt) / 1000

            val written = logger.append(
                timestampMs = timestamp,
                pressureHpa = pressure,
                verticalAccel = accelForSample,
                stepsInWindow = stepsForSample,
                trend = computed
            )
            logHealthy = written
            if (written) loggedRows = logger.rowCount
        }
    }

    val assessment = trend?.let { WeatherRule.assess(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("观风", color = Color.White, fontSize = 16.sp)
            Spacer(Modifier.fillMaxWidth(0.08f))
            Text(
                text = latestPressure?.let { "%.2f hPa".format(it) } ?: "等气压…",
                color = Color(0xFFB0B0B0),
                fontSize = 11.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        val likelihood = assessment?.likelihood ?: RainLikelihood.UNKNOWN
        Text(
            text = likelihood.label,
            color = when (likelihood) {
                RainLikelihood.HIGH -> Color(0xFFFF7A6B)
                RainLikelihood.MEDIUM -> Color(0xFFF2C14E)
                RainLikelihood.LOW -> Color(0xFF6EE7A8)
                RainLikelihood.UNKNOWN -> Color(0xFF909090)
            },
            fontSize = 32.sp
        )
        Text("风雨倾向", color = Color(0xFF909090), fontSize = 10.sp)

        Spacer(Modifier.height(4.dp))
        Text(assessment?.advice ?: "攒样本中…", color = Color.White, fontSize = 12.sp)
        Text(
            text = assessment?.rationale ?: "窗口内样本不足 $DEMO_MIN_SAMPLES 个",
            color = Color(0xFF909090),
            fontSize = 9.sp,
            lineHeight = 12.sp
        )

        Spacer(Modifier.height(8.dp))

        val grade = trend?.grade ?: TrendGrade.INSUFFICIENT
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("趋势 ", color = Color(0xFF909090), fontSize = 10.sp)
            Text(grade.label, color = Color.White, fontSize = 10.sp)
            Spacer(Modifier.fillMaxWidth(0.12f))
            Text("%.2f hPa/h".format(trend?.rateHpaPerHour ?: 0f), color = Color.White, fontSize = 10.sp)
        }
        InfoRow("ΔP(3h) 外推", "%+.2f hPa".format(trend?.deltaHpaPer3h ?: 0f))
        InfoRow("窗口跨度", "%.1f 分钟（演示）".format(trend?.windowMinutes ?: 0f))
        InfoRow("窗口内实测变压", "%+.2f hPa".format(trend?.observedDeltaHpa ?: 0f))
        InfoRow("覆盖率", "%.0f%%".format((trend?.coverageFraction ?: 0f) * 100f))
        InfoRow("置信度", (trend?.confidence ?: TrendConfidence.INSUFFICIENT).label)

        Spacer(Modifier.height(8.dp))
        Text("高度解耦", color = Color(0xFFF2C14E), fontSize = 10.sp)
        InfoRow("天气样本", "${trend?.weatherSamples ?: 0} 个")
        InfoRow("解耦样本数", "${trend?.elevationEvents ?: 0} 个")
        InfoRow("累计垂直位移", "%+.1f 米".format(trend?.elevationMeters ?: 0f))
        InfoRow("拟合优度 R²", "%.3f".format(trend?.fitRSquared ?: 0f))

        Spacer(Modifier.height(8.dp))
        Text(
            text = if (logHealthy) "记录中 · 已落盘" else "写入失败",
            color = if (logHealthy) Color(0xFF6EE7A8) else Color(0xFFFF7A6B),
            fontSize = 10.sp
        )
        InfoRow("日志文件", logger.displayName)
        InfoRow("已写入", "$loggedRows 行")
        InfoRow(
            "已运行",
            "%d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60)
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "测试指引：戴着它下楼 / 上楼或坐电梯，本界面保持在前台（屏幕已强制常亮），" +
                "回来用 adb pull 取 CSV",
            color = Color(0xFFB0B0B0),
            fontSize = 8.sp,
            lineHeight = 11.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "气压回调 ${pressureCallbackCount} 次 · 采样每 ${TICK_MS / 1000}s 一点 · " +
                "风雨倾向为启发式规则，未用真实降水校准",
            color = Color(0xFF707070),
            fontSize = 8.sp,
            lineHeight = 11.sp
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label + "  ", color = Color(0xFF909090), fontSize = 9.sp, lineHeight = 13.sp)
        Text(value, color = Color.White, fontSize = 9.sp, lineHeight = 13.sp)
    }
}
