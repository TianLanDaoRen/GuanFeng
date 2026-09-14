package com.yisiyun.guanfeng.core

/**
 * 一场天气过程的状态。
 *
 * @param active 是否处于「气压在降 → 可能下雨」的过程之中。
 * @param dropHpa 本轮过程自最高点的累计降幅（≤0）。过程结束后保留最后一次的值，便于复盘。
 * @param startMs 本轮过程的起始时刻（首次越过进入阈值的时刻）。
 * @param minHpa 本轮过程的最低点，解除判定就是看从它回升了多少。
 * @param peakHpa 本轮过程的参照最高点。
 * @param clearedRiseHpa 若刚解除，这里是自最低点回升的幅度（>0）；否则为 null。
 */
data class WeatherEpisode(
    val active: Boolean,
    val dropHpa: Float,
    val startMs: Long,
    val minHpa: Float,
    val peakHpa: Float,
    val clearedRiseHpa: Float?,
) {
    val durationMs: Long get() = if (startMs <= 0L) 0L else System.currentTimeMillis() - startMs
}

/**
 * 天气过程状态机。
 *
 * ## 为什么不用滑动窗口（主人的方案胜出）
 *
 * 我原先的做法是"回看最近 6 小时的最大降幅"，那是个**补丁**：
 * 它只能回答"过去一段时间降了多少"，回答不了"现在是不是还在一场过程里"。
 * 漏洞很直白——**如果降完之后一直下着雨超过 6 小时，窗口一滑照样忘干净**，
 * 于是又开始报「平稳 · 无需带伞」。
 *
 * 主人的方案是把下雨当成**有开始有结束的过程**，用状态机记住它：
 *   · 进入：气压持续下降或快速下降，累计降幅越过 [enterDropHpa] → 「可能下雨」
 *   · 维持：只要没满足解除条件就一直维持，**与过了多久无关**
 *   · 解除：气压自本轮最低点**回升** [clearRiseHpa] → 说明低压已过、过程结束
 *     （两个门限取同量级，见下面迟滞一节的说明）
 *
 * 这也正是气象仪器的通行做法：风暴警报一旦挂上就锁存，直到气压回升才摘掉。
 *
 * ## 为什么要留迟滞（hysteresis），而不是一有变化就切状态
 *
 * 主人特别点到「雨时下时停」这个边界。雨带间歇时气压会小幅上下抖，
 * 若进入与解除用同一个门限，状态就会来回翻，提醒也会跟着刷屏。
 * 所以设计成**双门限**：
 *   · 进入要降 [enterDropHpa]（默认 1.5 hPa，明显高于小时级噪声约 0.1~0.3）；
 *   · 解除要自最低点回升 [clearRiseHpa]（默认 1.0 hPa，是"低压已过"的量级）。
 * 中间的灰色地带**保持原状态**——这正是迟滞的意义。
 */
class WeatherEpisodeTracker(
    /**
     * 进入门限：**3 小时净降 2.0 hPa**（2026-09-14 主人定 A 方案）。
     *
     * 旧实现是「自最高点累计降 1.5、不限时」—— 那个最高点永不遗忘
     * （`if (pressureHpa > peak) peak = pressureHpa`），于是一整天慢慢降 1.5 也能进过程。
     * 现在最高点只取最近 3 小时（与全 app 的 3 小时锚点一致），数值 2.0（形成迟滞）。
     */
    private val enterDropHpa: Float = 2.0f,

    /** 参照最高点的回溯窗口。 */
    private val peakWindowMs: Long = 3L * 60L * 60L * 1000L,

    /** 解除超时（D 方案）：连续这么久既没创新低、也没回升到解除线，就自动解除。 */
    private val clearTimeoutMs: Long = 12L * 60L * 60L * 1000L,
    /**
     * 解除所需的回升幅度。**与进入门限取同量级**，这是刻意的：
     *
     * 一开始我把解除设成 1.0 hPa（比进入小），测"雨时下时停"时立刻翻车——
     * 雨带间歇期气压本来就上下抖，±0.6 hPa 的抖动峰峰就是 1.2，
     * 照样越过 1.0 的门限，状态于是来回翻、提醒跟着刷屏。
     *
     * 两个门限都代表"天气尺度上的真实变化"（远高于传感器噪声 0.1~0.3、
     * 也高于雨带抖动 0.5~1.0），所以取同量级才自洽。中间那片灰色地带
     * **保持原状态**——这正是迟滞的意义。
     */
    private val clearRiseHpa: Float = 1.5f,
) {

    private var active = false
    private var peak = Float.NaN
    private var min = Float.NaN
    private var startMs = 0L
    private var lastDrop = 0f
    private var lastClearedRise: Float? = null
    private val winMs = ArrayDeque<Long>()
    private val winHpa = ArrayDeque<Float>()
    private var lastLowMs = 0L

    fun current(): WeatherEpisode = WeatherEpisode(
        active = active,
        dropHpa = lastDrop,
        startMs = startMs,
        minHpa = if (min.isNaN()) 0f else min,
        peakHpa = if (peak.isNaN()) 0f else peak,
        clearedRiseHpa = lastClearedRise,
    )

    /**
     * 从持久化状态恢复——**这是「锁存」能跨进程存活的唯一途径**。
     *
     * 为什么必须有：状态机原本只活在内存里。手表的可用内存很小
     * （实测 Free 最低只剩 24 MB），前台服务被系统回收是常态；
     * 每次重启后 [peak] 清空，下一帧样本就把当前气压当成参照最高点，
     * 于是"已经降了 3 hPa、雨正在下"这件事被彻底忘掉，重新回到「平稳」。
     * 这与状态机的立意（挂上就不摘、直到回升）直接矛盾，所以状态必须落盘。
     *
     * 用 [WeatherEpisode] 本身作为快照类型，不另造结构：它已经带齐了
     * active / peak / min / startMs 四个字段，再多一个类型只是重复。
     *
     * @param clearedRiseHpa 只用于复盘显示，恢复时保留即可，不参与判定。
     */
    fun restore(episode: WeatherEpisode) {
        // peak/min 为 0 说明是"从未跟踪过"的空状态（真实气压约 1000 hPa，不可能是 0）
        if (episode.peakHpa <= 0f || episode.minHpa <= 0f) {
            reset()
            return
        }
        active = episode.active
        peak = episode.peakHpa
        min = episode.minHpa
        startMs = episode.startMs
        lastDrop = episode.dropHpa
        lastClearedRise = episode.clearedRiseHpa
    }

    /** 喂入一个**天气分量**气压（已解耦高度）。 */
    fun add(timestampMs: Long, pressureHpa: Float): WeatherEpisode {
        // 3 小时回溯窗口：进新样本压入，超窗弹出。
        // 窗口空时**不能抛异常、也不能清参照点**：夜里睡眠模式或装包会让样本隔几小时
        // （实测最长 18831 秒），窗口必被弹空——此时沿用跨会话持久化的 peak。
        winMs.addLast(timestampMs); winHpa.addLast(pressureHpa)
        while (winMs.isNotEmpty() && timestampMs - winMs.first() > peakWindowMs) {
            winMs.removeFirst(); winHpa.removeFirst()
        }
        if (winHpa.isNotEmpty()) peak = winHpa.max()

        if (peak.isNaN()) {
            peak = pressureHpa
            min = pressureHpa
            startMs = timestampMs
            return current()
        }

        if (!active) {
            // 未进入过程：参照最高点只取最近 3 小时（窗口空则沿用持久化的 peak）
            if (peak.isNaN()) peak = pressureHpa
            min = peak
            val drop = pressureHpa - peak
            lastDrop = 0f
            if (drop <= -enterDropHpa) {
                active = true
                startMs = timestampMs
                lastClearedRise = null
                min = pressureHpa
                lastLowMs = timestampMs
                lastDrop = drop
            }
            return current()
        }

        // 过程之中：更新最低点；只看"自最低点回升了多少"，与时间无关
        if (pressureHpa < min) {
            min = pressureHpa
            lastLowMs = timestampMs
        }
        lastDrop = min - peak
        val rise = pressureHpa - min
        if (rise >= clearRiseHpa || timestampMs - lastLowMs >= clearTimeoutMs) {
            // 低压已过：解除，并把参照点重置到当前，准备迎接下一轮
            active = false
            lastClearedRise = if (rise >= clearRiseHpa) rise else null
            peak = pressureHpa
            min = pressureHpa
            startMs = timestampMs
            lastDrop = 0f
        }
        return current()
    }

    /** 换气/重启时重置（跨会话续接由调用方决定是否允许）。 */
    fun reset() {
        active = false
        peak = Float.NaN
        min = Float.NaN
        startMs = 0L
        lastDrop = 0f
        lastClearedRise = null
    }
}
