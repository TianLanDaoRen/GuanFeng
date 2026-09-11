package com.yisiyun.guanfeng.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.core.MarkdownLite

/**
 * 极简 Markdown 渲染。
 *
 * 接口返回的是模型原生输出（含 Markdown 符号）与服务端追加的标语帧，
 * 不处理就会在表盘上看到一排 `**` 与 `---`。这里按块渲染：
 * 标题加大、项目符号加「· 」、分隔线画一条细线、`**粗体**` 走 AnnotatedString。
 *
 * 刻意不引第三方渲染库：真正会出现的语法就这几种，而手表屏幕也放不下表格。
 */
@Composable
fun MarkdownText(
    raw: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFE0E0E0),
    baseSize: TextUnit = 9.sp,
    lineHeight: TextUnit = 13.sp,
) {
    val blocks = MarkdownLite.parse(raw)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownLite.Block.Heading -> Text(
                    text = inlineOf(block.text, color),
                    fontSize = (baseSize.value + (4 - block.level)).sp,
                    lineHeight = lineHeight,
                )

                is MarkdownLite.Block.Bullet -> Row(modifier = Modifier.fillMaxWidth()) {
                    Text("· ", color = color, fontSize = baseSize, lineHeight = lineHeight)
                    Text(
                        text = inlineOf(block.text, color),
                        fontSize = baseSize,
                        lineHeight = lineHeight,
                    )
                }

                is MarkdownLite.Block.Paragraph -> Text(
                    text = inlineOf(block.text, color),
                    fontSize = baseSize,
                    lineHeight = lineHeight,
                )

                MarkdownLite.Block.Rule -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .height(1.dp)
                        .background(Color(0xFF3A3A3A)),
                )
            }
        }
    }
}

/** `**粗体**` → 加粗且提亮的 span，其余保持正文色。 */
private fun inlineOf(text: String, color: Color): AnnotatedString = buildAnnotatedString {
    MarkdownLite.inline(text).forEach { segment ->
        withStyle(
            if (segment.bold) {
                SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)
            } else {
                SpanStyle(color = color)
            }
        ) {
            append(segment.text)
        }
    }
}
