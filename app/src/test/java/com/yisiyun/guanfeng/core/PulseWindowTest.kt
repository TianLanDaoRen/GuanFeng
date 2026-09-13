package com.yisiyun.guanfeng.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 脉冲窗口的"墙钟超时"判据。
 *
 * 这条测试守的是一个**日志里看不出来**的真机事故：关窗原本挂在 `delay(20_000)` 上，
 * 而 `delay` 走 uptime 时钟 —— 手表熄屏空闲时整机挂起、uptime 不走，
 * 于是 20 秒的窗口被拖成平均 8.9 分钟（心率传感器注册 7h33m / 51 次，
 * PPG 亮了 48%，设计值 3.3%）。清醒时日志里每一对开/关都精确是 20.0 秒，
 * 所以看日志只会得出"一切正常"的错误结论。
 */
class PulseWindowTest {

    private val on = 20_000L
    private val t0 = 1_789_000_000_000L

    @Test
    fun `窗口内的正常时刻不关窗`() {
        assertFalse(pulseWindowExpired(t0, t0, on))
        assertFalse(pulseWindowExpired(t0, t0 + 5_000, on))
        assertFalse("正好 20 秒不算超时（边界留给正常收尾）", pulseWindowExpired(t0, t0 + on, on))
    }

    @Test
    fun `挂起过后按墙钟判超时`() {
        // 整机挂起了将近 9 分钟：这时间窗在 uptime 上可能只走了几秒
        assertTrue("挂起 8.9 分钟必须判超时", pulseWindowExpired(t0, t0 + 534_000, on))
        assertTrue(pulseWindowExpired(t0, t0 + on + 1, on))
    }

    @Test
    fun `窗口没开过时不能误判成超时`() {
        // startMs = 0 表示"没开过窗口"：若按 now - 0 > onMs 判断，任何时刻都会误判成超时，
        // 于是每秒都去注销一次（还会在日志里刷屏）
        assertFalse(pulseWindowExpired(0L, t0, on))
        assertFalse(pulseWindowExpired(-1L, t0, on))
    }
}
