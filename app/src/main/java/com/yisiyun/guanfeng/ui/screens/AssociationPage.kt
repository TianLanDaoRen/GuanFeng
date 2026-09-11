package com.yisiyun.guanfeng.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.core.AssociationAnalyzer
import com.yisiyun.guanfeng.core.AssociationSummary
import com.yisiyun.guanfeng.core.CheckInRecord
import com.yisiyun.guanfeng.core.HourlyBucket
import com.yisiyun.guanfeng.data.RecorderState
import com.yisiyun.guanfeng.log.CheckInHistory
import com.yisiyun.guanfeng.log.SessionHistory
import java.util.TimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 关联视图的观察周期。一周足够看出苗头，又不至于在 189dp 宽上挤成一团。 */
private const val PERIOD_DAYS = 7

/**
 * 关联视图的缓存：一次汇总要扫约 12 万行原始样本（一周 5 秒采样），
 * 每次翻页都重算会明显卡顿，所以 10 分钟内复用上一次结果。
 */
private object AssociationCache {
    var computedAtMs: Long = 0L
    var summary: AssociationSummary? = null
    var checkIns: List<CheckInRecord> = emptyList()
}

/**
 * 第四屏 · 关联：把打卡点叠到气压曲线上。
 *
 * 这一屏的意义是**把「气压 × 不适」摆在眼前**——气压曲线由小时均值构成，
 * 打卡点按当时的实际气压落在曲线上，一眼就能看出打卡是不是扎堆在气压骤变的时候。
 *
 * ⚠️ 一周 7 天、几次打卡，**样本量在统计上不足以证明任何关联**。
 * 所以本屏只描述事实、并明写样本量，不给因果判断——想在手腕上"看出结论"是自欺。
 */
@Composable
fun AssociationPage(state: RecorderState) {
    val context = LocalContext.current
    var summary by remember { mutableStateOf(AssociationCache.summary) }
    var loading by remember { mutableStateOf(AssociationCache.summary == null) }

    LaunchedEffect(Unit) {
        val now = System.currentTimeMillis()
        val fresh = AssociationCache.summary != null &&
            now - AssociationCache.computedAtMs < 10 * 60 * 1000L
        if (!fresh) {
            loading = true
            val zoneOffsetMs = TimeZone.getDefault().getOffset(now).toLong()
            val computed = withContext(Dispatchers.Default) {
                val hourly = SessionHistory.loadHourlyRollup(context, PERIOD_DAYS, now)
                val checkIns = CheckInHistory.loadRecent(context, PERIOD_DAYS, now)
                // 打卡点要落在它当时的气压高度上，所以把打卡记录一起带出分析结果
                AssociationAnalyzer.analyze(hourly, checkIns, PERIOD_DAYS, zoneOffsetMs) to checkIns
            }
            AssociationCache.summary = computed.first
            AssociationCache.checkIns = computed.second
            AssociationCache.computedAtMs = now
            summary = computed.first
            loading = false
        }
    }

    val current = summary
    val checkIns = AssociationCache.checkIns

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("关联", color = Color(0xFFB79CE8), fontSize = 11.sp)
            Spacer(Modifier.fillMaxWidth(0.1f))
            Text("近 $PERIOD_DAYS 天", color = Color(0xFF6E6E6E), fontSize = 8.sp)
        }

        if (loading || current == null) {
            Text("汇总中…", color = Color(0xFF8A8A8A), fontSize = 10.sp)
            return@Column
        }

        if (current.hourly.isEmpty()) {
            Text("还没有历史数据", color = Color(0xFF8A8A8A), fontSize = 10.sp)
            Text(
                text = "小时级汇总从现在开始积累，攒够一天就能看到曲线",
                color = Color(0xFF5E5E5E),
                fontSize = 8.sp,
                lineHeight = 11.sp,
            )
            return@Column
        }

        PressureCheckInChart(
            hourly = current.hourly,
            checkIns = checkIns,
            periodDays = PERIOD_DAYS,
            modifier = Modifier.fillMaxWidth().height(100.dp),
        )

        val overlap = current.overlapRatio
        Text(
            text = "打卡 ${current.checkInCount} 次 · 气压大变化日 ${current.bigSwingDays} 天",
            color = Color(0xFFD0D0D0),
            fontSize = 9.sp,
        )
        Text(
            text = if (overlap == null) {
                "还没有打卡记录"
            } else {
                "落在变化日的打卡 ${current.checkInsOnBigSwingDays} 次（%.0f%%）".format(overlap * 100)
            },
            color = Color(0xFFE8C36A),
            fontSize = 9.sp,
        )
        Text(
            text = "有效天 ${current.daysWithData} 天 · 样本量小，仅作观察，不作结论",
            color = Color(0xFF5E5E5E),
            fontSize = 7.sp,
            lineHeight = 10.sp,
        )
    }
}

/** 气压小时曲线 + 打卡点。空洞处断开折线，不假装数据连续。 */
@Composable
private fun PressureCheckInChart(
    hourly: List<HourlyBucket>,
    checkIns: List<CheckInRecord>,
    periodDays: Int,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val fromMs = hourly.first().hourStartMs
        val toMs = fromMs + periodDays.toLong() * 24 * 60 * 60 * 1000
        val minPressure = hourly.minOf { it.minHpa }
        val maxPressure = hourly.maxOf { it.maxHpa }
        val span = (maxPressure - minPressure).coerceAtLeast(1f)

        fun xOf(timestampMs: Long): Float =
            ((timestampMs - fromMs).toFloat() / (toMs - fromMs)) * size.width

        fun yOf(pressure: Float): Float =
            size.height - ((pressure - minPressure) / span) * size.height

        // 折线：仅相邻小时连续时才连线（间隔超过 2 小时视为断档，断开）
        val path = Path()
        var started = false
        hourly.forEachIndexed { index, bucket ->
            val px = xOf(bucket.hourStartMs)
            val py = yOf(bucket.avgHpa)
            val continuous = index == 0 ||
                bucket.hourStartMs - hourly[index - 1].hourStartMs <= 2L * 60 * 60 * 1000
            if (!continuous) started = false
            if (!started) {
                path.moveTo(px, py)
                started = true
            } else {
                path.lineTo(px, py)
            }
        }
        drawPath(
            path = path,
            color = Color(0xFF7FD1E8),
            style = Stroke(width = 2f),
        )

        // 打卡点：落在它当时的气压高度上
        checkIns.forEach { record ->
            val pressure = record.pressureHpa ?: hourly
                .firstOrNull { it.hourStartMs == com.yisiyun.guanfeng.core.HourlyAccumulator.floorToHour(record.timestampMs) }
                ?.avgHpa
                ?: return@forEach
            drawCircle(
                color = Color(0xFFE8C36A),
                radius = 3.2f,
                center = Offset(xOf(record.timestampMs), yOf(pressure)),
            )
        }
    }
}
