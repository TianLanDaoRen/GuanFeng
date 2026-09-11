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

    /** 真实提醒发生时刻意再刷新一次 indicator。 */
    fun requestRefresh() {
        _refresh.value += 1
    }
}
