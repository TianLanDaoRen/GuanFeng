package com.yisiyun.guanfeng.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.data.PendingAlert
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.yisiyun.guanfeng.ui.theme.INK_LOW
import com.yisiyun.guanfeng.ui.theme.INK_HIGH

/**
 * 待确认提醒弹层。
 *
 * ## 为什么提醒最终落在应用内
 *
 * 真机实测把两条系统通道都堵死了：
 *   · 普通通知：手表 SystemUI 的 `canPost` 白名单**连提醒流程都拦掉**——
 *     发得出去、上不了屏、也不震动；
 *   · indicator：能出现在表盘顶部，但**只显示图标、显示不了文字**。
 *
 * 所以"发生了什么"只能由应用自己讲。流程是：
 * 事发时**震动**唤你注意 → 你切回应用时弹出这张卡片 → 点「我已知晓」才消失。
 *
 * 为什么不让它自己消失：主人明确要求"让用户自己点我已知晓，不然就一直显示"——
 * 提醒的价值在于被看到，自动消失等于没提醒。
 */
@Composable
fun AlertConfirmOverlay(
    alert: PendingAlert,
    onAcknowledge: () -> Unit,
) {
    val time = SimpleDateFormat("HH:mm", Locale.US).format(Date(alert.atMs))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("⚠", color = Color(0xFFF2C14E), fontSize = 14.sp)
            Spacer(Modifier.weight(0.05f))
            Text("天气提醒", color = Color(0xFFF2C14E), fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            Text(time, color = INK_LOW, fontSize = 8.sp)
        }
        Spacer(Modifier.height(6.dp))

        // 内容可滚动：窄屏上宁可滚也不能被裁（Material AlertDialog 就是在这里翻的车）
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Text(
                text = alert.title,
                color = INK_HIGH,
                fontSize = 13.sp,
                lineHeight = 17.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = alert.text,
                color = INK_HIGH,
                fontSize = 9.sp,
                lineHeight = 13.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "样本量小，仅供参考，非医学建议",
                color = INK_LOW,
                fontSize = 7.sp,
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .height(32.dp)
                    .background(Color(0xFF2A4A6F), RoundedCornerShape(16.dp))
                    .clickable { onAcknowledge() },
                contentAlignment = Alignment.Center,
            ) {
                Text("我已知晓", color = Color(0xFFD8E8FA), fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}
