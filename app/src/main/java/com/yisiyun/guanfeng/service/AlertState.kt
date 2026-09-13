package com.yisiyun.guanfeng.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 提醒的显示状态。
 *
 * 存在的理由：提醒要走两条腿——**马达震动**（我们自己调）与 **indicator 文案**
 * （系统唯一允许第三方使用的可见通道）。indicator 由 [WatchSessionService]
 * 按行数分桶更新，最快也要几十秒才刷一次，而"测试提醒"需要立刻看到效果，
 * 所以用一个刷新信号把服务叫醒。
 */
object AlertState {

    private val _refresh = MutableStateFlow(0)

    /** 服务订阅它：一变就重建 indicator 通知。 */
    val refresh: StateFlow<Int> = _refresh.asStateFlow()

    @Volatile
    private var testUntilMs = 0L

    @Volatile
    private var testLabel: String? = null

    /**
     * 进入测试窗口：接下来 [windowMs] 内 indicator 显示测试文案。
     * 用来验证"提醒真的来了会是什么样"，而不必等一场真实的气压急降。
     */
    fun requestTest(windowMs: Long = 20_000L, label: String = "⚠ 可能转雨（测试）") {
        testUntilMs = System.currentTimeMillis() + windowMs
        testLabel = label
        _refresh.value += 1
    }

    /** 测试窗口内的文案；不在窗口内返回 null。 */
    fun activeTestLabel(nowMs: Long = System.currentTimeMillis()): String? =
        if (nowMs < testUntilMs) testLabel else null

    /** 预约的兑现时刻（0 = 没有预约）；界面线程写、采样循环线程读。 */
    @Volatile
    private var delayedTestAtMs = 0L

    /** 兑现日志里要写的延迟描述（如「30 分钟后」）；与 [delayedTestAtMs] 同生共死。 */
    @Volatile
    private var delayedTestNote: String? = null

    /**
     * **预约**一次延迟的测试提醒（默认 5 秒后），由**服务自己的循环**去兑现。
     *
     * ## 为什么不做成"界面里 delay(5s) 然后震动"
     *
     * 那样验不出任何东西：界面一离开（或进程被换掉），那个协程就没了，
     * 震不震只说明界面还在不在，说明不了**服务**能不能调起震动。
     * 而我们要回答的正是后者——"服务活着"（indicator 已证明）与
     * "服务能震动"是两件事，中间隔着一个未验证的假设。
     *
     * 所以预约存在这里、由 `PressureRecorder` 的采样循环（每 5 秒转一圈）兑现，
     * 走的是**与真实提醒完全相同的那条路**。
     *
     * [delayMs] 可以拉长（分钟级），用来验证"长时间之后还来不来"——
     * 5 秒与 30 分钟之间隔着睡眠模式、后台回收、indicator 换代一整条链路，
     * 量级不同就不能互相背书。预约时长由界面按钮（5 秒）或文件开关
     * （`files/test_alert_after_min`，见 [TestAlertSwitch]）给出。
     *
     * [note] 只是给兑现日志用的可读描述（如「30 分钟后」），不传就按时长自动生成。
     */
    fun requestDelayedTest(delayMs: Long = 5_000L, note: String? = null) {
        // 界面线程写、采样循环线程读，所以要 volatile：
        // 不加的话循环有可能一直看不到新值，表现成"预约了却永远不兑现"。
        delayedTestAtMs = System.currentTimeMillis() + delayMs
        delayedTestNote = note ?: describeDelay(delayMs)
        _refresh.value += 1
    }

    /**
     * 到点了就把它取走（只取一次）。返回可读的延迟描述；未到点或根本没预约返回 null。
     *
     * 返回描述而不是 Boolean：兑现那一刻要打日志，而"几分钟后"这个数只有预约时知道，
     * 事后回看日志必须能一眼分清这次验的是 5 秒还是 30 分钟。
     */
    fun consumeDelayedTest(nowMs: Long): String? {
        if (delayedTestAtMs == 0L || nowMs < delayedTestAtMs) return null
        delayedTestAtMs = 0L
        return delayedTestNote.also { delayedTestNote = null }
    }

    /**
     * 把延迟时长说成人话：整分钟说「N 分钟后」，其余说「N 秒后」。
     *
     * 纯函数（单测覆盖）。日志里的单位必须对得上量级——写成"5 秒后"，
     * 而实际预约的是 30 分钟，比不打日志更糟：它会让人拿着错的量级去对时间。
     */
    fun describeDelay(delayMs: Long): String =
        if (delayMs >= 60_000L && delayMs % 60_000L == 0L) "${delayMs / 60_000L} 分钟后"
        else "${delayMs / 1000L} 秒后"

    /** 真实提醒发生时刻意再刷新一次 indicator。 */
    fun requestRefresh() {
        _refresh.value += 1
    }
}
