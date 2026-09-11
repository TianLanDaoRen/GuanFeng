package com.yisiyun.guanfeng.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.yisiyun.guanfeng.core.RainLikelihood
import com.yisiyun.guanfeng.core.TrendGrade
import com.yisiyun.guanfeng.core.WeatherRule
import com.yisiyun.guanfeng.data.RecorderState
import com.yisiyun.guanfeng.ui.components.TrendGauge

/** 第一屏 · 观风：主读数就是那个环形仪表，一眼看风雨倾向与趋势位置。 */
@Composable
fun WeatherPage(state: RecorderState) {
    val trend = state.trend
    val assessment = trend?.let { WeatherRule.assess(it) }
    val hasTrend = trend != null && trend.grade != TrendGrade.INSUFFICIENT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.pressureHpa?.let { "%.1f hPa".format(it) } ?: "— hPa",
                color = Color(0xFFE8E8E8),
                fontSize = 13.sp,
            )
            Spacer(Modifier.fillMaxWidth(0.06f))
            Box(
                modifier = Modifier
                    .background(
                        color = if (trend?.isInVerticalTransit == true) Color(0xFF3A2E12) else Color(0xFF1C1C1C),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = if (trend?.isInVerticalTransit == true) "运动中" else "静止",
                    color = if (trend?.isInVerticalTransit == true) Color(0xFFF2C14E) else Color(0xFF9A9A9A),
                    fontSize = 9.sp,
                )
            }
        }

        TrendGauge(
            rateHpaPerHour = trend?.rateHpaPerHour ?: 0f,
            grade = trend?.grade ?: TrendGrade.INSUFFICIENT,
            likelihood = assessment?.likelihood ?: RainLikelihood.UNKNOWN,
            hasTrend = hasTrend,
            // 104dp：122dp 时会把底部「ΔP(3h)」挤出屏幕（实测）
            modifier = Modifier.height(104.dp).fillMaxWidth(0.78f),
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = assessment?.advice ?: "再等等",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(2.dp))
            // 只放短依据：主屏 189dp 宽，长句会折行并把布局顶乱（完整依据在记录页）
            Text(
                text = assessment?.shortReason ?: "样本不足",
                color = Color(0xFF8A8A8A),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "ΔP(3h) 外推 %+.1f hPa".format(trend?.deltaHpaPer3h ?: 0f),
                color = Color(0xFF6E6E6E),
                fontSize = 8.sp,
            )
        }
    }
}
