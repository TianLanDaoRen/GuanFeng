package com.yisiyun.guanfeng.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.yisiyun.guanfeng.MainActivity
import com.yisiyun.guanfeng.R
import com.yisiyun.guanfeng.data.PressureRecorder
import com.yisiyun.guanfeng.data.RecorderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 观风的保活前台服务：负责在熄屏 / 退到后台后继续记录，并接入表盘顶部的 indicator。
 *
 * 为什么必须有它：实测教训——把手表从充电底座取下、自动熄屏后，后台进程很快被系统回收，
 * 记录因此断掉；而每次重新打开界面又会新建一个 CSV，把一个连续动作切成若干碎片会话。
 *
 * indicator 接入的三条件（官方要求缺一不可）：
 *   ① setOngoing(true)
 *   ② setCategory(...) 取官方定义好的字符串
 *   ③ extras 里带 show_heytap_indicator = true
 *
 * category 选 "workout"（运动）：官方只开放 导航 / 运动 / 媒体播放 三类，
 * 而本应用记录的核心内容正是垂直运动（爬楼、电梯）与体感的对应关系，
 * 归入运动类是名副其实的，不是硬凑。
 */
class WatchSessionService : Service() {

    private var notificationJob: Job? = null
    private var alertScope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "服务创建")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildIndicatorNotification(null)
        startForeground(NOTIFICATION_ID, notification)
        // 采集交给单例，界面只是订阅方；服务活着，会话就连续。
        PressureRecorder.start(applicationContext)
        startNotificationUpdates()
        Log.i(TAG, "已进入前台并挂上 indicator")
        // 提醒发生时要立刻刷新 indicator（默认按行数分桶最快要几十秒），
        // 所以订阅一个刷新信号，收到就重建通知。scope 存下来，销毁时取消，避免泄漏。
        alertScope?.cancel()
        alertScope = CoroutineScope(SupervisorJob() + Dispatchers.Default).also { scope ->
            scope.launch {
                AlertState.refresh.collect {
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.notify(
                        NOTIFICATION_ID,
                        buildIndicatorNotification(PressureRecorder.state.value),
                    )
                }
            }
        }
        // 进程若被系统回收，尝试自动重建，保证长期记录不轻易断档。
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "服务销毁，停止采集")
        notificationJob?.cancel()
        notificationJob = null
        alertScope?.cancel()
        alertScope = null
        PressureRecorder.stop()
        super.onDestroy()
    }

    /**
     * 周期性刷新通知。
     *
     * 为什么必须刷新：indicator 是系统在「通知投递/更新」时判定的，
     * 而我们的通知只在服务启动那一瞬间投递过一次——那时应用还在前台，
     * 系统没有理由给一个前台应用挂「后台运行中」的提示。
     * 按行数分桶每 30 秒更新一次，既让系统有机会在应用退到后台后重新判定，
     * 也顺带把「已记录多少行」变成用户可见的进度。
     */
    private fun startNotificationUpdates() {
        notificationJob?.cancel()
        notificationJob = CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            var lastBucket = -1L
            PressureRecorder.state.collect { state ->
                val bucket = state.loggedRows / 15L
                if (state.recording && bucket != lastBucket) {
                    lastBucket = bucket
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.notify(NOTIFICATION_ID, buildIndicatorNotification(state))
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "观风记录",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "记录气压趋势与体感数据时显示"
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun buildIndicatorNotification(state: RecorderState?): Notification {
        val tapIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val extras = Bundle().apply { putBoolean(EXTRA_SHOW_INDICATOR, true) }
        // 更正一个曾被我想当然的前提：**indicator 只显示图标，不显示任何文字**
        // （主人在真机上确认）。所以这里写进文案不是为了在表盘上读，
        // 而是让通知记录里留下"当时是什么状态"，便于事后核对；
        // 真正传递信息的是应用内的待确认提醒。
        val alert = alertText(state)
        val text = when {
            alert != null && state != null -> "$alert · 已记录 ${state.loggedRows} 行"
            state != null -> "已记录 ${state.loggedRows} 行 · 垂直位移 %+.0f 米"
                .format(state.trend?.elevationMeters ?: 0f)
            else -> "正在记录气压与体感"
        }

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_guanfeng)
            .setContentTitle("观风")
            .setContentText(text)
            .setContentIntent(tapIntent)
            .setOngoing(true)
            // 必须声明"只提醒一次"：这条通知为了维持 indicator 每 30 秒重建一次，
            // 若不加这个标志，系统会把它当成应用在反复打扰，进而把**同一个 uid**
            // 的其它提醒类通知一并静音——实测就是把"转坏提醒"打成了
            // "Muting recently noisy 0|com.yisiyun.guanfeng|2001"。
            // 一个持续性的状态通知本来也不该每次更新都响。
            .setOnlyAlertOnce(true)
            .addExtras(extras)
            .setCategory(CATEGORY_WORKOUT)
            .build()
    }

    /** 转坏时给 indicator 的提示语；正常时返回 null。 */
    private fun alertText(state: com.yisiyun.guanfeng.data.RecorderState?): String? {
        // 测试窗口优先：让主人能立刻看到"真的转坏时"indicator 长什么样
        AlertState.activeTestLabel()?.let { return it }
        val formal = state?.trend?.let { com.yisiyun.guanfeng.core.WeatherRule.assess(it, state.recentFallHpa, state.episode) }
        val fast = state?.trendFast?.let { com.yisiyun.guanfeng.core.WeatherRule.assess(it, state.recentFallHpa, state.episode) }
        val active = if (formal != null &&
            formal.likelihood != com.yisiyun.guanfeng.core.RainLikelihood.UNKNOWN
        ) formal else fast
        return when (active?.likelihood) {
            com.yisiyun.guanfeng.core.RainLikelihood.HIGH -> "⚠ 可能转雨"
            com.yisiyun.guanfeng.core.RainLikelihood.MEDIUM -> "天气转差"
            else -> null
        }
    }

    companion object {
        private const val TAG = "GuanFengService"
        private const val CHANNEL_ID = "guanfeng_recording"
        private const val NOTIFICATION_ID = 1001

        /** 官方 indicator 规范：extra 的 key 固定为这个字符串。 */
        private const val EXTRA_SHOW_INDICATOR = "show_heytap_indicator"

        /** 官方 indicator 规范：运动类 category 的字符串值。 */
        private const val CATEGORY_WORKOUT = "workout"

        fun start(context: Context) {
            val intent = Intent(context, WatchSessionService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
