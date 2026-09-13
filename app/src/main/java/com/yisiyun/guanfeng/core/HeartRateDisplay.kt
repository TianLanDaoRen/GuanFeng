package com.yisiyun.guanfeng.core

/**
 * 界面上该显示哪个心率。
 *
 * ## 为什么要"记住上一次有效读数"
 *
 * 心率是**脉冲式**采样的：每 10 分钟才开 20 秒（见 PressureRecorder 的脉冲调度），
 * 而传感器在"没锁住/刚开窗"时会持续上报 0（0 的含义是"没在测"，不是"心率是 0"）。
 * 原来的实现把这类无效读数直接写成 null，于是**两次脉冲之间的整段时间里，
 * 界面都显示"—"**——可主人一直戴在手上，心率明明有值。
 *
 * 所以规则是：
 *   · **新鲜的**有效读数优先；
 *   · 没有新鲜读数时，显示**最近一次有效值**，但只在 [HeartRateDisplay.ttlMs] 之内
 *     （脉冲间隔 10 分钟，取 30 分钟给三轮余量）——超过就说明确实没在测，宁可显示"—"；
 *   · **无效读数绝不进入统计**：静息基线、小时归档用的仍是新鲜值那条路，
 *     否则一个陈旧的数会被反复计入静息样本。
 */
object HeartRateDisplay {

    /** 记住多久。脉冲间隔 10 分钟，给三轮余量。 */
    const val DEFAULT_TTL_MS = 30L * 60L * 1000L

    /**
     * @param fresh 本次新鲜读数（已经过滤过无效值，无效时传 null）
     * @param lastValid 最近一次有效读数
     * @param lastValidMs 最近一次有效读数的时刻
     * @return 界面上该显示的值，没有可用值时为 null
     */
    fun pick(
        fresh: Float?,
        lastValid: Float?,
        lastValidMs: Long,
        nowMs: Long,
        ttlMs: Long = DEFAULT_TTL_MS,
    ): Float? {
        if (fresh != null) return fresh
        if (lastValid == null || lastValidMs <= 0L) return null
        val age = nowMs - lastValidMs
        // 时钟回拨（age < 0）时按"刚拿到"处理：宁可显示一个稍旧的值，也不要闪成"—"
        if (age in 0..ttlMs || age < 0) return lastValid
        return null
    }
}
