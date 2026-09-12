package com.yisiyun.guanfeng.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.core.CATEGORY_COMFORT
import com.yisiyun.guanfeng.core.CATEGORY_SYMPTOM
import com.yisiyun.guanfeng.data.CheckInLogger
import com.yisiyun.guanfeng.data.CheckInSignal
import com.yisiyun.guanfeng.data.RecorderState
import com.yisiyun.guanfeng.ui.components.PageHeader
import com.yisiyun.guanfeng.ui.components.SUBTITLE_GRAY
import kotlinx.coroutines.delay

/**
 * 第三屏 · 体感打卡：把此刻的体感与此刻的气压绑在一起。
 *
 * ## 症状标签的来历（不再拍脑袋）
 *
 * 上一版是我随手写的四项，没有依据。现在改为按**文献里的「气象敏感」症状群**选，
 * 来源：*Meteoropathy: a review on the current state of knowledge*（PMC10478667）——
 * 该综述列举气象高度敏感者的常见表现：头痛、头晕、睡眠障碍、
 * **颈肩部位的疼痛**、关节与肌肉疼痛、乏力、心悸与血压波动、情绪变化等；
 * 另有资料把它们归为脑型 / 心型 / 无力神经型 / 关节肌肉型等症候群。
 *
 * 2026-09-12 按主人要求调整（清单见下方 [CHECK_IN_TAGS]）：
 *   · 「头痛」与「头晕」合并成「**头部**」——都是头部的感受，分开记反而让人犹豫点哪个；
 *   · 新增「**舒适**」——这一页已从「不适打卡」改名「体感打卡」，
 *     就不能只让人记坏消息。**感觉良好也是有价值的数据**（天然的对照组）。
 *
 * ## 交互
 * 标签、强度各选一次 → 按记录，正常路径两下手势完成；
 * 备注是可选路径（点输入框弹系统键盘），输入完点键盘自己的收起箭头再按记录。
 * 反馈直接占用标题行右侧，省一行高度——189×248 dp 上每一行都要算着用。
 */

/**
 * 体感标签：2×3，仍是六个，**不占额外高度**——这一页每一行都是算着用的。
 *
 * 前五个是文献里的气象敏感症候群代表项（脑型 / 关节肌肉型 / 无力神经型），
 * 最后一个 [COMFORT_TAG] 是**非症状**的对照组。
 */
private val CHECK_IN_TAGS = listOf("头痛", "头晕", "颈肩", "关节", "疲劳", "睡眠差")

/**
 * 「舒适」侧的标签，与症状标签**一一镜像**（2026-09-12 主人定）。
 *
 * 镜像不是为了好看，是为了能问一个**双向**的问题：
 * 气压骤变时，头痛是不是多了 **而且** 头脑清爽是不是少了。
 * 单向计数只能说"扎堆"，双向同源变化才接近证据。
 *
 * 最后一格「心情平稳」补的是文献里的「情绪变化」这一维——症状表没覆盖它，舒适侧补上。
 */
private val COMFORT_TAGS =
    listOf("头脑清爽", "颈肩轻松", "关节舒展", "精神充足", "睡得好", "心情平稳")

private val INTENSITIES = listOf("轻", "中", "重")

private const val TAG = "GuanFengCheckIn"

@Composable
fun CheckInPage(state: RecorderState) {
    val context = LocalContext.current
    val logger = remember { CheckInLogger(context) }

    var category by remember { mutableStateOf(CATEGORY_SYMPTOM) }
    // 标签与强度都**默认不选**。原先默认选中第一个标签、强度默认「中」，
    // 于是"只按一下记录"会写进「头痛 · 中」——用户在替自己表态，这是静默错记。
    // 空值就是"没说"，与本项目"没有数据不能写 0"是同一条规矩。
    var selectedTag by remember { mutableStateOf("") }
    var selectedIntensity by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var todayCount by remember { mutableStateOf(logger.countToday()) }
    var feedback by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(feedback) {
        if (feedback != null) {
            delay(6_000)
            feedback = null
        }
    }

    val isComfort = category == CATEGORY_COMFORT
    val tags = if (isComfort) COMFORT_TAGS else CHECK_IN_TAGS

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // 版式统一走 PageHeader；主标题保留本页的橙色。
        // 反馈（"已记录…"）占用副标题位，只在它出现时变色——不额外占一行高度。
        PageHeader(
            title = "体感打卡",
            titleColor = Color(0xFFF2C14E),
            subtitle = feedback ?: "今日 $todayCount 次",
            subtitleColor = if (feedback != null) Color(0xFF6EE7A8) else SUBTITLE_GRAY,
        )

        // ── 第一级：类别（互斥，必选） ────────────────────────────────
        // 为什么要有这一维：自定义文本没法归类——"起床后右侧发紧"是症状，
        // "今天挺舒服"是对照，两者在文件里长得一模一样，混在一起就把分母污染了。
        CategoryToggle(
            category = category,
            onSelect = { picked ->
                category = picked
                // 换类别时清掉不适用的选择：两边的标签不通用，强度对舒适根本不适用
                selectedTag = ""
                selectedIntensity = ""
            },
        )

        // ── 中段：可滚动 ──────────────────────────────────────────────
        // 高度预算算下来刚好够（不适模式 213/222），但只差 9dp。
        // 我在这类预算上犯过两次错（按钮被顶出屏幕），所以加这道保险：
        // 内容真溢出时它自己滚，而不是把「记录」按钮挤没。
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // ── 第二级：标签（可选，3 列 × 2 行） ──────────────────────
            // 原来是 2 列 3 行；改成 3 列 2 行省下一行，正好抵掉新增的类别按钮行。
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                tags.chunked(3).forEach { trio ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        trio.forEach { tag ->
                            Chip(
                                label = tag,
                                selected = selectedTag == tag,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    // 再点一次取消选中：标签是可选项，要能反悔
                                    selectedTag = if (selectedTag == tag) "" else tag
                                },
                            )
                        }
                        // 最后一行不满 3 个时补空位，免得剩下的格子被拉宽
                        repeat(3 - trio.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            // ── 强度（可选，仅不适） ──────────────────────────────────
            if (!isComfort) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    INTENSITIES.forEach { level ->
                        Chip(
                            label = level,
                            selected = selectedIntensity == level,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedIntensity = if (selectedIntensity == level) "" else level
                            },
                        )
                    }
                }
            }

            // ── 备注（可选，两种类别都显示） ──────────────────────────
            // 不再充当标签：标签列只存预设标签，自由文本只进 note。
            // 原来"写备注＝顶替标签"，于是自定义内容混进 tags 列、再也分不出是不足还是舒适。
            BasicTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .background(
                        color = if (note.isNotBlank()) Color(0xFF1B2A38) else Color(0xFF191919),
                        shape = RoundedCornerShape(6.dp),
                    )
                    .border(
                        width = if (note.isNotBlank()) 1.dp else 0.dp,
                        color = if (note.isNotBlank()) Color(0xFF7FD1E8) else Color.Transparent,
                        shape = RoundedCornerShape(6.dp),
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                textStyle = TextStyle(color = Color.White, fontSize = 10.sp),
                singleLine = true,
                decorationBox = { inner ->
                    if (note.isEmpty()) {
                        Text("备注（可选）", color = Color(0xFF5E5E5E), fontSize = 9.sp)
                    }
                    inner()
                },
            )
        }

        // ── 记录按钮：固定在底部，翻转类别时它不会跟着上下跳 ──────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(Color(0xFF2A5F8F), RoundedCornerShape(16.dp))
                .clickable {
                    val written = logger.append(
                        timestampMs = System.currentTimeMillis(),
                        tags = selectedTag,
                        intensity = if (isComfort) "" else selectedIntensity,
                        note = note,
                        pressureHpa = state.pressureHpa,
                        trend = state.trend,
                        heartRateBpm = state.heartRateBpm,
                        wristTemperatureC = state.wristTemperatureC,
                        lightLux = state.lightLux,
                        weatherPressureHpa = state.weatherPressureHpa,
                        category = category,
                    )
                    todayCount = logger.countToday()
                    feedback = if (written) {
                        // 广播出去：关联视图要立刻把新点画上，不必等缓存过期
                        CheckInSignal.notifyRecorded()
                        "已记录 " + buildString {
                            append(if (isComfort) "舒适" else "不适")
                            if (selectedTag.isNotBlank()) append(" · ").append(selectedTag)
                            if (!isComfort && selectedIntensity.isNotBlank()) {
                                append(" · ").append(selectedIntensity)
                            }
                        }
                    } else {
                        "写入失败"
                    }
                    Log.i(TAG, "打卡：$category · tag=「$selectedTag」 · 强度=「$selectedIntensity」")
                    // 记完清掉这次的选择，但**保留类别**——连续记同类时不必每次重选
                    selectedTag = ""
                    selectedIntensity = ""
                    note = ""
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("记录", color = Color.White, fontSize = 12.sp)
        }
    }
}

/**
 * 类别互斥按钮组：整块一个圆角框、中间一条分隔线，选中的那半实心填充。
 *
 * 两半用**各自的语义色**（不适＝琥珀，舒适＝青绿）而不是统一一个强调色：
 * 类别本身带语义，一眼看到底色就知道此刻记的是哪一类，比读字快。
 */
@Composable
private fun CategoryToggle(
    category: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        CATEGORY_SYMPTOM to ("不适" to Color(0xFFF2C14E)),
        CATEGORY_COMFORT to ("舒适" to Color(0xFF6EE7A8)),
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
    ) {
        items.forEachIndexed { index, (value, labelAndColor) ->
            val (label, accent) = labelAndColor
            val selected = category == value
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (selected) accent else Color.Transparent)
                    .clickable { onSelect(value) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (selected) Color(0xFF141414) else Color(0xFF8A8A8A),
                    fontSize = 11.sp,
                )
            }
            if (index == 0) {
                Box(Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF2A2A2A)))
            }
        }
    }
}

@Composable
private fun Chip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .background(
                color = if (selected) Color(0xFF26405C) else Color(0xFF191919),
                shape = RoundedCornerShape(8.dp),
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected) Color(0xFF5FA8E8) else Color(0xFF2A2A2A),
                ),
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color(0xFFDCEBFA) else Color(0xFFB4B4B4),
            fontSize = 11.sp,
        )
    }
}
