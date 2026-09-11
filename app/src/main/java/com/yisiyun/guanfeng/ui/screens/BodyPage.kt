package com.yisiyun.guanfeng.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.data.RecorderState

/**
 * 第二屏 · 体感：环境侧与身体侧并置。
 *
 * 关于心率的命名必须严格：
 *   - **实时心率**是传感器此刻的读数（`TYPE_HEART_RATE` 直接给的值）；
 *   - **静息基线**才是派生指标——只采「无垂直运动、无步数」的静止样本，取其低分位数。
 * 早先把瞬时值标成「静息心率」是错的：爬楼时读数 120，界面照样写「静息心率」。
 */
@Composable
fun BodyPage(state: RecorderState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        // 用固定间距而不是 SpaceEvenly：后者会把每行的注释顶出可視区（本屏只有 189×248dp）
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        BigReading(
            label = "实时心率",
            value = state.heartRateBpm?.let { "%.0f".format(it) } ?: "—",
            unit = "bpm",
            color = Color(0xFFFF8A8A),
            note = when {
                state.heartRateBpm == null -> "未取到读数"
                state.heartRateBpm == 0f -> "未佩戴时读数为 0"
                state.restingHeartRateBpm != null ->
                    "静息基线 %.0f bpm（静止样本低分位）".format(state.restingHeartRateBpm)
                else -> "静息基线攒样本中"
            },
        )
        BigReading(
            label = "腕温",
            value = state.wristTemperatureC?.let { "%.1f".format(it) } ?: "—",
            unit = "℃",
            color = Color(0xFFF2C14E),
            note = "厂商传感器首通道，未标定",
        )
        BigReading(
            label = "环境光",
            value = state.lightLux?.let { "%.0f".format(it) } ?: "—",
            unit = "lux",
            color = Color(0xFF9BB0FF),
            note = state.lightDelta10Min?.let {
                "10 分钟变化 %+.0f lux（仅记录，未参与判定）".format(it)
            } ?: "光照慢趋势攒样本中",
        )
    }
}

@Composable
private fun BigReading(
    label: String,
    value: String,
    unit: String,
    color: Color,
    note: String?,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Color(0xFF8A8A8A), fontSize = 9.sp)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, color = color, fontSize = 28.sp)
            Spacer(Modifier.fillMaxWidth(0.03f))
            Text(text = unit, color = Color(0xFF8A8A8A), fontSize = 10.sp)
        }
        if (note != null) {
            Text(text = note, color = Color(0xFF5E5E5E), fontSize = 7.sp)
        }
        Spacer(Modifier.height(0.dp))
    }
}
