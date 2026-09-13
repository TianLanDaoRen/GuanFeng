package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 心率显示的"记住上一次有效值"规则。
 *
 * 守的是真机现象：主人一直戴着手表，界面的心率却时不时显示"—"。
 * 原因是脉冲窗口之间的无效读数（0 = 没在测）被直接写成了 null。
 */
class HeartRateDisplayTest {

    private val t0 = 1_789_000_000_000L

    @Test
    fun `有新鲜读数时用新鲜的`() {
        assertEquals(72f, HeartRateDisplay.pick(fresh = 72f, lastValid = 65f, lastValidMs = t0, nowMs = t0))
    }

    @Test
    fun `没有新鲜读数时显示上一次有效值`() {
        // 脉冲间隔 10 分钟：两次窗口之间必须还能看到上一次的值
        assertEquals(
            68f,
            HeartRateDisplay.pick(fresh = null, lastValid = 68f, lastValidMs = t0, nowMs = t0 + 9 * 60_000L),
        )
    }

    @Test
    fun `超过保质期就老实显示没有`() {
        assertNull(
            "30 分钟没拿到任何有效读数，说明确实没在测，不该继续显示旧值",
            HeartRateDisplay.pick(fresh = null, lastValid = 68f, lastValidMs = t0, nowMs = t0 + 31 * 60_000L),
        )
    }

    @Test
    fun `从没拿到过有效读数时没有值`() {
        assertNull(HeartRateDisplay.pick(fresh = null, lastValid = null, lastValidMs = 0L, nowMs = t0))
        assertNull(HeartRateDisplay.pick(fresh = null, lastValid = 68f, lastValidMs = 0L, nowMs = t0))
    }

    @Test
    fun `时钟回拨时不闪成没有`() {
        // 校正过系统时间时 age 会是负数；此时宁可显示旧值，也不要让界面闪一下"—"
        assertEquals(70f, HeartRateDisplay.pick(fresh = null, lastValid = 70f, lastValidMs = t0 + 60_000L, nowMs = t0))
    }
}
