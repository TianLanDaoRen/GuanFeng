package com.yisiyun.guanfeng.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * 预约/兑现的语义单测。
 *
 * 这条链路的成败判据是日志里那句「测试提醒（N 分钟后）已兑现」，
 * 所以这里要钉住两件事：**到点必须兑现、且只兑现一次**；
 * **日志里的时长必须等于预约的时长**（写成 5 秒而实际约了 30 分钟，
 * 比不打日志更糟——它会让人拿着错的量级去对时间）。
 */
class AlertStateTest {

    /** 每个用例先把上一次遗留的预约清干净：AlertState 是单例，状态跨用例存在。 */
    @Before
    fun drain() {
        AlertState.consumeDelayedTest(Long.MAX_VALUE)
    }

    @Test
    fun `未到点不兑现_到点兑现一次`() {
        AlertState.requestDelayedTest(60_000L)
        val now = System.currentTimeMillis()

        assertNull("没到点就不能兑现", AlertState.consumeDelayedTest(now))
        assertEquals("1 分钟后", AlertState.consumeDelayedTest(now + 61_000L))
        assertNull("只兑现一次", AlertState.consumeDelayedTest(now + 61_000L))
    }

    @Test
    fun `没预约时兑现返回 null`() {
        assertNull(AlertState.consumeDelayedTest(System.currentTimeMillis() + 86_400_000L))
    }

    @Test
    fun `兑现描述必须等于预约时长`() {
        AlertState.requestDelayedTest(2 * 60_000L)
        val fired = AlertState.consumeDelayedTest(System.currentTimeMillis() + 2 * 60_000L)
        assertEquals("2 分钟后", fired)
    }

    @Test
    fun `时长描述分档_整分钟说分钟_其余说秒`() {
        assertEquals("5 秒后", AlertState.describeDelay(5_000L))
        assertEquals("1 分钟后", AlertState.describeDelay(60_000L))
        assertEquals("2 分钟后", AlertState.describeDelay(120_000L))
        assertEquals("30 分钟后", AlertState.describeDelay(30 * 60_000L))
        // 非整分钟不四舍五入成"1 分钟后"：宁可说得笨一点，也不能说成一个错的量级
        assertEquals("90 秒后", AlertState.describeDelay(90_000L))
    }
}
