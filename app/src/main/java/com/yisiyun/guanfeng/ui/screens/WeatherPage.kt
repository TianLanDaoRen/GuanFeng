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
import androidx.compose.foundation.layout.size
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
    // 双层引擎：正式窗口（3 小时）成熟前，先用速评窗口（5 分钟）顶上。
    // 等约 54 分钟才有任何结论是矫枉过正——速评可能误判，但误判的代价远小于干等。
    val formal = state.trend
    val fast = state.trendFast
    val formalReady = formal?.let { WeatherRule.assess(it) }?.likelihood
        ?.let { it != RainLikelihood.UNKNOWN } == true
    val trend = if (formalReady) formal else (fast ?: formal)
    val isFast = !formalReady && fast != null
    val assessment = trend?.let { WeatherRule.assess(it) }
    // 「有结论」= 置信度通过了闸门、真的给出了风雨倾向。
    // 不能只看 grade：置信度不足时 grade 仍是「平稳」，会出现
    // 环心写「未知」、下面写「平稳」、底部又写「再等等」的三处自相矛盾。
    val hasConclusion = assessment != null &&
        assessment.likelihood != RainLikelihood.UNKNOWN

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
            hasTrend = hasConclusion,
            // 必须给正方形：之前写成「高 104dp + 宽 0.78 屏宽」直接把圆环压成了椭圆。
            // 122dp 正方形在本页纵向余量内。
            modifier = Modifier.size(122.dp),
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 只在真有结论时才给行动建议（带伞 / 备伞 / 无变化）。
            // 没有结论时这里原本显示「再等等」，而环形中心的「观察中 · 攒样本中」
            // 已经把同一件事说了三遍——主人指出这行没用，确然。
            if (hasConclusion) {
                Text(
                    text = assessment?.advice ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(2.dp))
            }
            // 只放短依据：主屏 189dp 宽，长句会折行并把布局顶乱（完整依据在记录页）
            Text(
                text = assessment?.shortReason ?: "样本不足",
                color = Color(0xFF8A8A8A),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (isFast) {
                    "速评 · 5 分钟窗口 · 数据越久越准"
                } else {
                    "ΔP(3h) 外推 %+.1f hPa".format(trend?.deltaHpaPer3h ?: 0f)
                },
                color = Color(0xFF6E6E6E),
                fontSize = 8.sp,
            )
        }
    }
}
