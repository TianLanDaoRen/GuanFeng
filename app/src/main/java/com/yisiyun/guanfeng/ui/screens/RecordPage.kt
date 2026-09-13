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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.core.TrendConfidence
import com.yisiyun.guanfeng.core.WeatherRule
import com.yisiyun.guanfeng.data.PressureRecorder
import com.yisiyun.guanfeng.data.RecorderState
import com.yisiyun.guanfeng.ui.components.PageHeader
import com.yisiyun.guanfeng.ui.components.SUBTITLE_INK
import com.yisiyun.guanfeng.log.WeatherObservation
import com.yisiyun.guanfeng.service.AlertState
import com.yisiyun.guanfeng.service.TrendNotifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import com.yisiyun.guanfeng.log.WeatherObservationLogger
import com.yisiyun.guanfeng.ui.theme.INK_MID
import com.yisiyun.guanfeng.ui.theme.INK_LOW
import com.yisiyun.guanfeng.ui.theme.INK_HIGH

/**
 * 第五屏 · 记录：这一屏存在的意义是让「它到底有没有在记」一眼可见、可以自己验证。
 *
 * 它同时承担「诊断页」的职责——主屏放不下的完整依据（例如为什么判为趋势不可信）
 * 都挪到了这里。内容比一屏高，所以必须可滚动，否则下面的高度解耦信息会被裁掉。
 */
@Composable
fun RecordPage(state: RecorderState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val trend = state.trend
    val rationale = trend?.let { WeatherRule.assess(it, state.recentFallHpa, state.episode).rationale }
    var observedToday by remember { mutableStateOf(WeatherObservationLogger.countToday(context)) }
    var observeFeedback by remember { mutableStateOf("") }
    var noticeFeedback by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        // 主标题固定为「记录」并用本页的身份色（绿）。原先标题写的是「记录中 / 已停止」——
        // 那是**状态**，不是页名；状态挪到副标题去，副标题本来就是这个位置该放的东西。
        // 异常时副标题整条变红，告警语义没丢，只是不再靠标题变色来表达。
        PageHeader(
            title = "记录",
            titleColor = Color(0xFF6EE7A8),
            subtitle = buildString {
                append(if (state.recording) "记录中" else "已停止")
                append(" · ")
                append(if (state.logHealthy) "落盘正常" else "写入异常")
            },
            subtitleColor = if (state.recording && state.logHealthy) {
                SUBTITLE_INK
            } else {
                Color(0xFFFF7A6B)
            },
        )

        // 【版式约定】标题固定、内容滚动。
        // 这一页的内容比一屏高，必须能滚；原先标题也在滚动区内，滚到中间就不知道自己在哪一页。
        // 全应用统一：凡是需要滚动的页，标题都留在滚动区外（关联页早就这么做）。
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text("前台服务 + indicator 保活中", color = INK_LOW, fontSize = 7.sp)

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
            // **必须用跨窗口累计量**（state.elevationOffsetMeters）：
            // trend.elevationMeters 是引擎在"当前 3 小时窗口内"累加的局部量，
            // 同一段电梯在窗口里进进出出会让它从 +70 米翻到 −70 米——
            // 2026-09-12 夜里主人就是被这个数吓着的。详见 RecorderState.elevationOffsetMeters 的说明。
            Kv("累计垂直位移", "%+.1f 米".format(state.elevationOffsetMeters))
            // 这个数是**当前 3 小时窗口内**的高度事件数（不是历史累计）：
            // 主人的一趟散步在窗口里时显示 37，滑出去一半就变 18。
            // 标签必须写清口径——上一版写"解耦样本"，看着像累计数，与旁边的"累计垂直位移"并列时更容易误读。
            Kv("本窗口高度事件", "${trend?.elevationEvents ?: 0} 个")
            Kv("高度状态", if (trend?.isInVerticalTransit == true) "高度变化中" else "高度稳定")
            Kv("置信度", (trend?.confidence ?: TrendConfidence.INSUFFICIENT).label)
            Kv("拟合优度", "%.3f".format(trend?.fitRSquared ?: 0f))
            Kv("窗口覆盖", "%.0f%%".format((trend?.coverageFraction ?: 0f) * 100f))

            if (rationale != null) {
                Spacer(Modifier.height(4.dp))
                Text("判定依据", color = Color(0xFFF2C14E), fontSize = 9.sp)
                Text(
                    text = rationale,
                    color = INK_HIGH,
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                )
            }

            Spacer(Modifier.height(8.dp))
            // 诊断工具：手动触发一次转坏提醒，用于验证通知在 ColorOS Watch 上的呈现
            // （真的等一场气压急降可能要几天）。放在记录页——这页本来就是诊断用途。
            Text("诊断", color = Color(0xFFF2C14E), fontSize = 9.sp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(Color(0xFF2A2A1E), RoundedCornerShape(15.dp))
                    .clickable {
                        // 通知这条路已被系统白名单堵死（实测点了毫无震动），
                        // 所以测试改为验证真正要用的两条腿：**马达震动 + indicator 文案**。
                        // **不立即震**：预约 5 秒后由服务循环兑现。
                        // 这样按下按钮后可以退出应用/熄屏，验证的是
                        // "**服务**能不能调起震动"，而不是"界面还在不在"。
                        AlertState.requestDelayedTest(5_000L)
                        noticeFeedback = "已震动 · indicator 显示 20 秒"
                        scope.launch {
                            delay(20_000)
                            noticeFeedback = "测试窗口结束"
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("测试提醒", color = Color(0xFFE8DFB8), fontSize = 10.sp)
            }
            Text(
                text = if (noticeFeedback.isNotEmpty()) {
                    noticeFeedback
                } else {
                    "按下后 5 秒才震。趁这 5 秒退出应用或熄屏——震了说明服务能调起震动，没震说明这条链路不通"
                },
                color = INK_LOW,
                fontSize = 7.sp,
            )

            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(Color(0xFF2A2A1E), RoundedCornerShape(15.dp))
                    .clickable {
                        PressureRecorder.resetElevationBaseline()
                        noticeFeedback = "高度基准已重置为 0"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("重置高度基准", color = Color(0xFFE8DFB8), fontSize = 10.sp)
            }
            Text(
                text = "累计垂直位移当前 %+.1f 米；被误判污染后点这里归零"
                    .format(state.elevationOffsetMeters),
                color = INK_LOW,
                fontSize = 7.sp,
            )

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
                                val assessment = trend?.let { WeatherRule.assess(it, state.recentFallHpa, state.episode) }
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
                color = INK_LOW,
                fontSize = 7.sp,
            )

            Spacer(Modifier.height(6.dp))
            ArchiveHealthSection()

            Spacer(Modifier.height(6.dp))
            Text(
                text = "每 5 秒一个样本 · 正式窗口 3 小时 · 风雨倾向为启发式规则、未用真实降水校准",
                color = INK_LOW,
                fontSize = 7.sp,
                lineHeight = 10.sp,
            )
        }
    }
}

/**
 * 长期归档体检（见 `log/ArchiveHealth.kt`）。
 *
 * 归档是**唯一永不删除**的数据：原始采样文件只留最近 30 MB（约 15 天）就会被裁掉，
 * 而长期趋势、关联视图、AI 报告全靠归档。它悄悄停止增长的话，后果要**几周后**才被发现，
 * 那时原始样本早被裁掉、那段历史再也补不回来——所以把体检摆在看得见的地方。
 *
 * 数据在 IO 线程上读（尾部窗口 + 很小的归档文件），每 60 秒刷一次；读不到就整块不显示，
 * 不让一次磁盘异常把这一页搞成空白。
 */
@Composable
private fun ArchiveHealthSection() {
    val context = LocalContext.current
    var health by remember { mutableStateOf<com.yisiyun.guanfeng.log.ArchiveHealth?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            health = withContext(Dispatchers.IO) {
                runCatching {
                    com.yisiyun.guanfeng.log.loadArchiveHealth(
                        context = context,
                        nowMs = now,
                        recentWindowMs = ARCHIVE_CHECK_WINDOW_MS,
                    )
                }.getOrNull()
            }
            delay(60_000L)
        }
    }

    val snapshot = health ?: return
    Spacer(Modifier.height(2.dp))
    Text("长期归档", color = Color(0xFF6EE7A8), fontSize = 9.sp)

    // 措辞要准：比对的是"原始文件里有解耦样本、且已经走完的那些整点"，
    // 不是"最近 N 小时"。第一版写"近 18 小时无缺口"，会被读成"归档只覆盖 18 小时"——
    // 而归档其实覆盖 22 小时，只是其余几个整点在原始文件里没有解耦样本可比。
    val gapText = if (snapshot.healthy) {
        "近段 ${snapshot.recentHourCount} 个整点全部已归档"
    } else {
        "近段 ${snapshot.recentHourCount} 个整点里缺 ${snapshot.missingRecentHours.size} 个"
    }
    Kv(
        label = "归档完整性",
        value = gapText,
        // 有缺口才用警示色：健康时用高档白，避免"天天报警"把颜色用成噪声
        valueColor = if (snapshot.healthy) INK_HIGH else Color(0xFFFF7A6B),
    )
    Kv(
        label = "归档覆盖",
        value = buildString {
            append(snapshot.archiveHours)
            append(" 小时 · ")
            append(snapshot.earliestArchiveHourMs?.let { hourLabel(it) } ?: "无")
            append(" 起")
        },
        valueColor = INK_MID,
    )
    // 体积这一行**只放短值**：第一版把"1.7 MB / 30 MB · 保留自 09-11 18:21"整串塞进值里，
    // 而 Kv 的值是不定宽的、标签是 weight(1f)，结果标签"原始文件"被顶出了屏幕外（真机截图可见）。
    // 长信息走下一行小字——与"单位不跟数值挤一行"是同一条教训。
    Kv(
        label = "原始文件",
        value = String.format(
            java.util.Locale.US,
            "%.1f MB / %d MB",
            snapshot.sampleBytes / 1048576f,
            snapshot.sampleLimitBytes / 1048576L,
        ),
        valueColor = INK_MID,
    )
    Text(
        text = buildString {
            append("占上限 %.1f%%".format(java.util.Locale.US, snapshot.sampleUsage * 100f))
            append(" · 现有样本保留自 ")
            append(snapshot.sampleEarliestMs?.let { minuteLabel(it) } ?: "无")
        },
        color = INK_LOW,
        fontSize = 7.sp,
    )
    if (snapshot.duplicateArchiveHours > 0) {
        // 重复整点不是错误（重启会把当前小时再落一次盘，读取时按整点去重），
        // 但它是"重启过几次"的旁证，写出来比藏着好
        Text(
            text = "有 ${snapshot.duplicateArchiveHours} 个整点被重复落盘（读取时已按整点去重）",
            color = INK_LOW,
            fontSize = 7.sp,
        )
    }
}

/** 整点时刻：归档覆盖的是整点，显示成"09-12 21:00"。 */
private fun hourLabel(ms: Long): String =
    java.text.SimpleDateFormat("MM-dd HH:00", java.util.Locale.US).format(java.util.Date(ms))

/**
 * 真实时刻（分钟级）。
 *
 * 样本时间戳不是整点：第一版的格式串把分钟写死成 `:00`，于是 18:21 的首个样本
 * 被显示成 18:00 —— 体检页显示"看起来精确的错数"比不显示更坏，因为它会让人按错的时刻去对账。
 */
private fun minuteLabel(ms: Long): String =
    java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.US).format(java.util.Date(ms))

/** 体检窗口：与启动时的归档修补窗口同宽（24 小时），两边看的是同一段。 */
private const val ARCHIVE_CHECK_WINDOW_MS = 24L * 60L * 60L * 1000L

@Composable
private fun Kv(
    label: String,
    value: String,
    /** 值的颜色：默认高档（数值/正文）。有告警语义时由调用方覆盖。 */
    valueColor: Color = INK_HIGH,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        // 标签占剩余空间、值取自身宽度并右对齐：固定比例的 Spacer 会把长值挤没
        Text(
            text = label,
            color = INK_MID,
            fontSize = 9.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 9.sp,
            maxLines = 1,
            textAlign = TextAlign.End,
        )
    }
}

private fun formatDuration(seconds: Long): String =
    "%d:%02d".format(seconds / 60, seconds % 60)
