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

    @Test
    fun `被门限压平时不许写 0_0`() {
        // 真机事故：最近 3 小时实测净变 −0.45 hPa（关联页曲线也在往下），
        // 但因为没到 0.5 hPa 的绝对量门限，速率被强制为 0，
        // 页面就写成了"ΔP(3h) 外推 +0.0 hPa" —— 会被读成"气压不变"。
        assertEquals(
            "ΔP(3h) 变化不足 0.5 hPa",
            TrendLabel.pageDeltaLine(deltaHpaPer3h = 0f, belowThreshold = true),
        )
        assertEquals(
            "达标时照常报外推值",
            "ΔP(3h) 外推 -1.2 hPa",
            TrendLabel.pageDeltaLine(deltaHpaPer3h = -1.2f, belowThreshold = false),
        )
        assertEquals(
            "正数要带正号",
            "ΔP(3h) 外推 +0.8 hPa",
            TrendLabel.pageDeltaLine(deltaHpaPer3h = 0.8f, belowThreshold = false),
        )
    }
}
