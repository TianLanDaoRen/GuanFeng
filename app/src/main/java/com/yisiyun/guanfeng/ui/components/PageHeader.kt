package com.yisiyun.guanfeng.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.ui.theme.INK_MID

/**
 * 每页顶部的「主标题 + 副标题」。
 *
 * ## 为什么抽出来
 *
 * 原先四页各写各的：字号 11/13/8.sp 混着用、副标题有的居中有的靠左、
 * 间隔一会儿是 Spacer(0.06f) 一会儿是 0.1f——同一个应用里四套标题版式，
 * 翻页时能明显看出"这不是一套东西"。主人 2026-09-12 要求统一，于是集中到这一处。
 *
 * **主标题的颜色仍由各页自定**（观风蓝 / 体感蓝 / 打卡橙 / 记录绿），那是页面的身份；
 * 统一的是**版式**：字号、间隔、副标题的位置与颜色。
 *
 * ## 副标题为什么吃剩余宽度
 *
 * 副标题用 `weight(1f)` 而不是固定间隔。这一页曾因为横向预算算错，
 * 把「生成 AI 报告」按钮顶出过屏幕——**让可变长度的那一半去承担挤压**，
 * 标题就永远不可能被挤掉，也不必再手算字数。万一还是不够，被省略的是副标题。
 */
@Composable
fun PageHeader(
    title: String,
    titleColor: Color,
    subtitle: String,
    subtitleColor: Color = SUBTITLE_INK,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, color = titleColor, fontSize = 11.sp, maxLines = 1)
        Spacer(Modifier.width(6.dp))
        Text(
            text = subtitle,
            color = subtitleColor,
            fontSize = 7.sp,
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

/** 副标题的默认色：**中档**（白字三档见 `ui/theme/Ink.kt`）。
 *
 * 它以前是 `#6E6E6E`——在手表上属于"看得见但读不清"，而副标题是要读的信息
 * （"取自 20:39:33"、"已采 1253 条"），不是装饰。
 */
val SUBTITLE_INK = INK_MID
