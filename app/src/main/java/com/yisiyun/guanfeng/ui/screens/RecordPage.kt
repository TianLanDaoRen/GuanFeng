package com.yisiyun.guanfeng.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.core.TrendConfidence
import com.yisiyun.guanfeng.core.WeatherRule
import com.yisiyun.guanfeng.data.RecorderState
import com.yisiyun.guanfeng.log.WeatherObservationLogger
import com.yisiyun.guanfeng.log.WeatherObservation
import com.yisiyun.guanfeng.core.RainLikelihood
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background

/**
 * 第四屏 · 记录：这一屏存在的意义是让「它到底有没有在记」一眼可见、可以自己验证。
 *
 * 它同时承担「诊断页」的职责——主屏放不下的完整依据（例如为什么判为趋势不可信）
 * 都挪到了这里。内容比一屏高，所以必须可滚动，否则下面的高度解耦信息会被裁掉。
 */
@Composable
fun RecordPage(state: RecorderState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val trend = state.trend
    val rationale = trend?.let { WeatherRule.assess(it).rationale }
    var observedToday by remember { mutableStateOf(WeatherObservationLogger.countToday(context)) }
    var observeFeedback by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (state.recording) "记录中" else "已停止",
                color = if (state.recording) Color(0xFF6EE7A8) else Color(0xFFFF7A6B),
                fontSize = 13.sp,
            )
            Spacer(Modifier.fillMaxWidth(0.06f))
            Text(
                text = if (state.logHealthy) "落盘正常" else "写入异常",
                color = if (state.logHealthy) Color(0xFF7A7A7A) else Color(0xFFFF7A6B),
                fontSize = 8.sp,
            )
        }
        Text("前台服务 + indicator 保活中", color = Color(0xFF5E5E5E), fontSize = 7.sp)

        Spacer(Modifier.height(4.dp))
        Kv("会话时长", formatDuration(state.elapsedSeconds))
        Kv("已落盘", "${state.loggedRows} 行")
        // 只显示时间戳部分：完整文件名在 189dp 宽上会折成三行，把下面的内容全挤走
        Kv("会话文件", state.logFileName.ifEmpty { "—" }
            .removePrefix("guanfeng_")
            .removeSuffix(".csv"))
        if (state.restoredSamples > 0) {
            Kv("续接历史", "${state.restoredSamples} 个样本")
        }

        Spacer(Modifier.height(4.dp))
        Text("高度解耦", color = Color(0xFFF2C14E), fontSize = 9.sp)
        Kv("累计垂直位移", "%+.1f 米".format(trend?.elevationMeters ?: 0f))
        Kv("解耦样本", "${trend?.elevationEvents ?: 0} 个")
        Kv("当前状态", if (trend?.isInVerticalTransit == true) "垂直运动中" else "静止")
        Kv("置信度", (trend?.confidence ?: TrendConfidence.INSUFFICIENT).label)
        Kv("拟合优度", "%.3f".format(trend?.fitRSquared ?: 0f))
        Kv("窗口覆盖", "%.0f%%".format((trend?.coverageFraction ?: 0f) * 100f))

        if (rationale != null) {
            Spacer(Modifier.height(4.dp))
            Text("判定依据", color = Color(0xFFF2C14E), fontSize = 9.sp)
            Text(
                text = rationale,
                color = Color(0xFFB4B4B4),
                fontSize = 8.sp,
                lineHeight = 11.sp,
            )
        }

        Spacer(Modifier.height(6.dp))
        // 天气实况：校准唯一的数据来源。
        // 记录实况的**同时**把当时的判断一并落盘，之后才能直接算命中率与漏报率。
        Text("天气实况（供校准）", color = Color(0xFF6EE7A8), fontSize = 9.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            WeatherObservation.entries.forEach { observation ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .background(Color(0xFF1E2A22), RoundedCornerShape(15.dp))
                        .clickable {
                            val assessment = trend?.let { WeatherRule.assess(it) }
                            val written = WeatherObservationLogger.append(
                                context = context,
                                observation = observation,
                                weatherPressureHpa = state.weatherPressureHpa,
                                trend = trend,
                                likelihood = assessment?.likelihood ?: RainLikelihood.UNKNOWN,
                            )
                            if (written) {
                                observedToday = WeatherObservationLogger.countToday(context)
                                observeFeedback = "已记 ${observation.label}"
                            } else {
                                observeFeedback = "写入失败"
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(observation.label, color = Color(0xFFCFE8D8), fontSize = 10.sp)
                }
            }
        }
        Text(
            text = if (observeFeedback.isNotEmpty()) {
                "$observeFeedback · 今日 $observedToday 次"
            } else {
                "今日 $observedToday 次 · 下雨/起风时随手点一下"
            },
            color = Color(0xFF6E6E6E),
            fontSize = 7.sp,
        )

        Spacer(Modifier.height(6.dp))
        Text(
            text = "每 5 秒一个样本 · 正式窗口 3 小时 · 风雨倾向为启发式规则、未用真实降水校准",
            color = Color(0xFF4E4E4E),
            fontSize = 7.sp,
            lineHeight = 10.sp,
        )
    }
}

@Composable
private fun Kv(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        // 标签占剩余空间、值取自身宽度并右对齐：固定比例的 Spacer 会把长值挤没
        Text(
            text = label,
            color = Color(0xFF8A8A8A),
            fontSize = 9.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = Color(0xFFE8E8E8),
            fontSize = 9.sp,
            maxLines = 1,
            textAlign = TextAlign.End,
        )
    }
}

private fun formatDuration(seconds: Long): String =
    "%d:%02d".format(seconds / 60, seconds % 60)
