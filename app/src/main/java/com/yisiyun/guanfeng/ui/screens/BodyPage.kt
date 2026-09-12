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
import com.yisiyun.guanfeng.ui.components.PageHeader

/**
 * 第二屏 · 体感：环境侧与身体侧并置。
 *
 * 命名必须严格：
 *   - **实时心率**是传感器此刻的读数（`TYPE_HEART_RATE` 直接给的值）；
 *   - **静息基线**才是派生指标——只采「无垂直运动、无步数」的静止样本，取其低分位数。
 * 早先把瞬时值标成「静息心率」是错的：爬楼时读数 120，界面照样写「静息心率」。
 *
 * 布局用紧凑的两列仪表式，而不是三块大数字堆叠：本屏只有 189×248 dp，
 * 堆叠式会把最后一行的注释挤出屏幕（我连续改错两版才认账）。
 * 再加一层滚动兜底，任何情况下都不会有内容被裁掉。
 */
@Composable
fun BodyPage(state: RecorderState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // 版式统一走 PageHeader；各页主标题保留自己的颜色（体感用蓝）
        PageHeader(
            title = "体感",
            titleColor = Color(0xFF6FB4E8),
            subtitle = "环境 × 身体",
        )

        ReadingRow(
            // 【文案】原为「实时心率」。心率改成脉冲采样后（每 10 分钟开 20 秒），
            // 这个数最坏会陈旧十分钟——继续叫「实时」就是名不副实。
            // 一个仪表说"实时"却给十分钟前的读数，比没有读数更坏。
            label = "最近心率",
            value = state.heartRateBpm?.let { "%.0f".format(it) } ?: "—",
            unit = "bpm",
            color = Color(0xFFFF8A8A),
        )
        ReadingRow(
            label = "静息基线",
            value = state.restingHeartRateBpm?.let { "%.0f".format(it) } ?: "—",
            unit = "bpm",
            color = Color(0xFFE0A0A0),
            small = true,
            hint = if (state.restingHeartRateBpm == null) "静止样本攒集中" else null,
        )

        ReadingRow(
            label = "腕温",
            value = state.wristTemperatureC?.let { "%.1f".format(it) } ?: "—",
            unit = "℃",
            color = Color(0xFFF2C14E),
            hint = "首通道，未标定",
        )

        ReadingRow(
            label = "环境光",
            value = state.lightLux?.let { "%.0f".format(it) } ?: "—",
            unit = "lux",
            color = Color(0xFF9BB0FF),
        )
        ReadingRow(
            label = "光照趋势",
            value = state.lightDelta10Min?.let { "%+.0f".format(it) } ?: "—",
            unit = "lux/10min",
            color = Color(0xFF8FA6D8),
            small = true,
        )
    }
}

@Composable
private fun ReadingRow(
    label: String,
    value: String,
    unit: String,
    color: Color,
    small: Boolean = false,
    hint: String? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, color = Color(0xFF8A8A8A), fontSize = 9.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, color = color, fontSize = if (small) 15.sp else 20.sp)
                Spacer(Modifier.fillMaxWidth(0.04f))
                Text(text = unit, color = Color(0xFF7A7A7A), fontSize = 8.sp)
            }
        }
        if (hint != null) {
            Text(text = hint, color = Color(0xFF5E5E5E), fontSize = 7.sp)
        }
    }
}
