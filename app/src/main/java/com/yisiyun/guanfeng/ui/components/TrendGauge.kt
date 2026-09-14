package com.yisiyun.guanfeng.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.core.RainLikelihood
import com.yisiyun.guanfeng.core.TrendGrade
import kotlin.math.cos
import kotlin.math.sin
import com.yisiyun.guanfeng.ui.theme.INK_MID
import com.yisiyun.guanfeng.ui.theme.INK_HIGH

/**
 * 趋势环形仪表。
 *
 * 布局思想：OWW221 是 378×496 的大圆角方屏，四角本来就被硬件遮罩切掉（对角线 radius×0.4
 * 内不可见），所以把主读数做成环形、把内容居中放射，比铺满矩形的列表更贴合这块屏幕——
 * 四角的「禁区」反而成了留给弧线的空间。
 *
 * 表盘含义：240° 弧对称分布在正上方两侧，左端 = 急降，正中 = 平稳，右端 = 急升，
 * 白色游标指向当前速率位置。
 */
private data class TrendZone(val from: Float, val to: Float, val color: Color)

private val ZONES = listOf(
    // 【单位统一为 hPa/3 小时，与文献同源】界取 ±3 与 ±6：
    //   3 = 气象学 Law of Storms 的雷暴起点；6 = 强风；10 = 大风。
    //   所以黄段（缓降）从"刚够上雷暴判据"开始，红段（急降）从强风级开始。
    //   旧版是 hPa/小时（±1.5/±4.5）—— 与页面上那行 hPa/3h 的 ΔP 同屏并列，
    //   单位不同导致"ΔP −2.1 才刚到黄区"的误读（主人 2026-09-14 指出）。
    TrendZone(-9.0f, -6.0f, Color(0xFFFF6B5B)), // 急降（≥强风级）
    TrendZone(-6.0f, -3.0f, Color(0xFFF2C14E)), // 缓降（够上雷暴判据）
    TrendZone(-3.0f, 3.0f, Color(0xFF6EE7A8)),  // 平稳（含自然背景 ±0.6）
    TrendZone(0.5f, 1.5f, Color(0xFF7FD1E8)),   // 缓升
    TrendZone(1.5f, 3.0f, Color(0xFF9BB0FF)),   // 急升
)

private const val MAX_RATE = 3.0f
private const val HALF_SWEEP = 120f
private const val TOP_ANGLE = -90f

private fun angleFor(rate: Float): Float =
    TOP_ANGLE + (rate.coerceIn(-MAX_RATE, MAX_RATE) / MAX_RATE) * HALF_SWEEP

@Composable
fun TrendGauge(
    rateHpaPerHour: Float,
    grade: TrendGrade,
    likelihood: RainLikelihood,
    hasTrend: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {            val stroke = size.minDimension * 0.075f
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)

            // 五段色区
            ZONES.forEach { zone ->
                val start = angleFor(zone.from)
                val sweep = angleFor(zone.to) - start
                drawArc(
                    color = zone.color.copy(alpha = 0.85f),
                    startAngle = start,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt),
                )
            }

            if (!hasTrend) return@Canvas

            // 游标：沿环半径方向的一小段白色刻线，外加一个圆点
            val angle = angleFor(rateHpaPerHour)
            val radians = Math.toRadians(angle.toDouble())
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val direction = Offset(cos(radians).toFloat(), sin(radians).toFloat())

            val outer = Offset(
                center.x + direction.x * (radius - stroke * 0.15f),
                center.y + direction.y * (radius - stroke * 0.15f),
            )
            val inner = Offset(
                center.x + direction.x * (radius - stroke * 1.9f),
                center.y + direction.y * (radius - stroke * 1.9f),
            )
            drawLine(
                color = INK_HIGH,
                start = inner,
                end = outer,
                strokeWidth = stroke * 0.42f,
                cap = StrokeCap.Round,
            )
            drawCircle(color = INK_HIGH, radius = stroke * 0.42f, center = outer)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 没有趋势时不要显示一个孤零零的短横（视觉上像坏了），
            // 而是给一个明确的状态词，字号也相应收小
            if (hasTrend) {
                Text(
                    text = likelihood.label,
                    color = likelihoodColor(likelihood),
                    fontSize = 38.sp,
                )
            } else {
                Text(
                    text = "观察中",
                    color = INK_HIGH,
                    fontSize = 20.sp,
                )
            }
            Text(
                text = "风雨倾向",
                color = INK_MID,
                fontSize = 9.sp,
            )
            Text(
                text = if (hasTrend) grade.label else "攒样本中",
                color = Color(0xFFD0D0D0),
                fontSize = 11.sp,
            )
        }

        // 两端标注：色区本身不自解释——主人第一次就问了"这五个颜色分别代表什么"。
        // 只标两个极端，中间三档由游标位置与下方的趋势评级说明，避免堆字。
        Text(
            text = "急降",
            color = Color(0xFFFF6B5B),
            fontSize = 8.sp,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 4.dp, bottom = 6.dp),
        )
        Text(
            text = "急升",
            color = Color(0xFF9BB0FF),
            fontSize = 8.sp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 4.dp, bottom = 6.dp),
        )
    }
}

fun likelihoodColor(likelihood: RainLikelihood): Color = when (likelihood) {
    RainLikelihood.HIGH -> Color(0xFFFF7A6B)
    RainLikelihood.MEDIUM -> Color(0xFFF2C14E)
    RainLikelihood.LOW -> Color(0xFF6EE7A8)
    RainLikelihood.UNKNOWN -> INK_HIGH
}
