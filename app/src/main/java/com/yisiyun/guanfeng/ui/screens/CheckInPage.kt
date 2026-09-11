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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.data.CheckInLogger
import com.yisiyun.guanfeng.data.RecorderState
import kotlinx.coroutines.delay

/**
 * 第四屏 · 打卡：把此刻的不适与此刻的气压绑在一起。
 *
 * ## 症状标签的来历（不再拍脑袋）
 *
 * 上一版是我随手写的四项，没有依据。现在改为按**文献里的「气象敏感」症状群**选，
 * 来源：*Meteoropathy: a review on the current state of knowledge*（PMC10478667）——
 * 该综述列举气象高度敏感者的常见表现：头痛、头晕、睡眠障碍、
 * **颈肩部位的疼痛**、关节与肌肉疼痛、乏力、心悸与血压波动、情绪变化等；
 * 另有资料把它们归为脑型 / 心型 / 无力神经型 / 关节肌肉型等症候群。
 *
 * 本屏取六个高频且可在腕上快速辨认的代表项，每个都对应一个症候群：
 *   脑型：头痛、头晕
 *   关节肌肉型：颈肩、关节
 *   无力神经型：疲劳、睡眠差
 * （心型的心悸/气短留给「备注」自由输入；想改清单只需动下面这一行常量。）
 *
 * ## 交互
 * 标签、强度各选一次 → 按记录，正常路径两下手势完成；
 * 备注是可选路径（点输入框弹系统键盘），输入完点键盘自己的收起箭头再按记录。
 * 反馈直接占用标题行右侧，省一行高度——189×248 dp 上每一行都要算着用。
 */
private val SYMPTOM_TAGS = listOf("头痛", "头晕", "颈肩", "关节", "疲劳", "睡眠差")
private val INTENSITIES = listOf("轻", "中", "重")

private const val TAG = "GuanFengCheckIn"

@Composable
fun CheckInPage(state: RecorderState) {
    val context = LocalContext.current
    val logger = remember { CheckInLogger(context) }

    var selectedTag by remember { mutableStateOf(SYMPTOM_TAGS.first()) }
    var selectedIntensity by remember { mutableStateOf("中") }
    var note by remember { mutableStateOf("") }
    var todayCount by remember { mutableStateOf(logger.countToday()) }
    var feedback by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(feedback) {
        if (feedback != null) {
            delay(6_000)
            feedback = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("不适打卡", color = Color(0xFFF2C14E), fontSize = 11.sp)
            Spacer(Modifier.fillMaxWidth(0.1f))
            Text(
                text = feedback ?: "今日 $todayCount 次",
                color = if (feedback != null) Color(0xFF6EE7A8) else Color(0xFF7A7A7A),
                fontSize = 8.sp,
            )
        }

        // 症状：2×3，每个都对应一个文献症候群
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SYMPTOM_TAGS.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    pair.forEach { tag ->
                        Chip(
                            label = tag,
                            selected = selectedTag == tag,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedTag = tag
                                Log.i(TAG, "选中症状 $tag")
                            },
                        )
                    }
                }
            }
        }

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
                        selectedIntensity = level
                        Log.i(TAG, "选中强度 $level")
                    },
                )
            }
        }

        BasicTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .background(Color(0xFF191919), RoundedCornerShape(6.dp))
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(Color(0xFF2A5F8F), RoundedCornerShape(16.dp))
                .clickable {
                    Log.i(TAG, "打卡按钮被点击：$selectedTag · $selectedIntensity · note=「$note」")
                    val written = logger.append(
                        timestampMs = System.currentTimeMillis(),
                        tags = selectedTag,
                        intensity = selectedIntensity,
                        note = note,
                        pressureHpa = state.pressureHpa,
                        trend = state.trend,
                        heartRateBpm = state.heartRateBpm,
                        wristTemperatureC = state.wristTemperatureC,
                        lightLux = state.lightLux,
                    )
                    todayCount = logger.countToday()
                    feedback = if (written) {
                        "已记录 $selectedTag · $selectedIntensity"
                    } else {
                        "写入失败"
                    }
                    note = ""
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("记录", color = Color.White, fontSize = 12.sp)
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
