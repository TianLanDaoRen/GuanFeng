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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.style.TextAlign
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
import com.yisiyun.guanfeng.data.PressureRecorder
import com.yisiyun.guanfeng.data.RecorderState
import com.yisiyun.guanfeng.ui.components.PageHeader
import com.yisiyun.guanfeng.ui.components.SUBTITLE_GRAY
import com.yisiyun.guanfeng.log.CheckInHistory
import com.yisiyun.guanfeng.log.HourlyArchive
import com.yisiyun.guanfeng.log.SessionHistory
import com.yisiyun.guanfeng.ui.components.MarkdownText
import java.util.TimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** 「气压 × 体感」页的观察周期。 */
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
 * 「气压 × 体感」页缓存：一次汇总要扫约 12 万行原始样本，每次翻页都重算会卡。
 * 但打卡是用户的实时动作——所以缓存带版本号，打卡后立刻失效重算。
 */
private object AssociationCache {
    var computedAtMs: Long = 0L
    var version: Int = -1
    /** 近 7 天口径：曲线与页面上的数字用它。 */
    var summary: AssociationSummary? = null
    /** 全部可用历史口径：AI 报告要两段都给。 */
    var allSummary: AssociationSummary? = null
    var checkIns: List<CheckInRecord> = emptyList()
    /** 小时归档（含体感分量），AI 报告的 body_context 用它。 */
    var hourlyRows: List<com.yisiyun.guanfeng.core.HourlyRow> = emptyList()
    var recentFromMs: Long = 0L
}

private const val DAY_MS = 24L * 60L * 60L * 1000L

/**
 * 第四屏 · 气压 × 体感：把体感打卡点叠到气压曲线上，并提供 AI 报告入口。
 *
 * 这一屏有两个职责：
 *   1. 把「气压 × 体感」摆在眼前——曲线由小时均值构成，打卡点按当时的实际气压落位；
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
            // 长期历史读**小时归档**（约 1 KB/天、永不删除），不再扫十万行原始样本；
            // 再补上尚未落盘的当前小时，否则最近一小时在图上永远是空的。
            val allRows = HourlyArchive.dedupeByHour(
                HourlyArchive.loadAll(context) + listOfNotNull(PressureRecorder.currentHourRow())
            )
            val recentFromMs = now - PERIOD_DAYS * DAY_MS
            val toBucket = { row: com.yisiyun.guanfeng.core.HourlyRow ->
                HourlyBucket(row.hourStartMs, row.weatherAvgHpa, row.weatherMinHpa, row.weatherMaxHpa, row.samples)
            }
            val allBuckets = allRows.map(toBucket)
            val recentBuckets = allRows.filter { it.hourStartMs >= recentFromMs }.map(toBucket)
            val checkIns = CheckInHistory.loadRecent(context, SessionHistory.ALL_HISTORY_DAYS, now)
            val spanDays = if (allBuckets.isEmpty()) 0 else {
                ((allBuckets.last().hourStartMs - allBuckets.first().hourStartMs) / DAY_MS).toInt() + 1
            }
            ComputedAssociation(
                recent = AssociationAnalyzer.analyze(recentBuckets, checkIns, PERIOD_DAYS, zoneOffsetMs),
                all = AssociationAnalyzer.analyze(allBuckets, checkIns, spanDays, zoneOffsetMs),
                checkIns = checkIns,
                hourlyRows = allRows,
                recentFromMs = recentFromMs,
            )
        }
        AssociationCache.summary = computed.recent
        AssociationCache.allSummary = computed.all
        AssociationCache.checkIns = computed.checkIns
        AssociationCache.hourlyRows = computed.hourlyRows
        AssociationCache.recentFromMs = computed.recentFromMs
        AssociationCache.computedAtMs = now
        AssociationCache.version = checkInVersion
        summary = computed.recent
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
    // 门槛数的是**两种类别加起来**的打卡数。
    // 原先只数症状——于是"只记了一条舒适"时按钮是灰的，
    // 而那条舒适恰恰就是对照组数据，AI 报告本来就有话可说。
    val reportCheckIns = current?.let { it.checkInCount + it.comfortCount } ?: 0
    val readyForReport = current != null &&
        current.daysWithData >= MIN_DAYS_FOR_REPORT &&
        reportCheckIns >= 1

    fun startReport() {
        val snapshot = current ?: return
        val allSnapshot = AssociationCache.allSummary ?: snapshot
        reportFlow.value = ""
        reportError = null
        reportNotice = null
        generating = true
        reportOpen = true
        scope.launch {
            // 节流：每个 delta 都写一遍状态会让整页反复重组（真机日志里出现
            // Skipped 62 frames / Davey 1235ms）。累积到 150ms 才刷新一次界面。
            val buffer = StringBuilder()
            var lastPublishMs = 0L
            val result = PublicAiClient.stream(
                systemPrompt = AiDigest.buildSystemInstruction(),
                userContent = AiDigest.buildUserContent(
                    AiDigest.build(
                        allHistory = allSnapshot,
                        recent = snapshot,
                        checkIns = checkIns,
                        zoneOffsetMs = zoneOffsetMs,
                        hourlyRows = AssociationCache.hourlyRows,
                        recentFromMs = AssociationCache.recentFromMs,
                    )
                ),
                onDelta = { delta ->
                    buffer.append(delta)
                    val now = android.os.SystemClock.elapsedRealtime()
                    if (now - lastPublishMs >= 150L) {
                        lastPublishMs = now
                        reportFlow.value = buffer.toString()
                    }
                },
                onNotice = { notice -> reportNotice = notice },
            )
            // 收尾：把最后不足 150ms 的那一段也补上，否则结尾会缺一块
            reportFlow.value = buffer.toString()
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
            // 标题原为「关联」——名字是我起的，它说明不了这一页在干什么。
            // 这一页真正做的事是：把体感打卡的点叠到气压曲线上，看两者有没有关系。
            // 写成「气压 × 体感」，与第二屏的「环境 × 身体」同一种句式。
            // 版式即 PageHeader 的样板（其余各页都向它统一）。
            PageHeader(
                title = "气压 × 体感",
                titleColor = Color(0xFFB79CE8),
                subtitle = "曲线 24h · 统计近 $PERIOD_DAYS 天",
            )

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
                // 中间这截**可滚动**，底部的「生成 AI 报告」固定在页面下方。
                //
                // 原因：这一页的内容会随数据变多（图表 + 三行统计 + 图例 + 提示），
                // 而 189×248dp 的可用高度是固定的。我上一次加大图表时没算预算，
                // 总高约 228dp > 可用 222dp，**按钮直接被挤出屏幕**。
                // 把"重要操作"钉在底部、让可变内容自己滚，这类问题才不会复发。
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    PressureCheckInChart(
                        hourly = current.hourly,
                        checkIns = checkIns,
                        nowMs = System.currentTimeMillis(),
                        modifier = Modifier.fillMaxWidth().height(88.dp),
                    )
                    // 一行一件事。原先三件事挤在一行里（打卡数 · 大变化日 · 落在其上的打卡），
                    // 既读不清哪个数字对应什么，也容易被当成一个整体去理解——主人提得对。
                    // 两组分开报：症状组是"事件"，舒适组是**基准线**。
                    // 合成一个比值等于把"今天挺舒服"也算成不适——那正是加类别这一维要解决的事。
                    Text(
                        text = "不适 ${current.checkInCount} 次 · 舒适 ${current.comfortCount} 次",
                        color = Color(0xFFD0D0D0),
                        fontSize = 9.sp,
                    )
                    Text(
                        text = "气压大变化日 ${current.bigSwingDays} 天",
                        color = Color(0xFFD0D0D0),
                        fontSize = 9.sp,
                    )
                    val overlap = current.overlapRatio
                    Text(
                        text = if (overlap == null) {
                            "不适打卡还没有记录"
                        } else {
                            "不适落在变化日 ${current.checkInsOnBigSwingDays} 次（%.0f%%）"
                                .format(overlap * 100)
                        },
                        color = Color(0xFFE8C36A),
                        fontSize = 9.sp,
                    )
                    val comfortRatio = current.comfortRatio
                    Text(
                        text = if (comfortRatio == null) {
                            "舒适打卡还没有记录（它是对照组）"
                        } else {
                            "舒适落在变化日 ${current.comfortOnBigSwingDays} 次（%.0f%%）"
                                .format(comfortRatio * 100)
                        },
                        color = Color(0xFF6EE7A8),
                        fontSize = 9.sp,
                    )
                    Text(
                        text = "青线＝气压（已去高度） · 黄点＝不适 · 绿点＝舒适",
                        color = Color(0xFF5E6A72),
                        fontSize = 7.sp,
                    )
                    Text(
                        text = "有效天 ${current.daysWithData} 天 · 样本量小，仅作观察，不作结论",
                        color = Color(0xFF5E5E5E),
                        fontSize = 7.sp,
                        lineHeight = 10.sp,
                    )
                }

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
                        text = "数据不足：需至少 1 天气压记录与 1 次体感打卡（当前 ${current.daysWithData} 天 / " +
                            "$reportCheckIns 次）",
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

        // 同意弹层必须画在这个 Box **里面**。
        // 原先写成 Box 的兄弟节点，结果在 pager 页面里根本显示不出来——
        // 而上面这个报告浮层是画在 Box 内、真机验证过能显示的。结构对齐才是正解。
        if (showConsent) {
            ConsentOverlay(
                onCancel = { showConsent = false },
                onConfirm = {
                    showConsent = false
                    startReport()
                },
            )
        }
    }
}

/** 联网同意弹层：整页覆盖、可滚动、文案不含任何 Markdown 标记。 */
@Composable
private fun ConsentOverlay(onCancel: () -> Unit, onConfirm: () -> Unit) {
    // 不用 Material AlertDialog：它在 189dp 宽的表盘上会把长文案**直接裁掉**
    // （实测最后一行整个消失），而且不渲染 Markdown——文案里的 **加粗** 会原样露出星号。
    // 自己搭的好处：尺寸可控、可滚动兜底、样式与其余界面一致。
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text("需要联网", color = Color.White, fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Text(
                text = "将上传：" + "\n" +
                    "· 气压与体感的小时级统计" + "\n" +
                    "· 你的体感打卡记录（含手写备注原文）与天气实况" + "\n\n" +
                    "不会上传：" + "\n" +
                    "设备标识、账号身份、体征逐点读数。",
                color = Color(0xFFC8C8C8),
                fontSize = 9.sp,
                lineHeight = 13.sp,
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(30.dp)
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(15.dp))
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center,
            ) {
                Text("取消", color = Color(0xFFB0B0B0), fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(30.dp)
                    .background(Color(0xFF2A4A6F), RoundedCornerShape(15.dp))
                    .clickable { onConfirm() },
                contentAlignment = Alignment.Center,
            ) {
                Text("继续", color = Color(0xFFD8E8FA), fontSize = 11.sp)
            }
        }
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

/**
 * 气压曲线 + 打卡点。
 *
 * ## 这次重画解决了三个问题（都由主人指出）
 *
 * 1. **横轴原先根本没有时间含义**：`fromMs` 取第一个小时桶、`toMs = fromMs + 7 天`，
 *    而数据只覆盖最新的一小段——于是整条线被挤在横轴最左边 2~6%，形状完全读不出来。
 *    现在横轴是**真实时间窗**：`[现在 − 24 小时, 现在]`。
 *
 * 2. **纵轴按数据 min/max 自动拉伸**，0.3 hPa 的微小起伏会被拉满整屏高度、看着像大山。
 *    现在给纵轴设**最小量程 1.0 hPa**，并把上下限的数值直接标出来——
 *    一眼就能看出"这条线其实只动了 0.3 hPa"。
 *
 * 3. **画的是"去掉高度"的天气分量，而第一页显示的是原始气压**，两者本就不同
 *    （你在屋里走动会改变手腕高度，原始值随之变）。图例里写明，避免把两个数当成一个。
 */
@Composable
private fun PressureCheckInChart(
    hourly: List<HourlyBucket>,
    checkIns: List<CheckInRecord>,
    nowMs: Long,
    modifier: Modifier = Modifier,
) {
    val fromMs = nowMs - CHART_WINDOW_HOURS * 3_600_000L
    // 多留一个桶，免得最新的那个小时因为边界被切掉
    val visible = hourly.filter { it.hourStartMs >= fromMs - 3_600_000L }
    if (visible.isEmpty()) return

    // ## 纵轴范围：用**每小时 min–max 的极差**定，并且把 min–max 画成淡色带
    //
    // 这一段来回过几次，把过程留着免得再绕（也免得我第三次把结论忘掉）：
    //
    //   ① 最初轴用每小时 min/max，曲线画的却是小时均值 → 曲线永远只占纵轴一小截，
    //      主人两次截图都在问这件事。"轴的取值口径必须与图形的口径一致"是对的。
    //   ② 当时的修法是把轴也改成按均值定。但那等于**把观测到的真实极值藏起来**：
    //      黄点（打卡那一刻的瞬时值）看起来"不在线上"，根子就在这里。
    //   ③ 主人 2026-09-12 给了真正的解法：**把 min–max 画出来，并用它定轴。**
    //      于是两件事同时成立——淡色带恰好铺满纵轴（观测范围被完整交代），
    //      而均值折线因为"一小时内的气压变化本来就小"仍然几乎占满纵轴。
    //
    // 我原先以为"占满纵轴"与"如实呈现范围"是对立的，那是把问题想窄了：
    // 对立的从来不是这两个，而是**只画一个量、却拿另一个量去定轴**。
    val bandMin = visible.minOf { it.minHpa }
    val bandMax = visible.maxOf { it.maxHpa }
    // 仍然保留一个极小的下限，只为了避免"数据完全平坦时除零"这种退化情形。
    val bandSpan = (bandMax - bandMin).coerceAtLeast(DEGENERATE_SPAN_HPA)
    val pad = bandSpan * 0.05f
    val low = bandMin - pad
    val high = bandMax + pad
    // 映射用它自己的量程：上面加了 padding，就不能再拿 bandSpan 当分母，
    // 否则图形会超出上下边界 5%（这类"两个量混用"的错在这个文件里犯过两次了）。
    val axisSpan = high - low

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                fun xOf(timestampMs: Long): Float =
                    ((timestampMs - fromMs).toFloat() / (nowMs - fromMs)) * size.width

                fun yOf(pressure: Float): Float =
                    size.height - ((pressure - low) / axisSpan) * size.height

                // 暗色底 + 淡网格：有参照才看得出量级
                drawRoundRect(
                    color = Color(0xFF101418),
                    cornerRadius = CornerRadius(6f, 6f),
                    size = size,
                )
                val gridColor = Color(0xFF232A31)
                for (row in 1..3) {
                    val y = size.height * row / 4f
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                }
                for (col in 1..3) {
                    val x = size.width * col / 4f
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                }

                // 淡色带：每小时的 min–max。先画带再画线，线压在带上。
                //
                // 断档规则与折线**完全一致**（间隔 > 2 小时视为断档）：跨空洞把带连起来，
                // 会画出一段并不存在的"渐变"，那和跨空洞做回归是同一类错误。
                val bandColor = Color(0x2E7FD1E8)
                fun drawEnvelope(from: Int, to: Int) {
                    if (to <= from) {
                        // 只有单独一个小时：画一条竖线，别退化成一个看不见的点
                        val only = visible[from]
                        val x = xOf(only.hourStartMs)
                        drawLine(
                            color = bandColor,
                            start = Offset(x, yOf(only.maxHpa)),
                            end = Offset(x, yOf(only.minHpa)),
                            strokeWidth = 3f,
                        )
                        return
                    }
                    val band = Path()
                    for (i in from..to) {
                        val bucket = visible[i]
                        val x = xOf(bucket.hourStartMs)
                        if (i == from) band.moveTo(x, yOf(bucket.maxHpa))
                        else band.lineTo(x, yOf(bucket.maxHpa))
                    }
                    for (i in to downTo from) {
                        band.lineTo(xOf(visible[i].hourStartMs), yOf(visible[i].minHpa))
                    }
                    band.close()
                    drawPath(path = band, color = bandColor)
                }
                var runStart = 0
                visible.forEachIndexed { index, bucket ->
                    val broken = index > 0 &&
                        bucket.hourStartMs - visible[index - 1].hourStartMs > 2L * 3_600_000L
                    if (broken) {
                        drawEnvelope(runStart, index - 1)
                        runStart = index
                    }
                }
                drawEnvelope(runStart, visible.lastIndex)

                // 折线：仅在相邻小时连续时才连线（超 2 小时视为断档）
                val path = Path()
                var started = false
                visible.forEachIndexed { index, bucket ->
                    val px = xOf(bucket.hourStartMs)
                    val py = yOf(bucket.avgHpa)
                    val continuous = index == 0 ||
                        bucket.hourStartMs - visible[index - 1].hourStartMs <= 2L * 3_600_000L
                    if (!continuous) started = false
                    if (!started) {
                        path.moveTo(px, py)
                        started = true
                    } else {
                        path.lineTo(px, py)
                    }
                }
                drawPath(path = path, color = Color(0xFF7FD1E8), style = Stroke(width = 2.2f))

                // 打卡点：落在当时的气压高度上
                checkIns.forEach { record ->
                    if (record.timestampMs < fromMs) return@forEach
                    val pressure = record.chartPressureHpa ?: visible
                        .firstOrNull {
                            it.hourStartMs == HourlyAccumulator.floorToHour(record.timestampMs)
                        }
                        ?.avgHpa
                        ?: return@forEach
                    val center = Offset(xOf(record.timestampMs), yOf(pressure))
                    // 按类别分色：不适＝琥珀、舒适＝青绿。
                    // 两组一分开，这张图才真正在回答"我和气压有没有关系"——
                    // 只看一种点只能说"扎堆"，两组对照才知道扎堆是不是稀奇。
                    val dotColor = if (record.isComfort) Color(0xFF6EE7A8) else Color(0xFFE8C36A)
                    drawCircle(dotColor, radius = 3.6f, center = center)
                    drawCircle(Color(0xFF1A1408), radius = 1.4f, center = center)
                }
            }

            // 纵轴上下限：把量程直接摆在图上，是"别把小起伏看成大山"的关键
            Text(
                text = "%.1f".format(high),
                color = Color(0xFF6E6E6E),
                fontSize = 7.sp,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 3.dp, top = 2.dp),
            )
            Text(
                text = "%.1f".format(low),
                color = Color(0xFF6E6E6E),
                fontSize = 7.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 3.dp, bottom = 2.dp),
            )
        }

        // 横轴时刻：四等分处标出真实钟点
        Row(modifier = Modifier.fillMaxWidth().padding(top = 2.dp)) {
            for (mark in 0..3) {
                val ts = fromMs + (nowMs - fromMs) * mark / 3
                val label = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US)
                    .format(java.util.Date(ts))
                Text(
                    text = if (mark == 3) "现在" else label,
                    color = Color(0xFF6E6E6E),
                    fontSize = 6.sp,
                    textAlign = when (mark) {
                        0 -> TextAlign.Start
                        3 -> TextAlign.End
                        else -> TextAlign.Center
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** 图画的时间窗：24 小时够看完一场天气过程，也远大于最小量程带来的可读性需求。 */
private const val CHART_WINDOW_HOURS = 24

/**
 * 纵轴的退化下限（hPa）：只在数据完全平坦时兜底，避免除零。
 * 它**不再承担"限制分辨率"的职责**——分辨率交给数据本身，量级交给轴上的数字。
 */
private const val DEGENERATE_SPAN_HPA = 0.2f

/** 一次读取算出的两段口径与体感归档。 */
private data class ComputedAssociation(
    val recent: AssociationSummary,
    val all: AssociationSummary,
    val checkIns: List<CheckInRecord>,
    val hourlyRows: List<com.yisiyun.guanfeng.core.HourlyRow>,
    val recentFromMs: Long,
)
