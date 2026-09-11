package com.yisiyun.guanfeng.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 一条等待用户确认的提醒。 */
data class PendingAlert(
    val title: String,
    val text: String,
    val atMs: Long,
)

/**
 * 待确认提醒。
 *
 * ## 为什么需要它（两条通道都被平台堵掉之后的唯一出路）
 *
 * 实测结论：手表 SystemUI 的 `canPost` 白名单把第三方本地通知**连提醒流程都拦掉**，
 * 而 indicator **只显示图标、不显示任何文字**。也就是说：
 *   · 通知——发得出去，上不了屏，也不震动；
 *   · indicator——能被看到，但只能表达"有这事"，表达不了"是什么事"。
 *
 * 剩下唯一能把信息讲清楚的通道，是**应用自己的界面**。所以提醒拆成两步：
 *   1. 事发时**震动**（实测有效，系统侧能看到马达记录）+ 落一条待确认提醒；
 *   2. 用户**下次切回应用**时弹出提醒，必须点「我已知晓」才消失。
 *
 * 用 SharedPreferences 而不是日志文件：这是一个"有没有/已读未读"的小状态，
 * 需要跨进程死亡存活，且读写要原子可靠。
 */
object PendingAlertStore {

    private const val PREFS = "guanfeng_pending_alert"
    private const val KEY_TITLE = "title"
    private const val KEY_TEXT = "text"
    private const val KEY_AT = "at"

    private val _signal = MutableStateFlow(0)

    /**
     * 界面订阅它：变化就重新读一次待确认提醒。
     * 两个触发点——提醒新产生，以及应用回到前台（由 MainActivity.onResume 调 [checkNow]）。
     */
    val signal: StateFlow<Int> = _signal.asStateFlow()

    /** 由 MainActivity.onResume 调用：把"回到前台了，去看看有没有提醒"广播出去。 */
    fun checkNow() {
        _signal.value += 1
    }

    fun record(context: Context, title: String, text: String, atMs: Long = System.currentTimeMillis()) {
        runCatching {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString(KEY_TITLE, title)
                .putString(KEY_TEXT, text)
                .putLong(KEY_AT, atMs)
                .apply()
        }
        _signal.value += 1
    }

    /** 读出一条未确认的提醒；没有就返回 null。 */
    fun load(context: Context): PendingAlert? = runCatching {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val title = prefs.getString(KEY_TITLE, null) ?: return null
        PendingAlert(
            title = title,
            text = prefs.getString(KEY_TEXT, "") ?: "",
            atMs = prefs.getLong(KEY_AT, 0L),
        )
    }.getOrNull()

    /** 用户点了「我已知晓」。 */
    fun acknowledge(context: Context) {
        runCatching {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
        }
        _signal.value += 1
    }
}
