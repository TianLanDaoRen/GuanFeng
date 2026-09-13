package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 净变标签必须跟着窗口长度走。
 *
 * 真机事故：通知里的 `observedDeltaHpa` 来自**速评引擎的 5 分钟窗口**，
 * 却写成"3 小时净变"，与观风页的 `ΔP(3h) 外推` 并列时自相矛盾（两个都对，标签错了）。
 */
class TrendLabelTest {

    @Test
    fun `满窗才叫三小时`() {
        assertEquals("3 小时净变", TrendLabel.deltaLabel(180f))
        assertEquals("3 小时净变", TrendLabel.deltaLabel(179.95f))
        assertEquals("3 小时净变", TrendLabel.deltaLabel(170f))
    }

    @Test
    fun `速评窗口老老实实写分钟`() {
        assertEquals("近 5 分钟净变", TrendLabel.deltaLabel(5f))
        assertEquals("近 60 分钟净变", TrendLabel.deltaLabel(60f))
        assertEquals("近 169 分钟净变", TrendLabel.deltaLabel(169f))
    }

    @Test
    fun `窗口缺失时也不能写成三小时`() {
        assertEquals("近 0 分钟净变", TrendLabel.deltaLabel(0f))
    }
}
