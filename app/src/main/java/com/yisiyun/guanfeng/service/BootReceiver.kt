package com.yisiyun.guanfeng.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 开机自启：把采集恢复起来。
 *
 * ## 它只解决一半问题（必须说清楚，否则会被当成"保活已搞定"）
 *
 * 手表的**睡眠模式会停掉一切应用**（主人 2026-09-11 告知），而且**不会自己回来**。
 * 这种"停"在 Android 里等价于 `force-stop`：系统会把包标记为 stopped，
 * **任何 `START_STICKY`、任何广播都不会再把它拉起来**，只能由用户手动点开一次。
 * 这是平台设计，不是缺陷，本类也救不了它。
 *
 * 本类能救的是另一半：**真正的系统重启**。真机 `dumpsys batterystats` 在 20 天里
 * 记到 `System starts: 30`，说明这块表是会重启的；而重启之后，
 * 在这之前应用里**没有任何入口会启动 [WatchSessionService]**（只有 MainActivity.onCreate 调）
 * ——也就是说重启一次，记录就断到主人想起来点开为止。
 *
 * ## 未验证
 *
 * ColorOS 可能默认禁止第三方应用自启（国产 ROM 常见），本接收器**是否真能收到广播、
 * 收到了能否拉起前台服务，都还没在真机上验过**。不要据它的存在就认为"重启也不怕"。
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != ACTION_QUICKBOOT &&
            action != ACTION_HTC_QUICKBOOT
        ) {
            return
        }
        Log.i(TAG, "收到 $action，尝试恢复采集")
        runCatching { WatchSessionService.start(context) }
            .onFailure { Log.w(TAG, "自启失败：$it") }
    }

    private companion object {
        const val TAG = "GuanFengBoot"

        /** 部分厂商的"快速开机"用私有 action，一并收下，收不到也无害。 */
        const val ACTION_QUICKBOOT = "android.intent.action.QUICKBOOT_POWERON"
        const val ACTION_HTC_QUICKBOOT = "com.htc.intent.action.QUICKBOOT_POWERON"
    }
}
