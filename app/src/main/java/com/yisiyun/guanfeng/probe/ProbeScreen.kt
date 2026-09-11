package com.yisiyun.guanfeng.probe

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val TAG = "GuanFengProbe"

/** OWW221 实测圆角（屏幕像素口径）：64px，四角一致。安全内缩系数取官方公式 1-cos45° ≈ 0.29。 */
private const val CORNER_FALLBACK_PX = 64
private const val CORNER_SAFE_FRACTION = 0.29f

/**
 * android.hardware.Sensor.TYPE_WRIST_TILT_GESTURE = 26。
 * 该常量不在公开 SDK 里（编译期报 Unresolved reference），因此用字面量并在此标注来源。
 * 相对地，Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT(34) 是公开常量，可直接引用。
 */
private const val TYPE_WRIST_TILT_GESTURE = 26

private data class Target(val type: Int, val label: String)

/** AOSP 公开类型：第三方应用理论上都能注册，逐条实测确认。 */
private val STANDARD_TARGETS = listOf(
    Target(Sensor.TYPE_PRESSURE, "气压 pressure"),
    Target(Sensor.TYPE_LIGHT, "环境光 light"),
    Target(Sensor.TYPE_ACCELEROMETER, "加速度 accelerometer"),
    Target(Sensor.TYPE_GYROSCOPE, "陀螺 gyroscope"),
    Target(Sensor.TYPE_MAGNETIC_FIELD, "地磁 magnetic_field"),
    Target(Sensor.TYPE_HEART_RATE, "心率 heart_rate"),
    Target(Sensor.TYPE_STEP_COUNTER, "计步 step_counter"),
    Target(TYPE_WRIST_TILT_GESTURE, "抬腕 wrist_tilt"),
    Target(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT, "离腕 offbody"),
)

/**
 * 厂商自定义类型号（dumpsys sensorservice 实测到的）：
 * 硬件存在不等于普通应用拿得到，这一组是本次探针的核心悬念。
 */
private val VENDOR_TARGETS = listOf(
    Target(65572, "PPG"),
    Target(69632, "PPG_HRM"),
    Target(69633, "PPG_SPO 血氧"),
    Target(69634, "PPG_WEAR 佩戴"),
    Target(69636, "ECG"),
    Target(69815, "WRIST_TEMPERATURE 腕温"),
    Target(69801, "PPG_RAW_G0 原始通道"),
    Target(69813, "GPS_MAIN"),
    Target(69816, "CUST_ACC"),
)

/** 要持续读数的四路（体感双轨 + 气压内核的最小集合）。 */
private val LIVE_TARGETS = listOf(
    Target(Sensor.TYPE_PRESSURE, "气压"),
    Target(Sensor.TYPE_LIGHT, "光照"),
    Target(Sensor.TYPE_ACCELEROMETER, "加速度"),
    Target(Sensor.TYPE_HEART_RATE, "心率"),
)

@Composable
fun ProbeScreen(bodySensorsGranted: Boolean) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val density = LocalDensity.current
    // 四角安全内缩：32dp × 0.29 ≈ 9.3dp（64px ÷ density 2.0 = 32dp）
    val cornerInsetDp = with(density) { (CORNER_FALLBACK_PX / density.density) * CORNER_SAFE_FRACTION }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = cornerInsetDp.dp + 4.dp, vertical = 8.dp)
    ) {
        Title("观风 · 探针")
        Caption("BODY_SENSORS = ${if (bodySensorsGranted) "已授权" else "未授权"}")

        Spacer(Modifier.height(6.dp))
        SectionHeading("① 屏幕档案")
        ScreenFacts(cornerInsetDp)

        Spacer(Modifier.height(6.dp))
        SectionHeading("② 标准传感器可用性")
        AvailabilityList(sensorManager, STANDARD_TARGETS)

        Spacer(Modifier.height(6.dp))
        SectionHeading("③ 厂商自定义传感器")
        AvailabilityList(sensorManager, VENDOR_TARGETS)

        Spacer(Modifier.height(6.dp))
        SectionHeading("④ 实时读数")
        LiveReadings(sensorManager, bodySensorsGranted)

        Spacer(Modifier.height(6.dp))
        SectionHeading("⑤ 键盘探针")
        KeyboardProbe()

        Spacer(Modifier.height(6.dp))
        SectionHeading("⑥ 厂商传感器注册实测")
        VendorRegisterTest(sensorManager)

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun ScreenFacts(cornerInsetDp: Float) {
    val context = LocalContext.current
    val metrics = remember { context.resources.displayMetrics }
    val config = remember { context.resources.configuration }
    val round = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) config.isScreenRound else false

    KeyValue("像素", "${metrics.widthPixels} x ${metrics.heightPixels} px")
    KeyValue("密度", "density=${metrics.density} · ${metrics.densityDpi} dpi")
    KeyValue("dp 尺寸", "${config.screenWidthDp} x ${config.screenHeightDp} dp")
    KeyValue("圆屏判定", "isScreenRound=$round")
    KeyValue("四角内缩", "%.1f dp".format(cornerInsetDp))
    KeyValue("系统", "API ${Build.VERSION.SDK_INT} · ${Build.MODEL}")
}

@Composable
private fun AvailabilityList(sensorManager: SensorManager, targets: List<Target>) {
    targets.forEach { target ->
        val sensor = remember(target.type) {
            val found = runCatching { sensorManager.getDefaultSensor(target.type) }.getOrNull()
            Log.i(TAG, "getDefaultSensor(${target.type}) [${target.label}] -> ${found?.name ?: "null"}")
            found
        }
        val mark = if (sensor != null) "✓" else "✗"
        val color = if (sensor != null) Color(0xFF6EE7A8) else Color(0xFF8A8A8A)
        Text(
            text = "$mark ${target.type}  ${target.label}${if (sensor != null) "  (${sensor.name})" else ""}",
            color = color,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LiveReadings(sensorManager: SensorManager, bodySensorsGranted: Boolean) {
    val readings = remember { mutableStateMapOf<Int, String>() }
    val counts = remember { mutableStateMapOf<Int, Int>() }

    DisposableEffect(bodySensorsGranted) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val target = LIVE_TARGETS.firstOrNull { it.type == event.sensor.type } ?: return
                val formatted = when (event.sensor.type) {
                    Sensor.TYPE_PRESSURE -> "%.2f hPa".format(event.values[0])
                    Sensor.TYPE_LIGHT -> "%.0f lux".format(event.values[0])
                    Sensor.TYPE_ACCELEROMETER ->
                        "%.2f / %.2f / %.2f".format(event.values[0], event.values[1], event.values[2])
                    Sensor.TYPE_HEART_RATE -> "%.0f bpm".format(event.values[0])
                    else -> event.values.joinToString(" ") { "%.2f".format(it) }
                }
                readings[event.sensor.type] = formatted
                val next = (counts[event.sensor.type] ?: 0) + 1
                counts[event.sensor.type] = next
                // 抽样打日志，避免刷爆 logcat
                if (next <= 3 || next % 50 == 0) {
                    Log.i(TAG, "LIVE ${target.label} = $formatted (第 $next 次回调)")
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.i(TAG, "onAccuracyChanged ${sensor?.name} accuracy=$accuracy")
            }
        }

        val registered = mutableListOf<Sensor>()
        LIVE_TARGETS.forEach { target ->
            if (target.type == Sensor.TYPE_HEART_RATE && !bodySensorsGranted) {
                Log.i(TAG, "跳过心率：BODY_SENSORS 未授权")
                return@forEach
            }
            val sensor = runCatching { sensorManager.getDefaultSensor(target.type) }.getOrNull()
            if (sensor == null) {
                Log.i(TAG, "跳过 ${target.label}：getDefaultSensor 返回 null")
                return@forEach
            }
            val ok = runCatching {
                sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }.getOrElse { error ->
                Log.w(TAG, "registerListener ${target.label} 抛异常: $error")
                false
            }
            Log.i(TAG, "registerListener ${target.label}(type=${target.type}) = $ok")
            if (ok) registered += sensor
        }

        onDispose {
            registered.forEach { runCatching { sensorManager.unregisterListener(listener, it) } }
            Log.i(TAG, "onDispose 注销 ${registered.size} 个监听")
        }
    }

    LIVE_TARGETS.forEach { target ->
        KeyValue(
            target.label,
            readings[target.type]?.let { "$it  (${counts[target.type]} 次)" } ?: "等待回调…"
        )
    }
}

@Composable
private fun KeyboardProbe() {
    var text by remember { mutableStateOf("") }
    var focused by remember { mutableStateOf(false) }
    var previousLength by remember { mutableStateOf(0) }
    val history = remember { mutableStateListOf<String>() }
    val focusRequester = remember { FocusRequester() }

    Caption("点输入框 → 系统键盘以整屏覆盖层弹出；回来看看文本是否落到 state")

    BasicTextField(
        value = text,
        onValueChange = { updated ->
            val delta = updated.length - previousLength
            previousLength = updated.length
            text = updated
            val record = "Δ$delta  len=${updated.length}  「$updated」"
            history.add(0, record)
            while (history.size > 5) {
                history.removeAt(history.size - 1)
            }
            Log.i(TAG, "onValueChange $record")
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(Color(0xFF1C1C1C))
            .padding(horizontal = 6.dp, vertical = 5.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                focused = it.isFocused
                Log.i(TAG, "输入框 focus=${it.isFocused}")
            },
        textStyle = TextStyle(color = Color.White, fontSize = 12.sp, lineHeight = 14.sp),
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                Text("点这里输入…", color = Color(0xFF808080), fontSize = 11.sp)
            }
            innerTextField()
        }
    )

    Spacer(Modifier.height(4.dp))
    Button(
        onClick = {
            Log.i(TAG, "手动请求焦点以弹出键盘")
            runCatching { focusRequester.requestFocus() }
        }
    ) {
        Text("弹键盘", fontSize = 11.sp)
    }

    Spacer(Modifier.height(4.dp))
    KeyValue("focus", focused.toString())
    KeyValue("回显", if (text.isEmpty()) "(空)" else "「$text」")
    Text("变更轨迹（最新在上）：", color = Color(0xFFB0B0B0), fontSize = 9.sp)
    if (history.isEmpty()) {
        Text("· 暂无", color = Color(0xFF707070), fontSize = 9.sp)
    } else {
        history.forEach { record ->
            Text("· $record", color = Color(0xFFB0B0B0), fontSize = 9.sp, lineHeight = 12.sp)
        }
    }
}

/**
 * 关键一环：getDefaultSensor 返回非 null 只说明「看得到」，
 * 真正的门槛是 registerListener 能不能成功、以及有没有回调。
 * 逐个注册 → 观察 1.5 秒 → 注销，结果全部打到 logcat。
 */
@Composable
private fun VendorRegisterTest(sensorManager: SensorManager) {
    val results = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(Unit) {
        VENDOR_TARGETS.forEach { target ->
            val sensor = runCatching { sensorManager.getDefaultSensor(target.type) }.getOrNull()
            if (sensor == null) {
                results[target.label] = "无此传感器"
                Log.i(TAG, "VENDOR ${target.label}: getDefaultSensor -> null")
                return@forEach
            }

            var callbacks = 0
            var lastValue = ""
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    callbacks++
                    lastValue = "size=${event.values.size} " +
                        event.values.take(4).joinToString(" ") { "%.2f".format(it) }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            val registered = runCatching {
                sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }.getOrElse { error ->
                Log.w(TAG, "VENDOR ${target.label}: registerListener 抛异常 $error")
                false
            }

            delay(1500)
            runCatching { sensorManager.unregisterListener(listener, sensor) }

            results[target.label] = when {
                !registered -> "✗ 注册被拒"
                callbacks > 0 -> "✓ 成功 · $callbacks 次回调 · $lastValue"
                else -> "△ 注册成功但 1.5s 内无回调"
            }
            Log.i(TAG, "VENDOR ${target.label}: registered=$registered callbacks=$callbacks last=$lastValue")
        }
        Log.i(TAG, "VENDOR 六步测试全部完成")
    }

    VENDOR_TARGETS.forEach { target ->
        val mark = results[target.label] ?: "测试中…"
        val color = when {
            mark.startsWith("✓") -> Color(0xFF6EE7A8)
            mark.startsWith("✗") -> Color(0xFFE07A7A)
            mark.startsWith("△") -> Color(0xFFD9B44A)
            else -> Color(0xFF909090)
        }
        Text(
            text = "${target.label}  $mark",
            color = color,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Title(text: String) {
    Text(text = text, color = Color.White, fontSize = 15.sp, lineHeight = 18.sp)
}

@Composable
private fun SectionHeading(text: String) {
    Text(text = text, color = Color(0xFFFFD166), fontSize = 11.sp, lineHeight = 14.sp)
}

@Composable
private fun Caption(text: String) {
    Text(text = text, color = Color(0xFF909090), fontSize = 9.sp, lineHeight = 12.sp)
}

@Composable
private fun KeyValue(key: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$key: ", color = Color(0xFF909090), fontSize = 9.sp, lineHeight = 12.sp)
        Text(text = value, color = Color.White, fontSize = 9.sp, lineHeight = 12.sp)
    }
}
