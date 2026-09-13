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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.core.CorroborationEngine
import com.yisiyun.guanfeng.core.RainLikelihood
import com.yisiyun.guanfeng.core.TrendGrade
import com.yisiyun.guanfeng.core.WeatherRule
import com.yisiyun.guanfeng.core.TrendLabel
import com.yisiyun.guanfeng.data.RecorderState
import com.yisiyun.guanfeng.ui.components.TrendGauge
import com.yisiyun.guanfeng.ui.theme.INK_MID
import com.yisiyun.guanfeng.ui.theme.INK_LOW
import com.yisiyun.guanfeng.ui.theme.INK_HIGH

/**
 * 第一屏 · 观风。
 *
 * 一眼看两件事：**环**上是气压在怎么变（左急降、右急升），
 * **环心**的大字是风雨倾向（低/中/高…）。
 * 这两者不是一回事——环说的是趋势，环心说的是结论。
 */
@Composable
fun WeatherPage(state: RecorderState) {
    // 双层引擎：正式窗口（3 小时）成熟前，先用速评窗口（5 分钟）顶上。
    // 等约 54 分钟才有任何结论是矫枉过正——速评可能误判，但误判的代价远小于干等。
    val formal = state.trend
    val fast = state.trendFast
    val formalReady = formal?.let { WeatherRule.assess(it, state.recentFallHpa, state.episode) }?.likelihood
        ?.let { it != RainLikelihood.UNKNOWN } == true
    val trend = if (formalReady) formal else (fast ?: formal)
    val isFast = !formalReady && fast != null
    val assessment = trend?.let { WeatherRule.assess(it, state.recentFallHpa, state.episode) }
    // 「有结论」= 置信度通过了闸门、真的给出了风雨倾向。
    // 不能只看 grade：置信度不足时 grade 仍是「平稳」，会出现
    // 环心写「未知」、下面写「平稳」、底部又写「再等等」的三处自相矛盾。
    val rawHasConclusion = assessment != null &&
        assessment.likelihood != RainLikelihood.UNKNOWN

    // 体感与环境佐证：只有光照骤降允许升档（物理因果），
    // 心率与腕温只上屏提示——理由写在 CorroborationEngine 的注释里。
    val corroborations = CorroborationEngine.evaluate(
        lightLux = state.lightLux,
        lightDelta10Min = state.lightDelta10Min,
        heartRateBpm = state.heartRateBpm,
        restingHeartRateBpm = state.restingHeartRateBpm,
        wristTemperatureC = state.wristTemperatureC,
        wristTempBaselineC = state.wristTemperatureBaselineC,
        isResting = state.isResting,
    )
    val likelihood = if (rawHasConclusion) {
        CorroborationEngine.apply(
            likelihood = assessment!!.likelihood,
            items = corroborations,
            // 只在气压本身正在下降时才允许佐证升档：
            // 否则"气压平稳 + 走进楼道"就能报出一次无中生有的降雨
            pressureFalling = trend?.grade == TrendGrade.FALLING ||
                trend?.grade == TrendGrade.FALLING_FAST,
        )
    } else {
        RainLikelihood.UNKNOWN
    }
    val hasConclusion = likelihood != RainLikelihood.UNKNOWN

    // 纵向预算（本页可用高度 222dp = 248 − 14 翻页符 − 12 页边距）：
    //   顶行 37 + 圆环 122 + 底部信息最坏 47 = 206 ≤ 222 ✓
    // 底部「最坏 47」= 建议 20 + 间隔 2 + 体感一行 12 + 间隔 2 + 速评 11。
    // 体感那行必须限死一行，否则三条佐证同时出现时会折成三行，把圆环挤出去。
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                    text = // 这一格说明的是"此刻的气压变化是不是高度引起的"，不是"人在不在动"。
                    // 写"静止/运动中"会被读成身体状态（主人指出过），改成高度域的说法。
                    if (trend?.isInVerticalTransit == true) "高度变化中" else "高度稳定",
                    color = if (trend?.isInVerticalTransit == true) Color(0xFFF2C14E) else INK_MID,
                    fontSize = 9.sp,
                )
            }
        }

        // 圆环在「顶行与底部信息之间」居中对齐，而不是用 SpaceBetween 把空白平均分到两处——
        // 后者会让圆环整体偏上。主人指出「重心有些偏上」，确然。
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            TrendGauge(
                rateHpaPerHour = trend?.rateHpaPerHour ?: 0f,
                grade = trend?.grade ?: TrendGrade.INSUFFICIENT,
                likelihood = likelihood,
                hasTrend = hasConclusion,
                // 必须给正方形：之前写成「高 104dp + 宽 0.78 屏宽」直接把圆环压成了椭圆。
                modifier = Modifier.size(122.dp),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 只在真有结论时才给行动建议（带伞 / 备伞 / 无变化）。
            // 没有结论时这里原本显示「再等等」，而环形中心的「观察中 · 攒样本中」
            // 已经把同一件事说了三遍——主人指出这行没用，确然。
            if (hasConclusion) {
                Text(
                    text = assessment?.advice ?: "",
                    color = INK_HIGH,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(2.dp))
            }
            // 短依据只在「为什么还判断不了」时才显示。
            // 判断得出来的时候它只是在复述趋势（趋势说「平稳」、原因也写「气压平稳」），
            // 主人一眼看出这是重复——确然，而且它还占掉了本来能给体感佐证的那一行。
            if (!hasConclusion) {
                Text(
                    text = assessment?.shortReason ?: "样本不足",
                    color = INK_HIGH,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            } else if (corroborations.isNotEmpty()) {
                // 只上屏最高优先级的一条，其余折叠成 +N：
                // 三条全铺开会在 165dp 宽里折成三行，把圆环挤出页面。
                val first = corroborations.first()
                val suffix = if (corroborations.size > 1) " +${corroborations.size - 1}" else ""
                Text(
                    text = "体感：${first.label}$suffix",
                    color = Color(0xFFE8C36A),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
            // 有结论、又没有佐证时，这里什么都不放：
            // 短原因的作用只是解释「为什么还判断不了」，判断得出来时它纯属复述评级。
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (isFast) {
                    // 不要写"5 分钟窗口"：那是内部概念，和主页面说的 3 小时窗口并列时会让人以为
                    // 预测只有 5 分钟有效（主人明确指出过）。这里只讲"这是临时结论、会越来越准"。
                    "速评 · 数据越久越准"
                } else {
                    // 被门限压平时不许写 +0.0（会被读成"气压不变"）——见 TrendLabel.pageDeltaLine
                    TrendLabel.pageDeltaLine(
                        deltaHpaPer3h = trend?.deltaHpaPer3h ?: 0f,
                        belowThreshold = trend?.belowThreshold == true,
                    )
                },
                color = INK_LOW,
                fontSize = 8.sp,
            )
        }
    }
}
