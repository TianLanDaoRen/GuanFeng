package com.yisiyun.guanfeng.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import com.yisiyun.guanfeng.R

private val qweatherIconFamily = FontFamily(Font(R.font.qweather_icons))

/**
 * 官方天气图标。
 *
 * 字体图标的好处在这里体现得最直接：**一个 `color` 参数就能适配任何底色**。
 * 这个界面是深色的，而官方 SVG 是 `fill="currentColor"`（颜色由使用者定），
 * 两者天然对上——换成位图就得为每种底色各准备一套。
 *
 * 未知天气码显示一个中性符号而不是留空：留空会让人以为"这格没数据"，
 * 而实际是"这个码我们还没有对应图标"，是两回事。
 */
@Composable
fun WeatherIcon(
    code: String,
    tint: Color,
    fontSize: TextUnit,
) {
    Text(
        text = QweatherIcons.glyphFor(code) ?: "•",
        fontFamily = qweatherIconFamily,
        color = tint,
        fontSize = fontSize,
    )
}
