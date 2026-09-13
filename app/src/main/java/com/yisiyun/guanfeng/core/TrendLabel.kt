package com.yisiyun.guanfeng.core

/**
 * 把"某个窗口内观测到的净变"写成不会误导的标签。
 *
 * ## 为什么要这个函数（2026-09-13 真机）
 *
 * 主人发现两处数字对不上：观风页写 `ΔP(3h) 外推 +0.0 hPa`，而启动后的通知里写
 * "3 小时净变 +0.3 hPa"。查下来是**两个不同的量**被贴了同一个标签：
 *
 * | 位置 | 表达式 | 含义 |
 * |---|---|---|
 * | 观风页 | `deltaHpaPer3h = 速率 × 3` | **外推**出来的 3 小时变化（标签写了"外推"，诚实） |
 * | 通知 | `observedDeltaHpa` | **窗口内实际观测到**的变化 |
 *
 * 更糟的是通知那个 trend 可能是**速评引擎（5 分钟窗口）**——正式引擎判 UNKNOWN 时就会用它，
 * 于是"5 分钟里的 0.3 hPa 抖动"被写成"3 小时净变 0.3 hPa"。5 分钟的抖动当然不是 0，
 * 而 3 小时的真实净变确实是 0 —— 两个都"对"，但标签错了就变成自相矛盾。
 *
 * 所以标签必须**由窗口长度决定**，不能写死"3 小时"。
 */
object TrendLabel {

    /** 窗口达到这个分钟数才敢叫"3 小时"（3 小时 = 180 分钟，留一点余量）。 */
    const val FULL_WINDOW_MINUTES = 170f

    /**
     * 观风页那行的说法。
     *
     * 被门限压平时**不能写"+0.0"** —— 那会被读成"气压不变"，而实际是"变了但不足以支撑方向"。
     * 这种情况直接说"变化不足 0.5 hPa"，与仪表盘上的"平稳"自洽。
     */
    fun pageDeltaLine(deltaHpaPer3h: Float, belowThreshold: Boolean, thresholdHpa: Float = 0.5f): String =
        if (belowThreshold) "ΔP(3h) 变化不足 ${thresholdHpa} hPa"
        else "ΔP(3h) 外推 %+.1f hPa".format(deltaHpaPer3h)

    /** 窗口内的净变标签：满窗叫"3 小时净变"，否则老实说是"近 N 分钟净变"。 */
    fun deltaLabel(windowMinutes: Float): String =
        if (windowMinutes >= FULL_WINDOW_MINUTES) "3 小时净变" else "近 ${windowMinutes.toInt()} 分钟净变"
}
