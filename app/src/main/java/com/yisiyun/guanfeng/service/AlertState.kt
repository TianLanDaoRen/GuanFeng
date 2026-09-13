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
     */
    private var delayedTestAtMs = 0L

    fun requestDelayedTest(delayMs: Long = 5_000L) {
        delayedTestAtMs = System.currentTimeMillis() + delayMs
        _refresh.value += 1
    }

    /** 到点了就把它取走（只取一次），未到点或已取走返回 false。 */
    fun consumeDelayedTest(nowMs: Long): Boolean {
        if (delayedTestAtMs == 0L || nowMs < delayedTestAtMs) return false
        delayedTestAtMs = 0L
        return true
    }

    fun activeTestLabel(nowMs: Long = System.currentTimeMillis()): String? =
        if (nowMs < testUntilMs) testLabel else null

    /** 真实提醒发生时刻意再刷新一次 indicator。 */
    fun requestRefresh() {
        _refresh.value += 1
    }
}
