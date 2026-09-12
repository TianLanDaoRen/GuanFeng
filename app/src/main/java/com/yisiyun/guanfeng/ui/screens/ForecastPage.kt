package com.yisiyun.guanfeng.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.log.QweatherLogger
import com.yisiyun.guanfeng.ui.components.PageHeader
import com.yisiyun.guanfeng.ui.components.WeatherIcon

/**
 * 负一屏 · 天气预报。
 *
 * ## 为什么放在"负一屏"而不是加在末尾
 *
 * 主人定的：**默认仍然是观风页**（那是这个应用的主角，戴着表一抬手要看到的是气压），
 * 预报是"往左多看一眼"的东西，所以放在主屏**之前**。
 *
 * ## 它只读已落盘的数据，不自己发请求
 *
 * 页面上的每一行都是**某次采集当时拿回来的**，所以断网也能看——这与整个应用
 * "核心不依赖网络"的性格一致。要刷新就等下一轮采集，或去设置流程里重新定位。
 * 代价是它可能落后最多一个采集周期（30 分钟），所以**必须把取数时刻写在脸上**，
 * 不能让人以为是此刻的实时天气。
 *
 * ## 没同意也要能用
 *
 * 主人要求"如果用户没同意，依旧给他留个按钮"——所以这里不拦人：
 * 没数据就说明为什么、给一个按钮；有旧数据就先显示旧数据。
 */
@Composable
fun ForecastPage(
    snapshot: QweatherLogger.Snapshot?,
    consentGranted: Boolean,
    onRequestConsent: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        PageHeader(
            title = "天气预报",
            titleColor = Color(0xFF7FD1E8),
            subtitle = snapshot?.let { "取自 ${it.fetchedClock}" } ?: "尚未采集",
        )

        when {
            !consentGranted -> EmptyState(
                text = "还没开启天气采集。开启后每 30 分钟取一次和风天气，" +
                    "存进本机 CSV，供日后校准判据。",
                buttonLabel = "开启天气采集",
                onButton = onRequestConsent,
            )

            snapshot == null -> EmptyState(
                text = "还没有采到数据。手表需要定位权限与网络；第一次采集通常在开启后几分钟内完成。",
                buttonLabel = "去设置",
                onButton = onRequestConsent,
            )

            else -> SnapshotBody(snapshot)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.SnapshotBody(
    snapshot: QweatherLogger.Snapshot,
) {
    // 当前状况：一行大字，抬手就能看清
    Row(verticalAlignment = Alignment.CenterVertically) {
        WeatherIcon(code = snapshot.conditionCode, tint = Color(0xFF9FD8EE), fontSize = 26.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            snapshot.conditionText.ifBlank { "—" },
            color = Color(0xFFE8E8E8),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.weight(1f))
        Text(
            snapshot.tempC.toDoubleOrNull()
                ?.let { String.format(java.util.Locale.US, "%.1f℃", it) }
                ?: "—",
            color = Color(0xFFF2C14E),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
    Spacer(Modifier.height(3.dp))
    Text(
        buildString {
            append("体感 ").append(
                snapshot.feelsLikeC.toDoubleOrNull()
                    ?.let { String.format(java.util.Locale.US, "%.1f", it) }
                    ?: "—",
            ).append("℃")
            append("　湿度 ").append(
                // 上游给的是 0–1 的小数，界面上换算成百分比更直观
                snapshot.humidity.toDoubleOrNull()?.let { "${(it * 100).toInt()}%" } ?: "—",
            )
            append("　气压 ").append(snapshot.pressureHpa.ifBlank { "—" }).append(" hPa")
        },
        color = Color(0xFF8A8A8A),
        fontSize = 8.sp,
        lineHeight = 11.sp,
    )

    Spacer(Modifier.height(8.dp))
    Text("未来几小时", color = Color(0xFF6EE7A8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))

    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        snapshot.hours.forEach { hour -> HourRow(hour) }
    }

    Spacer(Modifier.height(6.dp))
    Text(
        // 定位来源要写出来：它决定这份预报的可信范围（Wi-Fi 是 30 米级、IP 是城市级）
        "天气数据由和风天气提供 · 位置 ${snapshot.locationSource.ifBlank { "未知" }}",
        color = Color(0xFF5A5A5A),
        fontSize = 7.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun HourRow(hour: QweatherLogger.HourLine) {
    // forecastTime 是 **ISO UTC**（形如 2026-09-12T08:00Z）——**必须换算成本地时间**再显示。
    // 第一版我直接截了字符串，于是 16:30 取的快照里未来第一格写着"09:00"，
    // 一眼就能看出不对（手表上没有任何地方显示 UTC）。
    val label = runCatching {
        java.time.OffsetDateTime.parse(hour.clock)
            .atZoneSameInstant(java.time.ZoneId.systemDefault())
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrNull() ?: hour.clock.substringAfter('T').take(5).ifBlank { "—" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = Color(0xFF9A9A9A), fontSize = 9.sp, modifier = Modifier.weight(0.9f))
        WeatherIcon(code = hour.conditionCode, tint = Color(0xFFB8D8E8), fontSize = 12.sp)
        Spacer(Modifier.width(4.dp))
        Text(
            hour.conditionText.ifBlank { "—" },
            color = Color(0xFFD0D0D0),
            fontSize = 9.sp,
            modifier = Modifier.weight(1.4f),
        )
        Text(
            hour.precipProbability.toDoubleOrNull()
                ?.takeIf { it > 0.0 }
                ?.let { "💧${(it * 100).toInt()}%" }
                ?: "",
            color = Color(0xFF7FD1E8),
            fontSize = 8.sp,
            modifier = Modifier.weight(0.9f),
        )
        Text(
            // 上游给两位小数（28.14），界面上一位就够——手表屏幕小，多一位只是噪声
            // 单位要写出来：只有数字读者得靠上下文猜是温度还是降水概率，
            // 而这一行里两样都有
            hour.tempC.toDoubleOrNull()
                ?.let { String.format(java.util.Locale.US, "%.1f℃", it) }
                ?: "—",
            color = Color(0xFFE8E8E8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.EmptyState(
    text: String,
    buttonLabel: String,
    onButton: () -> Unit,
) {
    Spacer(Modifier.height(10.dp))
    Text(text, color = Color(0xFFB8B8B8), fontSize = 9.sp, lineHeight = 12.sp)
    Spacer(Modifier.weight(1f))
    Button(
        onClick = onButton,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2A6C86),
            contentColor = Color(0xFFD8F2FA),
        ),
    ) { Text(buttonLabel, fontSize = 11.sp) }
    Spacer(Modifier.height(6.dp))
    Text(
        "天气数据由和风天气提供",
        color = Color(0xFF5A5A5A),
        fontSize = 7.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}
