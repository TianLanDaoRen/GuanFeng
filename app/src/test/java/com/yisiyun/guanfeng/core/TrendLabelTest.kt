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
    fun `页面显示实测值_而不是被门限压平后的 0`() {
        // 真机那一窗：最近 3 小时实测净变 −0.45 hPa（关联页曲线也在往下），
        // 而绝对量门限（0.5 hPa）把速率压成了 0，页面原本写"外推 +0.0 hPa"——
        // 会被读成"气压不变"。现在改显示实测值：判定保守可以，仪表必须显示真实读数。
        assertEquals(
            "−0.45f 在 %.1f 下是 −0.4（float 实际是 −0.44999998）",
            "ΔP(3h) 实测 -0.4 hPa",
            TrendLabel.pageDeltaLine(observedDeltaHpa = -0.45f, windowMinutes = 180f),
        )
        assertEquals(
            "+0.46 → +0.5，正号必须写出来",
            "ΔP(3h) 实测 +0.5 hPa",
            TrendLabel.pageDeltaLine(observedDeltaHpa = 0.46f, windowMinutes = 179.95f),
        )
        assertEquals(
            "窗口不满时不许拿实测值冒充 3 小时",
            "近 5 分钟净变 -0.3 hPa",
            TrendLabel.pageDeltaLine(observedDeltaHpa = -0.32f, windowMinutes = 5f),
        )
    }

    @Test
    fun `圆环指针用实测平均速率_且与页面那行自洽`() {
        // 真机那一窗：净变 −0.45 hPa / 3 小时 → 平均速率 −0.15 hPa/h，乘 3 回到 −0.45
        val rate = TrendLabel.averageRateHpaPerHour(observedDeltaHpa = -0.45f, windowMinutes = 180f)
        assertEquals(-0.15f, rate, 0.005f)
        assertEquals(-0.45f, rate * 3f, 0.01f)

        // 抗噪：静置窗口跨度只有 0.04 hPa 时，平均速率也只给 0.013 hPa/h，
        // 而最小二乘斜率在同一段能给出 −0.56 hPa/h（门限存在的理由）
        assertEquals(0.0133f, TrendLabel.averageRateHpaPerHour(0.04f, 180f), 0.001f)

        // 窗口缺失时不许给出速率
        assertEquals(0f, TrendLabel.averageRateHpaPerHour(1f, 0f), 0.0001f)
    }
}
