package com.yisiyun.guanfeng.core

/**
 * 脉冲窗口该不该强制关闭。
 *
 * ## 为什么必须有这个函数（真机事故，2026-09-12）
 *
 * 体感传感器（心率/腕温）不是常开的：每 10 分钟开 20 秒，靠协程 `delay(20_000)` 关窗。
 * 但**手表熄屏空闲时整机会挂起，而 `delay`/`Handler` 走的是 uptime 时钟 —— 挂起期间不走**。
 * 于是"20 秒的窗口"在真机上会被拖长：`dumpsys batterystats` 里心率传感器
 * （`Sensor 21`）注册了 **7 小时 33 分 / 51 次 ≈ 平均每次 8.9 分钟**，
 * 而进程总共只跑了约 15.7 小时 —— PPG 亮了 **48%** 的时间，设计值是 **3.3%**。
 * 清醒时日志里每一对开/关都精确是 20.0 秒，所以光看日志根本看不出问题。
 *
 * 两道闸配合才闭合这个漏洞：
 *   1. **拿到数据就关**（进程醒着时的快路径，几秒内就关掉）；
 *   2. **按墙钟超时强关**（本函数，进程/整机挂起过之后的兜底）。
 *
 * 用 `System.currentTimeMillis()` 而不是 `elapsedRealtime` 也不是 uptime：
 * 墙钟在挂起期间**照常推进**，所以窗口开了 9 分钟以后，下一次采样循环一跑就能立刻发现并关掉。
 *
 * 参数为 [startMs] ≤ 0 时返回 false：那表示"窗口没开过"，不能误判成超时。
 */
fun pulseWindowExpired(startMs: Long, nowMs: Long, onMs: Long): Boolean =
    startMs > 0L && nowMs - startMs > onMs
