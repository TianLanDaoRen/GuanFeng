package com.yisiyun.guanfeng.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.yisiyun.guanfeng.R
import com.yisiyun.guanfeng.core.RainLikelihood
import com.yisiyun.guanfeng.core.TrendResult
import com.yisiyun.guanfeng.core.WeatherAssessment

/**
 * 转坏提醒。
 *
 * ## 为什么需要它
 *
 * 在这一步之前，整个应用是个**仪表盘**：你得主动抬腕、翻页、盯着看。
 * 而风雨预测的价值恰恰在"出门前就知道"——最需要它的场景（正要下楼）
 * 反而是你最不会去看表的时刻。所以必须由它主动开口。
 *
 * ## 声音与震动交给系统
 *
 * 主人的意见是对的：只发一条普通通知，提示音与震动由手表按用户自己的设置处理，
 * **应用不该自己去震**。所以这里用 IMPORTANCE_DEFAULT 的渠道，
 * 不自建震动模式、不调 Vibrator。
 *
 * ## 关于"通知是否依赖 indicator"
 *
 * 通知本身不依赖前台服务——任何应用随时可以 notify()。真正依赖 indicator 的是
 * **"知道该发通知"**：我们的触发条件是气压连续变差，而持续读传感器在 Android 8+
 * 必须持有前台服务。换句话说，indicator 保的不是通知，是"发现事件的能力"。
 *
 * ## 防打扰
 *
 * 只在倾向**升级到「高」**时提醒一次，并在 [MIN_INTERVAL_MS] 内不重复；
 * 等倾向回落到低之后重新武装。宁可漏报一次，也不要变成一整天叮叮响的噪音源——
 * 被关掉通知的提醒等于不存在。
 */
object TrendNotifier {

    /**
     * 渠道 id 带 v2：Android 的渠道重要度**创建后不可修改**，
     * 而首版用的是 IMPORTANCE_DEFAULT（没有横幅）。要让改动生效必须换 id 重建，
     * 同时删掉旧渠道，免得用户看到两个同名项。
     */
    private const val CHANNEL_ID = "guanfeng_trend_alert_v2"
    private const val LEGACY_CHANNEL_ID = "guanfeng_trend_alert"
    private const val NOTIFICATION_ID = 2001

    /** 两次提醒之间的最短间隔。 */
    private const val MIN_INTERVAL_MS = 2L * 60L * 60L * 1000L

    private var armed = true
    private var lastNotifiedAtMs = 0L

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.deleteNotificationChannel(LEGACY_CHANNEL_ID)
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "天气转坏提醒",
                // HIGH：这是需要用户当时就知道了的信息，应当以横幅弹出。
                // 声音与震动由系统按用户自己的设置处理，应用不自建震动模式。
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "气压快速下降、可能转雨时提醒一次"
                enableVibration(true)
            }
        )
    }

    /** 每次算出趋势后调用；内部自己判断该不该真的发。 */
    fun maybeNotify(
        context: Context,
        likelihood: RainLikelihood,
        assessment: WeatherAssessment,
        trend: TrendResult?,
        nowMs: Long = System.currentTimeMillis(),
    ) {
        // 回落到低之后重新武装，这样"降了又升、又降"能被提醒两次
        if (likelihood == RainLikelihood.LOW || likelihood == RainLikelihood.UNKNOWN) {
            armed = true
            return
        }
        if (!armed) return
        if (likelihood != RainLikelihood.HIGH) return
        if (nowMs - lastNotifiedAtMs < MIN_INTERVAL_MS) return

        val delta = trend?.deltaHpaPer3h ?: 0f
        val posted = post(
            context = context,
            title = "气压急降 · 可能转雨",
            text = buildString {
                append("3 小时变压 %+.1f hPa".format(delta))
                append(" · ").append(assessment.advice)
                if (trend?.confidence != null) append(" · ").append(assessment.shortReason)
            },
        )
        if (posted) {
            armed = false
            lastNotifiedAtMs = nowMs
        }
    }

    private fun post(context: Context, title: String, text: String): Boolean = runCatching {
        ensureChannel(context)
        val manager = context.getSystemService(NotificationManager::class.java) ?: return false
        // 点通知应当能打开应用——原先没有 contentIntent，点了没反应。
        val tapIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            android.content.Intent(context, com.yisiyun.guanfeng.MainActivity::class.java)
                .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                    android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP),
            android.app.PendingIntent.FLAG_IMMUTABLE or
                android.app.PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_guanfeng)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(tapIntent)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
        true
    }.getOrDefault(false)

    /**
     * 手动发一条测试通知。
     *
     * 存在的理由：真实触发条件（倾向升到「高」）可能要等好几天才遇到一次，
     * 而"通知在 ColorOS Watch 上到底会不会响/弹"必须尽早验证。
     * 这条走同一条渠道、同一套构建逻辑，只是内容标明是测试。
     */
    fun sendTestNotification(context: Context) {
        post(
            context = context,
            title = "测试提醒（手动触发）",
            text = "若你能看到这条通知，说明渠道与权限都正常",
        )
    }

    /** 供测试或状态重置使用。 */
    fun resetForTest() {
        armed = true
        lastNotifiedAtMs = 0L
    }
}
