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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.core.TrendConfidence
import com.yisiyun.guanfeng.data.RecorderState

/** 第三屏 · 记录：这一屏存在的意义是让「它到底有没有在记」一眼可见、可以自己验证。 */
@Composable
fun RecordPage(state: RecorderState) {
    val trend = state.trend
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (state.recording) "记录中" else "已停止",
                color = if (state.recording) Color(0xFF6EE7A8) else Color(0xFFFF7A6B),
                fontSize = 14.sp,
            )
            Spacer(Modifier.fillMaxWidth(0.06f))
            Text(
                text = if (state.logHealthy) "落盘正常" else "写入异常",
                color = if (state.logHealthy) Color(0xFF7A7A7A) else Color(0xFFFF7A6B),
                fontSize = 9.sp,
            )
        }
        Text(
            text = "前台服务 + indicator 保活中",
            color = Color(0xFF5E5E5E),
            fontSize = 8.sp,
        )

        Spacer(Modifier.height(4.dp))
        Kv("会话时长", formatDuration(state.elapsedSeconds))
        Kv("已落盘", "${state.loggedRows} 行")
        Kv("日志文件", state.logFileName.ifEmpty { "—" })

        Spacer(Modifier.height(4.dp))
        Text("高度解耦", color = Color(0xFFF2C14E), fontSize = 9.sp)
        Kv("累计垂直位移", "%+.1f 米".format(trend?.elevationMeters ?: 0f))
        Kv("解耦样本", "${trend?.elevationEvents ?: 0} 个")
        Kv("置信度", (trend?.confidence ?: TrendConfidence.INSUFFICIENT).label)
        Kv("拟合优度", "%.3f".format(trend?.fitRSquared ?: 0f))

        Spacer(Modifier.height(4.dp))
        Text(
            text = "每 2 秒采样 · 演示窗口 6 分钟 · 风雨倾向为启发式规则、未用真实降水校准",
            color = Color(0xFF4E4E4E),
            fontSize = 7.sp,
            lineHeight = 10.sp,
        )
    }
}

@Composable
private fun Kv(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Color(0xFF8A8A8A), fontSize = 9.sp)
        Spacer(Modifier.fillMaxWidth(0.5f))
        Text(text = value, color = Color(0xFFE8E8E8), fontSize = 9.sp)
    }
}

private fun formatDuration(seconds: Long): String =
    "%d:%02d".format(seconds / 60, seconds % 60)
