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
        // 进程若被系统回收，尝试自动重建，保证长期记录不轻易断档。
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "服务销毁，停止采集")
        notificationJob?.cancel()
        notificationJob = null
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
        val text = state?.let {
            "已记录 ${it.loggedRows} 行 · 垂直位移 %+.0f 米".format(it.trend?.elevationMeters ?: 0f)
        } ?: "正在记录气压与体感"

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_guanfeng)
            .setContentTitle("观风")
            .setContentText(text)
            .setContentIntent(tapIntent)
            .setOngoing(true)
            .addExtras(extras)
            .setCategory(CATEGORY_WORKOUT)
            .build()
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
