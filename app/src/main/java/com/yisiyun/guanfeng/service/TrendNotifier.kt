package com.yisiyun.guanfeng.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.yisiyun.guanfeng.R
import com.yisiyun.guanfeng.data.PendingAlertStore
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

    /**
     * 同一等级的最短重复间隔。
     *
     * 主人要求：**风雨等级只要有变化就应该提醒**。所以判据从"只在升到高时提醒"
     * 改成"等级变了就提醒"，冷却只用来防止在两级之间来回抖动时刷屏。
     */
    private const val MIN_INTERVAL_MS = 10L * 60L * 1000L

    private var lastLevel: RainLikelihood? = null
    private var lastNotifiedAtMs = 0L

    /**
     * 直接驱动马达。
     *
     * 为什么不靠通知来震动：**手表的 SystemUI 有 canPost 白名单**，
     * 第三方应用的本地通知连提醒流程都不走——真机实测点下去毫无震动
     * （通知能进系统数据库、也留下了 mVibrateNotificationKey 记录，但马达没转）。
     * 系统侧可验证的证据是 `aidl_vibrator: Vibrator on for timeoutMs: 600`。
     */
    fun vibrateAlert(context: Context) {
        val audio = context.getSystemService(android.media.AudioManager::class.java)
        // 静音模式（既不出声也不震）下不打扰——尊重用户的设置
        if (audio?.ringerMode == android.media.AudioManager.RINGER_MODE_SILENT) return
        val vibrator = context.getSystemService(android.os.Vibrator::class.java) ?: return
        if (!vibrator.hasVibrator()) return
        // 三短一长，与普通通知的"两下"区分开：不看表也能分辨是天气提醒
        val pattern = longArrayOf(0, 350, 200, 350, 200, 600)
        runCatching {
            vibrator.vibrate(android.os.VibrationEffect.createWaveform(pattern, -1))
        }
    }

    /**
     * 测试提醒：完整走一遍真实提醒的两条腿——**震动 + 落一条待确认提醒**。
     *
     * 通知这条路已被系统白名单堵死（实测毫无震动也不上屏），
     * indicator 又只显示图标、显示不了文字，
     * 所以真正能把"是什么事"讲清楚的只有应用内的确认弹窗。
     */
    fun triggerTestAlert(context: Context) {
        vibrateAlert(context)
        PendingAlertStore.record(
            context = context,
            title = "测试提醒（手动触发）",
            text = "若你在应用内看到这条并点了「我已知晓」，说明整条提醒链路是通的。",
        )
    }

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.deleteNotificationChannel(LEGACY_CHANNEL_ID)
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "天气转坏提醒",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "气压变化导致风雨倾向改变时提醒"
                enableVibration(true)
            }
        )
    }

    /**
     * 每次算出趋势后调用。
     *
     * 触发条件：**倾向等级发生变化**（低↔中↔高之间任意跳变，含转好）。
     * 转好也提醒是有意的——"雨要停了"和"雨要来了"一样是用户想知道的信息。
     */
    fun maybeNotify(
        context: Context,
        likelihood: RainLikelihood,
        assessment: WeatherAssessment,
        trend: TrendResult?,
        nowMs: Long = System.currentTimeMillis(),
    ) {
        if (likelihood == RainLikelihood.UNKNOWN) return
        val previous = lastLevel
        if (likelihood == previous) return
        lastLevel = likelihood
        // 首次拿到结论时也提醒一次（此前是"未知"）
        if (nowMs - lastNotifiedAtMs < MIN_INTERVAL_MS) return
        lastNotifiedAtMs = nowMs

        val body = buildString {
            previous?.let { append("由「").append(it.label).append("」变为「").append(likelihood.label).append("」 · ") }
            append("3 小时净变 %.1f hPa".format(trend?.observedDeltaHpa ?: 0f))
            append(" · ").append(assessment.advice)
        }
        vibrateAlert(context)
        PendingAlertStore.record(context, "风雨倾向：${likelihood.label}", body, nowMs)
        AlertState.requestRefresh()
        post(context, "风雨倾向：${likelihood.label}", body)
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

    /** 供测试或状态重置使用。 */
    fun resetForTest() {
        lastLevel = null
        lastNotifiedAtMs = 0L
    }
}
