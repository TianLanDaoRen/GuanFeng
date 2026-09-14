package com.yisiyun.guanfeng.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import com.yisiyun.guanfeng.ui.theme.INK_LOW
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
    TrendZone(3.0f, 6.0f, Color(0xFF7FD1E8)),   // 缓升
    TrendZone(6.0f, 9.0f, Color(0xFF9BB0FF)),   // 急升
)

/**
 * 量程上限，单位 **hPa/3小时**（与区段表、与页面那行 ΔP 同一单位）。
 *
 * 2026-09-14 踩过的坑：区段表改到 ±9 了，这里还留着 ±3 ——
 * 于是 ±3 以外的色块被 `coerceIn` 挤到弧外，**黄段整段消失**、只剩一截红，
 * 指针位置也随之离谱。**改单位时必须同时改量程**。
 */
/** 区段名：**由区间反推**，不另存一份字符串，免得名字与区间各说各话。 */
private fun zoneLabel(z: TrendZone): String = when {
    z.to <= -6f -> "急降"
    z.to <= -3f -> "缓降"
    z.from < 3f -> "平稳"
    z.from < 6f -> "缓升"
    else -> "急升"
}

/** 由落点值反查所处区段（与色块同源：标注与针永远不可能说不一致的话）。 */
private fun zoneOf(value: Float): TrendZone =
    ZONES.firstOrNull { value >= it.from && value < it.to } ?: ZONES.last()

private const val MAX_RATE = 9.0f
private const val HALF_SWEEP = 120f
private const val TOP_ANGLE = -90f

private fun angleFor(rate: Float): Float =
    TOP_ANGLE + (rate.coerceIn(-MAX_RATE, MAX_RATE) / MAX_RATE) * HALF_SWEEP

@Composable
fun TrendGauge(
    deltaHpaPer3h: Float,
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
            val angle = angleFor(deltaHpaPer3h)
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

        }

        // 两端标注：色区本身不自解释——主人第一次就问了"这五个颜色分别代表什么"。
        // 只标两个极端，中间三档由游标位置与下方的趋势评级说明，避免堆字。
        // 「风雨倾向」放在**外层 Box** 里、不参与环心 Column 的居中 ——
        // 否则它是 Column 的第 4 个孩子，整块会重新居中，大字被压到弧心以下
        // （主人 2026-09-14 截图："间隔有些大了，导致中太靠下"）。
        Text(
            text = "风雨倾向",
            color = INK_MID,
            fontSize = 9.sp,
// 位置是按几何定死的，不是试出来的：
            //   大字 38sp，顶边约在中心上方 23dp；环内半径约 52dp。
            //   所以标签放 −30dp：离大字 7dp（紧贴上沿）、离弧线 17dp（不遮挡）。
            //   上一版放 −44dp，左上角到 −49dp，正好顶到弧线（主人截图"有些遮挡"）。
            modifier = Modifier.align(Alignment.Center).offset(y = (-30).dp),
        )

        Text(
            text = "急降",
            color = Color(0xFFFF6B5B),
            fontSize = 8.sp,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 4.dp, bottom = 6.dp),
        )
        // 中间那格：**与两端同字号、同贴底基线**，只有颜色不同（取当前区段色）。
        // 值也从同一个落点推出来 —— 主人 2026-09-14 指出过"针在绿段而中间写缓降"。
        Text(
            text = if (hasTrend) zoneLabel(zoneOf(deltaHpaPer3h)) else "攒样本中",
            color = if (hasTrend) zoneOf(deltaHpaPer3h).color else INK_LOW,
            fontSize = 8.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp),
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
