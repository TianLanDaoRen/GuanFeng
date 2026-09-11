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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.data.RecorderState

/** 第二屏 · 体感：环境侧与身体侧并置，这是只有贴着皮肤的设备才拿得到的数据。 */
@Composable
fun BodyPage(state: RecorderState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        BigReading(
            label = "静息心率",
            value = state.heartRateBpm?.let { "%.0f".format(it) } ?: "—",
            unit = "bpm",
            color = Color(0xFFFF8A8A),
            note = if (state.heartRateBpm == null || state.heartRateBpm == 0f) "未佩戴时读数为 0" else null,
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
            note = null,
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
            Spacer(Modifier.height(0.dp))
        }
        if (note != null) {
            Text(
                text = note,
                color = Color(0xFF5E5E5E),
                fontSize = 7.sp,
                textAlign = TextAlign.Start,
            )
        }
    }
}
