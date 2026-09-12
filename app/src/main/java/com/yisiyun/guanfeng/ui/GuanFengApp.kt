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
import com.yisiyun.guanfeng.data.QweatherClient
import com.yisiyun.guanfeng.data.WeatherCollector
import com.yisiyun.guanfeng.log.QweatherLogger
import com.yisiyun.guanfeng.ui.screens.ForecastPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
private const val PAGE_COUNT = 6

/**
 * 默认停在第 1 页（观风），而不是第 0 页。
 *
 * 原因是主人定的版面：**天气预报放在"负一屏"**（第 0 页），主屏仍是观风——
 * 戴着表一抬手要看到的是气压与风雨倾向，预报是"往右多看一眼"的东西。
 */
private const val DEFAULT_PAGE = 1

@Composable
fun GuanFengApp() {
    val state by PressureRecorder.state.collectAsState()
    val pagerState = rememberPagerState(initialPage = DEFAULT_PAGE, pageCount = { PAGE_COUNT })

    val context = LocalContext.current
    val alertSignal by PendingAlertStore.signal.collectAsState()
    // 提醒覆盖在最上层：它必须盖住翻页符与整页内容，否则又会重演"弹层看不见"
    var pendingAlert by remember { mutableStateOf<PendingAlert?>(null) }

    // 天气采集的一次性同意。**只在还没问过的时候弹**——
    // SharedPreferences 记一个布尔值就够，不需要为"问过一次了"这种状态上数据库。
    // 天气页显示的快照：只读已落盘的数据，不自己发请求（断网也能看）。
    // 一分钟刷新一次就够——数据本身 30 分钟才更新一轮。
    var snapshot by remember { mutableStateOf<QweatherLogger.Snapshot?>(null) }
    var forecastDays by remember { mutableStateOf<List<QweatherLogger.DayLine>>(emptyList()) }
    var airNow by remember { mutableStateOf<QweatherLogger.AirLine?>(null) }
    LaunchedEffect(Unit) {
        while (true) {
            val loaded = withContext(Dispatchers.IO) {
                Triple(
                    QweatherLogger.readLatestSnapshot(context),
                    QweatherLogger.readLatestDays(context),
                    QweatherLogger.readLatestAir(context),
                )
            }
            snapshot = loaded.first
            forecastDays = loaded.second
            airNow = loaded.third
            delay(60_000)
        }
    }

    // 天气采集的**设置流程**：说明 → 定位 → （失败时的）出路。
    // 已经同意过、也已经有坐标时，整个流程不再出现。见 ForecastSetupOverlay 的注释。
    // 状态机的顺序是主人定的：**先问同意 → 同意了才门控 Wi-Fi → 再走定位**。
    // 不同意就不同意了：什么都不弹，天气功能整条不存在。
    var setupStep by remember {
        mutableStateOf<WeatherSetupStep?>(
            when {
                !WeatherConsent.hasDecided(context) -> WeatherSetupStep.Consent
                !WeatherConsent.isGranted(context) -> null
                !SiteLocation.wifiEnabled(context) -> WeatherSetupStep.NeedWifi
                // 同意过、Wi-Fi 也开着，但还没有坐标（上次定位失败、或表格刚升级）→ 直接进定位
                !SiteLocation.hasRemembered(context) -> WeatherSetupStep.Locating(0)
                else -> null
            },
        )
    }
    val scope = rememberCoroutineScope()

    fun startLocating() {
        setupStep = WeatherSetupStep.Locating(0)
        scope.launch {
            // 卫星与高德并发，卫星优先（等满 30 秒）；都没成才退到 IP 推断
            val fix = SiteLocation.acquirePreferringGps(context)
            if (fix != null) {
                setupStep = null
                // **立刻采一次**。
                // 不这么做的话，用户刚开启天气功能会看到"还没有采到数据"，
                // 而真相是"主循环下一次采集要等最多 5 分钟"——那是个没必要让人等的时间。
                // 刚拿到坐标正是最该马上取数的时刻：此刻的天气对应此刻的位置。
                runCatching { WeatherCollector.collect(context) }
                return@launch
            }
            // GPS 没定上：先问一次网络推断（IP 定位），拿到就摆给使用者看，由他决定。
            // **不偷偷用**——这是这一版与最初那版"配置兜底坐标"的根本区别。
            val network = SiteLocation.networkFix(context)
            setupStep = if (network != null) {
                // 把坐标换成人类可读的城市名。查不到也不影响使用——坐标本身已经拿到了。
                val name = QweatherClient.fetchCityName(network.lat, network.lon)
                WeatherSetupStep.NetworkFound(city = name, accuracyKm = 10)
            } else {
                WeatherSetupStep.Failed(hasRemembered = SiteLocation.hasRemembered(context))
            }
        }
    }

    // 用户点「开启」的同一刻再要定位权限：先解释、后要权限，比一进应用就弹系统框清楚得多。
    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        // 给不给都往下走：没权限时 acquire 会立刻返回 null，落到失败界面让使用者看见原因，
        // 而不是停在一个转圈的哑巴界面上。
        startLocating()
    }

    // Wi-Fi 门是**实时的**：用户在系统面板里开完 Wi-Fi 回来，这里应当自动放行进入定位。
    // 只在门开着的时候轮询——一个本地属性读取，代价可忽略。
    //
    // **必须等 Wi-Fi 真正就绪，而不是开关一拨就走**：实测开关变成"开"的瞬间，
    // Wi-Fi 协议栈还没起来，高德立刻回 error 19「没有检查到SIM卡，并且关闭了WIFI开关」，
    // 于是两个来源全部失败、白白落到 IP 推断。所以要求它**连续为真三秒**才放行。
    val needWifi = setupStep is WeatherSetupStep.NeedWifi
    LaunchedEffect(needWifi) {
        if (needWifi) {
            var stableSeconds = 0
            while (true) {
                delay(1_000)
                stableSeconds = if (SiteLocation.wifiEnabled(context)) stableSeconds + 1 else 0
                if (stableSeconds >= 3) {
                    startLocating()
                    break
                }
            }
        }
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
                // 第 0 页是**负一屏**：预报。默认不落在这里，见 DEFAULT_PAGE
                0 -> ForecastPage(
                    snapshot = snapshot,
                    days = forecastDays,
                    air = airNow,
                    consentGranted = WeatherConsent.isGranted(context),
                    // 没同意也留一个入口，不耽误使用（主人要求）
                    onRequestConsent = { setupStep = WeatherSetupStep.Consent },
                )
                1 -> WeatherPage(state)
                2 -> BodyPage(state)
                3 -> CheckInPage(state)
                4 -> AssociationPage(state)
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
                onOpenWifi = { SiteLocation.openWifiPanel(context) },
                onExitApp = { context.findActivity()?.finish() },
                onAllow = {
                    WeatherConsent.grant(context)
                    // **同意之后必须先过 Wi-Fi 门**（主人定的顺序：先问同意，同意了才门控 Wi-Fi）。
                    // 这里不能直接去要权限：状态机只在首次组合时求值，那一刻还没同意，
                    // 走的是"同意"那一支——Wi-Fi 检查得在这里主动做一次。
                    if (!SiteLocation.wifiEnabled(context)) {
                        setupStep = WeatherSetupStep.NeedWifi
                    } else {
                        // FINE + COARSE 一起要：GPS 必须 FINE，Wi-Fi/网络定位用 COARSE。
                        locationPermission.launch(
                            arrayOf(
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            ),
                        )
                    }
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

/**
 * 从 Compose 的 LocalContext 拿到 Activity。
 *
 * Compose 给的往往是 ContextWrapper，直接 as Activity 会崩；必须一层层剥到真正的 Activity。
 * 用在"退出应用"上——主人的 Wi-Fi 门要求不打开 Wi-Fi 就退出，那是真的退出，不是返回上一页。
 */
private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
