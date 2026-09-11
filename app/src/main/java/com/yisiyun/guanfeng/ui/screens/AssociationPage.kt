package com.yisiyun.guanfeng.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yisiyun.guanfeng.ai.PublicAiClient
import com.yisiyun.guanfeng.core.AiDigest
import com.yisiyun.guanfeng.core.AssociationAnalyzer
import com.yisiyun.guanfeng.core.AssociationSummary
import com.yisiyun.guanfeng.core.CheckInRecord
import com.yisiyun.guanfeng.core.HourlyAccumulator
import com.yisiyun.guanfeng.core.HourlyBucket
import com.yisiyun.guanfeng.data.CheckInSignal
import com.yisiyun.guanfeng.data.RecorderState
import com.yisiyun.guanfeng.log.CheckInHistory
import com.yisiyun.guanfeng.log.SessionHistory
import com.yisiyun.guanfeng.ui.components.MarkdownText
import java.util.TimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** 关联视图的观察周期。 */
private const val PERIOD_DAYS = 7

/**
 * AI 报告的可用门槛。
 *
 * 口径要分清两件事：
 *   · **硬门槛**：至少要有一点气压记录和一次打卡，否则没有任何可分析的对象——
 *     这是"能不能点"。
 *   · **建议量**：攒满一周再生成，结论才稍微站得住——这是"该不该点"。
 * 后者写在提示里、由用户自己判断，而不是替他锁死按钮：
 * 判断权交给使用的人，我们只负责把代价与局限说清。
 */
private const val MIN_DAYS_FOR_REPORT = 1
private const val SUGGESTED_DAYS_FOR_REPORT = 7

/**
 * 关联页缓存：一次汇总要扫约 12 万行原始样本，每次翻页都重算会卡。
 * 但打卡是用户的实时动作——所以缓存带版本号，打卡后立刻失效重算。
 */
private object AssociationCache {
    var computedAtMs: Long = 0L
    var version: Int = -1
    var summary: AssociationSummary? = null
    var checkIns: List<CheckInRecord> = emptyList()
}

/**
 * 第四屏 · 关联：把打卡点叠到气压曲线上，并提供 AI 报告入口。
 *
 * 这一屏有两个职责：
 *   1. 把「气压 × 不适」摆在眼前——曲线由小时均值构成，打卡点按当时的实际气压落位；
 *   2. 在数据够用时，让用户**主动决定**要不要把这份聚合统计发出去做一次 AI 解读。
 *
 * 关于"该不该让手表自己调 LLM"：核心功能（趋势、解耦、打卡）全部离线，
 * 这一项是锦上添花。代价（需要联网、要把聚合统计发出去）由用户在按钮后的
 * 弹窗里显式确认，我们不替他决定。
 *
 * ⚠️ 样本量口径：一周 7 天、几次打卡在统计上不足以证明任何关联。
 * 所以本屏只描述事实、并明写样本量；AI 报告的提示词里也硬约束禁止医学结论。
 */
@Composable
fun AssociationPage(state: RecorderState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val checkInVersion by CheckInSignal.version.collectAsState()

    var summary by remember { mutableStateOf(AssociationCache.summary) }
    var loading by remember { mutableStateOf(AssociationCache.summary == null) }

    // 打卡事件会改变 version → 这里立刻重算，无需退出应用
    LaunchedEffect(checkInVersion) {
        val now = System.currentTimeMillis()
        val fresh = AssociationCache.summary != null &&
            AssociationCache.version == checkInVersion &&
            now - AssociationCache.computedAtMs < 10 * 60 * 1000L
        if (fresh) {
            summary = AssociationCache.summary
            loading = false
            return@LaunchedEffect
        }
        loading = true
        val zoneOffsetMs = TimeZone.getDefault().getOffset(now).toLong()
        val computed = withContext(Dispatchers.Default) {
            val hourly = SessionHistory.loadHourlyRollup(context, PERIOD_DAYS, now)
            val checkIns = CheckInHistory.loadRecent(context, PERIOD_DAYS, now)
            AssociationAnalyzer.analyze(hourly, checkIns, PERIOD_DAYS, zoneOffsetMs) to checkIns
        }
        AssociationCache.summary = computed.first
        AssociationCache.checkIns = computed.second
        AssociationCache.computedAtMs = now
        AssociationCache.version = checkInVersion
        summary = computed.first
        loading = false
    }

    val current = summary
    val checkIns = AssociationCache.checkIns

    // ---- AI 报告状态 ----
    var showConsent by remember { mutableStateOf(false) }
    var reportOpen by remember { mutableStateOf(false) }
    var generating by remember { mutableStateOf(false) }
    var reportError by remember { mutableStateOf<String?>(null) }
    var reportNotice by remember { mutableStateOf<String?>(null) }
    val reportFlow = remember { MutableStateFlow("") }
    val reportText by reportFlow.collectAsState()

    val zoneOffsetMs = remember { TimeZone.getDefault().getOffset(System.currentTimeMillis()).toLong() }
    val readyForReport = current != null &&
        current.daysWithData >= MIN_DAYS_FOR_REPORT &&
        current.checkInCount >= 1

    fun startReport() {
        val snapshot = current ?: return
        reportFlow.value = ""
        reportError = null
        reportNotice = null
        generating = true
        reportOpen = true
        scope.launch {
            val result = PublicAiClient.stream(
                systemPrompt = AiDigest.buildSystemInstruction(PERIOD_DAYS),
                userContent = AiDigest.buildUserContent(
                    AiDigest.build(snapshot, checkIns, zoneOffsetMs)
                ),
                onDelta = { delta -> reportFlow.value += delta },
                onNotice = { notice -> reportNotice = notice },
            )
            generating = false
            reportNotice = null
            result.onFailure { reportError = it.message ?: "请求失败" }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("关联", color = Color(0xFFB79CE8), fontSize = 11.sp)
                Spacer(Modifier.fillMaxWidth(0.1f))
                Text("近 $PERIOD_DAYS 天", color = Color(0xFF6E6E6E), fontSize = 8.sp)
            }

            if (loading || current == null) {
                Text("汇总中…", color = Color(0xFF8A8A8A), fontSize = 10.sp)
            } else if (current.hourly.isEmpty()) {
                Text("还没有历史数据", color = Color(0xFF8A8A8A), fontSize = 10.sp)
                Text(
                    text = "小时级汇总从现在开始积累，攒够一天就能看到曲线",
                    color = Color(0xFF5E5E5E),
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                )
            } else {
                PressureCheckInChart(
                    hourly = current.hourly,
                    checkIns = checkIns,
                    periodDays = PERIOD_DAYS,
                    modifier = Modifier.fillMaxWidth().height(94.dp),
                )
                Text(
                    text = "打卡 ${current.checkInCount} 次 · 气压大变化日 ${current.bigSwingDays} 天",
                    color = Color(0xFFD0D0D0),
                    fontSize = 9.sp,
                )
                val overlap = current.overlapRatio
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

                Spacer(Modifier.height(2.dp))
                if (readyForReport) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(Color(0xFF2A4A6F), RoundedCornerShape(15.dp))
                            .clickable { showConsent = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("生成 AI 报告", color = Color(0xFFD8E8FA), fontSize = 11.sp)
                    }
                    Text(
                        text = if (current.daysWithData < SUGGESTED_DAYS_FOR_REPORT) {
                            "样本仍小（${current.daysWithData} 天）· 建议攒满 $SUGGESTED_DAYS_FOR_REPORT 天更可信 · 需联网"
                        } else {
                            "样本越小结论越弱 · 需联网，由你确认"
                        },
                        color = Color(0xFF7A7A7A),
                        fontSize = 7.sp,
                    )
                } else {
                    Text(
                        text = "数据不足：需至少 1 天气压记录与 1 次打卡（当前 ${current.daysWithData} 天 / " +
                            "${current.checkInCount} 次）",
                        color = Color(0xFF8A8A8A),
                        fontSize = 8.sp,
                        lineHeight = 10.sp,
                    )
                }
            }
        }

        if (reportOpen) {
            ReportOverlay(
                text = reportText,
                generating = generating,
                notice = reportNotice,
                error = reportError,
                onClose = { reportOpen = false },
                onRetry = { startReport() },
            )
        }
    }

    if (showConsent) {
        AlertDialog(
            onDismissRequest = { showConsent = false },
            title = { Text("需要联网", fontSize = 13.sp, color = Color.White) },
            text = {
                Text(
                    text = "将把本页的聚合统计（天数、气压极值、打卡次数与标签分布）" +
                        "发到 yunsisanren.top 做一次分析。\n" +
                        "不含逐条打卡记录、备注原文与体征逐点数据。",
                    fontSize = 9.sp,
                    color = Color(0xFFC8C8C8),
                    lineHeight = 12.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConsent = false
                    startReport()
                }) { Text("继续", fontSize = 11.sp) }
            },
            dismissButton = {
                TextButton(onClick = { showConsent = false }) { Text("取消", fontSize = 11.sp) }
            },
        )
    }
}

/** 报告浮层：整页覆盖，可滚动，流式追加文本并按极简 Markdown 渲染。 */
@Composable
private fun ReportOverlay(
    text: String,
    generating: Boolean,
    notice: String?,
    error: String?,
    onClose: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("AI 报告", color = Color(0xFFB79CE8), fontSize = 11.sp)
            Spacer(Modifier.fillMaxWidth(0.06f))
            if (generating) {
                Text("生成中…", color = Color(0xFF8A8A8A), fontSize = 8.sp)
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "关闭",
                color = Color(0xFF7FD1E8),
                fontSize = 9.sp,
                modifier = Modifier.clickable { onClose() },
            )
        }
        Spacer(Modifier.height(4.dp))
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            when {
                error != null -> Text(
                    text = "生成失败：$error",
                    color = Color(0xFFFF8A8A),
                    fontSize = 9.sp,
                    lineHeight = 13.sp,
                )

                text.isEmpty() && generating -> Text(
                    text = notice ?: "正在分析…（公共接口无 SLA，高峰可能排队）",
                    color = if (notice != null) Color(0xFFE8C36A) else Color(0xFF8A8A8A),
                    fontSize = 9.sp,
                    lineHeight = 13.sp,
                )

                else -> MarkdownText(raw = text)
            }
        }
        if (error != null) {
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(Color(0xFF2A4A6F), RoundedCornerShape(14.dp))
                    .clickable { onRetry() },
                contentAlignment = Alignment.Center,
            ) {
                Text("重试", color = Color(0xFFD8E8FA), fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "由公共 AI 接口生成 · 样本量小，仅供参考，非医学建议",
            color = Color(0xFF5E5E5E),
            fontSize = 7.sp,
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
        drawPath(path = path, color = Color(0xFF7FD1E8), style = Stroke(width = 2f))

        checkIns.forEach { record ->
            val pressure = record.pressureHpa ?: hourly
                .firstOrNull { it.hourStartMs == HourlyAccumulator.floorToHour(record.timestampMs) }
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
