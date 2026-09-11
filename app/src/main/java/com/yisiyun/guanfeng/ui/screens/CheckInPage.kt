package com.yisiyun.guanfeng.ui.screens

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
import android.util.Log
import com.yisiyun.guanfeng.data.CheckInLogger
import com.yisiyun.guanfeng.data.RecorderState

/**
 * 第四屏 · 打卡：把此刻的不适与此刻的气压绑在一起。
 *
 * 交互按手表来设计：部位与强度都是一次点击选中，最后按一次「记录」——
 * 正常路径两下手势完成，不逼着你在难受的时候去打字。
 * 备注是可选路径：需要时才点输入框弹系统键盘（整屏覆盖式），
 * 输入完点键盘的收起箭头再按「记录」。
 */
private val TAGS = listOf("头痛", "颈紧", "关节", "疲劳")
private val INTENSITIES = listOf("轻", "中", "重")

private const val TAG = "GuanFengCheckIn"

@Composable
fun CheckInPage(state: RecorderState) {
    val context = LocalContext.current
    val logger = remember { CheckInLogger(context) }

    var selectedTag by remember { mutableStateOf(TAGS.first()) }
    var selectedIntensity by remember { mutableStateOf("中") }
    var note by remember { mutableStateOf("") }
    var todayCount by remember { mutableStateOf(logger.countToday()) }
    var feedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("不适打卡", color = Color(0xFFF2C14E), fontSize = 11.sp)
            Spacer(Modifier.fillMaxWidth(0.08f))
            Text("今日 $todayCount 次", color = Color(0xFF7A7A7A), fontSize = 8.sp)
        }

        // 部位：2×2
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            TAGS.chunked(2).forEach { pair ->
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
                                Log.i(TAG, "选中部位 $tag")
                            },
                        )
                    }
                }
            }
        }

        // 强度
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

        // 备注（可选）
        BasicTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .background(Color(0xFF191919), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 5.dp),
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
                .height(34.dp)
                .background(Color(0xFF2A5F8F), RoundedCornerShape(17.dp))
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
                        "已记录：$selectedTag · $selectedIntensity"
                    } else {
                        "写入失败"
                    }
                    note = ""
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("记录", color = Color.White, fontSize = 12.sp)
        }

        Text(
            text = feedback ?: "附带当前气压 %.2f hPa 与 ΔP(3h) %+.2f hPa".format(
                state.pressureHpa ?: 0f,
                state.trend?.deltaHpaPer3h ?: 0f,
            ),
            color = if (feedback != null) Color(0xFF6EE7A8) else Color(0xFF6E6E6E),
            fontSize = 8.sp,
            lineHeight = 10.sp,
        )
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
            .height(32.dp)
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
