package com.yisiyun.guanfeng.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yisiyun.guanfeng.data.PressureRecorder
import com.yisiyun.guanfeng.ui.screens.BodyPage
import com.yisiyun.guanfeng.ui.screens.CheckInPage
import com.yisiyun.guanfeng.ui.screens.RecordPage
import com.yisiyun.guanfeng.ui.screens.WeatherPage

/**
 * 观风主界面：三屏左右滑动（观风 / 体感 / 记录），底部三点为翻页符。
 *
 * 按 OPPO 交互规范：非屏幕左边缘的左右滑动用于切换应用内功能界面，
 * 多页面时应给出翻页符提示；左边缘的右滑留给系统的返回手势（windowSwipeToDismiss）。
 */
private const val PAGE_COUNT = 4

@Composable
fun GuanFengApp() {
    val state by PressureRecorder.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        HorizontalPager(
            state = pagerState,
            // 底部留 20dp：翻页符是浮层，留不够会把各页最后一行小字遮住
            modifier = Modifier.fillMaxSize().padding(bottom = 20.dp),
        ) { page ->
            when (page) {
                0 -> WeatherPage(state)
                1 -> BodyPage(state)
                2 -> CheckInPage(state)
                else -> RecordPage(state)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(PAGE_COUNT) { index ->
                val active = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (active) 6.dp else 4.dp)
                        .clip(CircleShape)
                        .background(if (active) Color(0xFFB4B4B4) else Color(0xFF3A3A3A)),
                )
            }
        }
    }
}
