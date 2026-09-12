package com.yisiyun.guanfeng.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.yisiyun.guanfeng.data.PendingAlert
import com.yisiyun.guanfeng.data.PendingAlertStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.yisiyun.guanfeng.data.WeatherConsent
import com.yisiyun.guanfeng.ui.components.AlertConfirmOverlay
import com.yisiyun.guanfeng.data.SiteLocation
import com.yisiyun.guanfeng.ui.components.ForecastSetupOverlay
import com.yisiyun.guanfeng.ui.components.WeatherSetupStep
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import com.yisiyun.guanfeng.ui.screens.AssociationPage
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
private const val PAGE_COUNT = 5

@Composable
fun GuanFengApp() {
    val state by PressureRecorder.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })

    val context = LocalContext.current
    val alertSignal by PendingAlertStore.signal.collectAsState()
    // 提醒覆盖在最上层：它必须盖住翻页符与整页内容，否则又会重演"弹层看不见"
    var pendingAlert by remember { mutableStateOf<PendingAlert?>(null) }

    // 天气采集的一次性同意。**只在还没问过的时候弹**——
    // SharedPreferences 记一个布尔值就够，不需要为"问过一次了"这种状态上数据库。
    // 天气采集的**设置流程**：说明 → 定位 → （失败时的）出路。
    // 已经同意过、也已经有坐标时，整个流程不再出现。见 ForecastSetupOverlay 的注释。
    var setupStep by remember {
        mutableStateOf<WeatherSetupStep?>(
            when {
                !WeatherConsent.hasDecided(context) -> WeatherSetupStep.Consent
                // 同意过但还没有坐标（例如上次定位失败、或表格刚升级）→ 直接进定位
                WeatherConsent.isGranted(context) && !SiteLocation.hasRemembered(context) ->
                    WeatherSetupStep.Locating(0)
                else -> null
            },
        )
    }
    val scope = rememberCoroutineScope()

    fun startLocating() {
        setupStep = WeatherSetupStep.Locating(0)
        scope.launch {
            val fix = SiteLocation.acquire(context)
            setupStep = if (fix != null) {
                null
            } else {
                WeatherSetupStep.Failed(hasRemembered = SiteLocation.hasRemembered(context))
            }
        }
    }

    // 用户点「开启」的同一刻再要定位权限：先解释、后要权限，比一进应用就弹系统框清楚得多。
    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        // 给不给都往下走：没权限时 acquire 会立刻返回 null，落到失败界面让使用者看见原因，
        // 而不是停在一个转圈的哑巴界面上。
        startLocating()
    }

    // 定位中的秒数。**key 用布尔值**：状态本身每秒都在变，用状态做 key 会让协程每秒重启。
    val locating = setupStep is WeatherSetupStep.Locating
    LaunchedEffect(locating) {
        if (locating) {
            var seconds = 0
            while (true) {
                delay(1_000)
                seconds += 1
                setupStep = WeatherSetupStep.Locating(seconds)
            }
        }
    }
    LaunchedEffect(alertSignal) {
        // 信号有两个来源：提醒新产生、以及应用回到前台（MainActivity.onResume）
        pendingAlert = PendingAlertStore.load(context)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        HorizontalPager(
            state = pagerState,
            // 底部留 14dp：翻页符浮层只占底部 5–11dp，14dp 足够不遮挡且不浪费纵向空间
            // （曾用 20dp，结果把观风页与体感页各挤掉一行——余量要算着给，不能图省事）
            modifier = Modifier.fillMaxSize().padding(bottom = 14.dp),
        ) { page ->
            when (page) {
                0 -> WeatherPage(state)
                1 -> BodyPage(state)
                2 -> CheckInPage(state)
                3 -> AssociationPage(state)
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

        // 天气采集设置：整页覆盖，同样必须盖住翻页符
        setupStep?.let { step ->
            ForecastSetupOverlay(
                step = step,
                onAllow = {
                    WeatherConsent.grant(context)
                    locationPermission.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                },
                onDecline = {
                    WeatherConsent.decline(context)
                    setupStep = null
                },
                onCancelLocating = { setupStep = null },
                onRetry = { startLocating() },
                // 用上次记录的坐标：那条记录是**历史上真取到过**的，不是猜的
                onUseRemembered = { setupStep = null },
            )
        }

        // 待确认提醒：整页覆盖，点「我已知晓」才消失
        pendingAlert?.let { alert ->
            AlertConfirmOverlay(
                alert = alert,
                onAcknowledge = {
                    PendingAlertStore.acknowledge(context)
                    pendingAlert = null
                },
            )
        }
    }
}
