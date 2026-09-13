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
     * 观风页那行：**显示实测净变**，不显示被门限压平过的外推值。
     *
     * 为什么改（2026-09-13 主人指出）：引擎有一条绝对量门限（窗口净变 <0.5 hPa 不报方向，
     * 速率强制为 0），于是页面写"外推 +0.0 hPa" —— 可那一窗口实测是 −0.45 hPa，
     * 关联页曲线也在往下。**判定保守是对的，但仪表必须显示真实读数**，
     * 否则用户看到的是"气压不变"，而事实是"在降，只是幅度不足以支撑预报结论"。
     *
     * 满窗才叫 `ΔP(3h)`；窗口不满时老实写"近 N 分钟净变"（不拿短窗外推充数）。
     */
    fun pageDeltaLine(observedDeltaHpa: Float, windowMinutes: Float): String =
        if (windowMinutes >= FULL_WINDOW_MINUTES) {
            "ΔP(3h) 实测 %+.1f hPa".format(observedDeltaHpa)
        } else {
            "近 ${windowMinutes.toInt()} 分钟净变 %+.1f hPa".format(observedDeltaHpa)
        }

    /**
     * 圆环指针用的速率：**窗口内的平均速率** = 实测净变 ÷ 窗口小时数。
     *
     * 不用最小二乘斜率，也不用被门限压平过的 `rateHpaPerHour`：
     *   · 平均速率是**实测值**，不会像斜率那样被噪声放大（真机实测：静置 6 分钟、跨度仅 0.04 hPa，
     *     回归斜率却给出 −0.56 hPa/h —— 那正是门限存在的理由）；
     *   · 它乘 3 小时正好等于页面显示的 ΔP(3h)，两者自洽。
     * 判据（等级、提醒）仍然走门限，仪表与判定各司其职。
     */
    fun averageRateHpaPerHour(observedDeltaHpa: Float, windowMinutes: Float): Float =
        if (windowMinutes <= 1f) 0f else observedDeltaHpa / (windowMinutes / 60f)

    /** 窗口内的净变标签：满窗叫"3 小时净变"，否则老实说是"近 N 分钟净变"。 */
    fun deltaLabel(windowMinutes: Float): String =
        if (windowMinutes >= FULL_WINDOW_MINUTES) "3 小时净变" else "近 ${windowMinutes.toInt()} 分钟净变"
}
