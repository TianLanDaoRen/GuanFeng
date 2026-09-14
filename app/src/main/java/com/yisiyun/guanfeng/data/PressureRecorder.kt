package com.yisiyun.guanfeng.data

import android.content.Context
import java.io.File
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.yisiyun.guanfeng.core.Barometric
import com.yisiyun.guanfeng.core.SensorRatePlan
import com.yisiyun.guanfeng.core.HeartRateDisplay
import com.yisiyun.guanfeng.core.HourAccumulator
import com.yisiyun.guanfeng.core.backfillCarriedOver
import com.yisiyun.guanfeng.core.missingHourBuckets
import com.yisiyun.guanfeng.core.WeatherEpisode
import com.yisiyun.guanfeng.core.WeatherEpisodeTracker
import com.yisiyun.guanfeng.core.WeatherRule
import com.yisiyun.guanfeng.core.HourlyRow
import com.yisiyun.guanfeng.core.PressureSample
import com.yisiyun.guanfeng.core.pulseWindowExpired
import com.yisiyun.guanfeng.core.RainLikelihood
import com.yisiyun.guanfeng.core.PressureTrendEngine
import com.yisiyun.guanfeng.core.SampleAggregator
import com.yisiyun.guanfeng.core.TrendResult
import com.yisiyun.guanfeng.log.CsvSessionLogger
import com.yisiyun.guanfeng.log.csvNum
import com.yisiyun.guanfeng.log.HourlyArchive
import com.yisiyun.guanfeng.log.PowerLogger
import com.yisiyun.guanfeng.service.AlertState
import com.yisiyun.guanfeng.service.TrendNotifier
import com.yisiyun.guanfeng.log.SessionHistory
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 采集器对界面暴露的全部状态。界面只读它，不再自己接传感器。 */
data class RecorderState(
    val recording: Boolean = false,
    val pressureHpa: Float? = null,
    /**
     * 解耦掉高度之后的「天气分量」气压。
     * 长视图、打卡快照都必须用它——用原始读数会把坐电梯的 9 hPa 算成一次天气剧变。
     */
    val weatherPressureHpa: Float? = null,
    /** 正式趋势（3 小时窗口）。 */
    val trend: TrendResult? = null,
    /** 速评趋势（5 分钟窗口）：3 小时窗口还没铺满时先给一个结论。 */
    val trendFast: TrendResult? = null,
    /** 是否处于静止（无步数且加速度低）。健身中不做体感提示，见 CorroborationEngine。 */
    val isResting: Boolean = true,
    /**
     * 最近 [RECENT_FALL_WINDOW_MS] 内「天气分量」的累计净降幅（≤0）。
     * 用于记住"曾经降过"，避免气压降完转平后就被判成「无需带伞」。
     */
    val recentFallHpa: Float = 0f,
    /** 天气过程状态：进入「可能下雨」后一直挂着，直到气压自最低点明显回升。 */
    val episode: WeatherEpisode? = null,
    /** 传感器此刻的瞬时心率（`TYPE_HEART_RATE` 直接给的值，不是静息心率）。 */
    val heartRateBpm: Float? = null,
    /**
     * 静息基线：只采「没有垂直运动、没有步数」的静止样本，取其低分位数。
     * 这才是「静息心率」该有的来历——瞬时值直接当静息心率是错的。
     */
    val restingHeartRateBpm: Float? = null,
    val wristTemperatureC: Float? = null,
    /** 腕温的会话基线（慢速滑动均值）。用于看"偏离了多少"，而不是绝对温度。 */
    val wristTemperatureBaselineC: Float? = null,
    val lightLux: Float? = null,
    /** 环境光在 10 分钟尺度上的变化（lux）。用于将来分析它与天气的关系，现在只记录。 */
    val lightDelta10Min: Float? = null,
    val loggedRows: Int = 0,
    val elapsedSeconds: Long = 0,
    val logFileName: String = "",
    val logHealthy: Boolean = true,
    /** 启动时从历史 CSO 续接回来的样本数：>0 说明 3 小时窗口没从零开始。 */
    val restoredSamples: Int = 0,
    /** 系统步数传感器的累计读数（用于诊断步态是否可靠）。 */
    val stepPulses: Int = 0,
    /**
     * **跨窗口累计**的高度偏移（米），正数表示净上升。
     *
     * 这才是"累计垂直位移"该显示的量：它就是每一轮解耦真正减掉的那个值
     * （[elevationOffsetHpa] 换算成米），所以它与落盘状态、与 `weather_pressure_hpa` 列
     * 是同一把尺子（持久化文件里 `elevation_offset_hpa = -0.101` → 这里约 +0.84 米）。
     *
     * **不要用 [TrendResult.elevationMeters] 显示"累计"**：那个是引擎在**当前 3 小时窗口内**
     * 累加出来的局部量，同一段电梯在窗口里进进出出就会让它从 +70 米翻到 −70 米
     * ——2026-09-12 夜里就是这么把主人吓着的（详见 [TrendResult.elevationMeters] 的说明）。
     */
    val elevationOffsetMeters: Float = 0f,
)

/**
 * 气压与体感的采集核心。
 *
 * 设计要点：
 *  1. **采集逻辑不放在界面里**。放 Activity/Compose 里，界面一被回收记录就断，
 *     而且每次重开都会新建一个 CSV，把一个连续动作切成碎片会话。
 *  2. **两个时间尺度**：界面按 2 秒刷新实时读数；样本按 5 秒聚合落盘并喂给引擎。
 *     3 小时窗口下若按 2 秒落点，一天 4 万多行且相邻差异几乎全是噪声。
 *  3. **启动时从历史续接**：否则服务重启后 3 小时窗口要从零攒，等一小时才有可信读数。
 */
object PressureRecorder {

    private const val TAG = "GuanFengRecorder"

    /** 界面实时读数刷新节奏。 */
    private const val LIVE_TICK_MS = 2_000L

    /**
     * 样本聚合与落盘节奏：5 秒一个样本。
     *
     * 这个值是在两个方向上折中出来的：
     *   · 太密（原为 2 秒）：3 小时窗口下一天 4 万多行，且相邻差异几乎全是噪声；
     *   · 太疏（曾试 15 秒）：一趟 45 秒的电梯只剩 3 个样本，轨迹分辨率掉一个量级——
     *     而真机验证过的电梯/楼梯行为（−70.5 米、往返闭合 0.4 米）是在 2 秒采样下测的，
     *     不该在没重新验证的情况下把分辨率砍到 1/7。
     * 5 秒下：3 小时 = 2160 个样本、一天约 1.7 万行（~2 MB），电梯约 9 个样本。
     * 每个样本内部聚合约 40 个原始读数，噪声仍被中位数完全压掉。
     */
    private const val SAMPLE_INTERVAL_MS = 5_000L

    /** 正式趋势窗口。噪声在 3 小时内只折算约 0.013 hPa/h，远低于 0.5 hPa/h 的判定边界。 */
    private const val WINDOW_MS = 3L * 60L * 60L * 1000L

    private const val MIN_SAMPLES = 20

    /**
     * 速评引擎：5 分钟窗口。
     *
     * 主人的判断是对的——「3 小时窗口 + 覆盖率 30%」意味着要等约 54 分钟才有结论，
     * 那是矫枉过正。5 分钟数据确实容易误判，但误判的代价小、等待的代价大。
     * 因此做成两层：速评先给结论，正式窗口成熟后覆盖它。
     */
    private const val FAST_WINDOW_MS = 5L * 60L * 1000L
    private const val FAST_MIN_SAMPLES = 10

    /** 速评窗口短，绝对量门限必须按比例缩小，否则 5 分钟内永远达不到 0.5 hPa 而恒判「平稳」。 */
    private const val FAST_MIN_ABSOLUTE_DELTA_HPA = 0.15f

    /** 竖直积分的时间常数（秒）：足够长到能积累一次爬楼，又短到能抑制漂移。 */
    private const val VERTICAL_TAU_S = 3f

    /** 累计降幅的观察窗：足够长到能记住一场天气过程，又不至于记住上一天的旧账。 */
    private const val RECENT_FALL_WINDOW_MS = 6L * 60L * 60L * 1000L

    /** 少于这么多个样本（约 10 分钟）就不算累计降幅，避免刚启动时报出假降幅。 */
    private const val RECENT_FALL_MIN_SAMPLES = 120

    /** 跨会话状态的落盘节奏（不必每条样本都写盘）。 */
    private const val PERSIST_INTERVAL_MS = 5L * 60L * 1000L

    /**
     * 归档修补的回看窗口。
     *
     * 24 小时足够覆盖"昨晚睡着时丢的那个整点"，也与样本文件尾部 2 MB（约一天）匹配。
     * 再往前的缺失就没有修复价值了——而且样本文件本身也只留约两周。
     */
    private const val REPAIR_WINDOW_MS = 24L * 60L * 60L * 1000L

    /**
     * 核心传感器的请求周期：5 Hz。
     *
     * 气压必须连续采（5 秒聚合节奏就是它撑起来的），竖直运动证据同理——
     * 一趟 45 秒的电梯只有约 9 个 5 秒样本，再降就分辨不出来了。
     */
    private const val CORE_SAMPLING_US = 200_000

    /**
     * 光照的请求周期：5 秒一次。
     *
     * 10 分钟趋势只需要首尾两点，5 Hz 是 250 倍过采样。
     * 注意真机上该传感器 minRate = 0.60 Hz，HAL 会把这里钳到 0.6 Hz。
     */
    private const val LIGHT_SAMPLING_US = 5_000_000

    // ── 原始流黑匣子（实验用；不在界面上留任何按钮，用 adb 建/删开关文件控制）────────────────
    /** 开关：这个文件存在才记录。 */
    private const val RAW_LOG_FLAG = "raw_log_on"
    /** 输出：原始 5Hz 流（气压 / 重力 / 线加速度），格式 timestamp_ns,sensor_type,v0,v1,v2 */
    private const val RAW_LOG_FILE = "raw_sensors.csv"
    /**
     * 上限 200MB ≈ 4 天（实测 15 条/秒 → 约 0.6KB/s = 2.1MB/h；气压行还带传感器温度）。
     *
     * 为什么要有上限：手表存储 20GB 根本不在乎这点体积（一天 47MB，一年写入约 17GB），
     * 上限只为三件事——别让忘了删的开关无限增长、拉取分析要快、以及**别和电量测量混在一起**
     * （开了原始记录之后功耗账就不干净了）。到顶即停，不轮转、不覆盖。
     */
    private const val RAW_LOG_MAX_BYTES = 200L * 1024L * 1024L

    /** 体感传感器（心率/腕温）的脉冲窗口长度。20 秒足够 PPG 锁定并给出十几次读数。 */
    private const val PULSE_ON_MS = 20_000L

    /** 体感传感器的脉冲间隔。10 分钟一次：腕温是分钟级慢变量，静息基线也只取低分位数。 */
    private const val PULSE_INTERVAL_MS = 10L * 60L * 1000L

    /**
     * 脉冲窗口里"取了心率之后，再等腕温多久就收工"。
     *
     * 为什么不干脆等满 [PULSE_ON_MS]：那是**定时器**，而进程随时可能被冻结或被杀，
     * 定时器就再也不会响（见 [maybeClosePulseWindow] 的真机账）。
     */
    private const val PULSE_TEMP_GRACE_MS = 5_000L

    /**
     * 墙钟强关门限：比 [PULSE_ON_MS] 多给 10 秒宽限。
     *
     * 为什么不直接取 20 秒：正常收尾是那条 `delay(20s)`，两者会在同一秒前后脚到。
     * 门限贴着 20 秒的话，**每一轮都会**由强关先出手，于是每轮多出一条"超时强关"日志——
     * 报警变成噪声，真正出问题那一次反而淹掉了。给 10 秒宽限，正常路径先赢；
     * 只有真挂起过（uptime 停摆、20 秒拖成几分钟）才会走到强关。
     */
    private const val PULSE_FORCE_CLOSE_MS = PULSE_ON_MS + 10_000L

    /**
     * 硬件 FIFO 批量延迟：让事件先攒在**器件侧**的 FIFO 里，攒够或到点再一次性投递。
     *
     * 取 1 秒是个权衡（不是随手写的数）：
     *   · 现在 4 路 5 Hz 的注册全是 `batching_period = 0`，也就是**每秒 20 次事件投递**；
     *     1 秒批量把它压到 20 个事件凑成一批。
     *   · 为什么不用 5 秒（正好等于落盘节奏）：高度解耦要把"同一时刻"的**气压**与
     *     **竖直位移**配对，两路都批量到 5 秒后，两者的时间差最大可到 5 秒——
     *     电梯里气压 2.8 hPa/分，5 秒就是 0.23 hPa 的错配。1 秒把这个错配压到 0.05 hPa。
     *
     * 关键前提：**批量不会动到物理**。竖直积分用的是 `event.timestamp`（事件自带的时间戳，
     * 穿过 FIFO 依然保留），不是回调到达的墙钟——见 [integrateVertical]。
     * 不支持批量的器件（AOSP 合成的 Linear Acceleration 的 maxDelay = 0）会被框架钳回 0，
     * 只是白传一个参数，不会有副作用。
     */
    private const val BATCH_LATENCY_US = 1_000_000

    /**
     * 低于这个值的心率读数一律视为"没在测"。
     *
     * 生理上不可能有人静息心率低于 20，所以这个门限没有误杀风险；
     * 而 PPG 在脱腕/未锁定时**会稳定上报 0**（真机实测），
     * 不做处理就会以"0 bpm"的形态进入小时归档与 AI 报告。
     */
    private const val MIN_VALID_HEART_RATE_BPM = 20f

    /**
     * 恢复历史时：超过这个间隔视为断档，断档之前的数据一律不接
     * （跨空洞拟合会造出假趋势 —— 这是硬原则，只是"多长的空洞算空洞"要选对）。
     *
     * 2026-09-13 从 5 分钟放宽到 10 分钟。原因是真机上撞到的：
     * 装一次 APK（或系统更新、应用被系统停一段）会产生 **7~9 分钟**的空档，
     * 5 分钟的门限会把它当成断档 → **整个 3 小时窗口只剩断档之后的几十秒**，
     * 界面上表现为"窗口又没了"，而实际上历史数据一条没少。
     * 8 分钟的空洞放在 3 小时窗口里，两侧各自有近一小时的真实数据，
     * 拟合不会因此偏掉；而"丢掉整个窗口"的代价要大得多。
     */
    private const val MAX_GAP_MS = 10L * 60L * 1000L

    /** 会话文件保留天数已由「保留最近 N 个文件」取代，见 SessionHistory.pruneOldSessions。 */

    /** 内存里保留的样本上限（4 小时容量，比窗口多留一档余量）。 */
    private val MAX_SAMPLES = (4 * 60 * 60 * 1000L / SAMPLE_INTERVAL_MS).toInt()

    /** 腕温是厂商自定义传感器，其 16 个通道的语义没有公开文档，这里取首通道并如实标注未标定。 */
    private const val TYPE_WRIST_TEMPERATURE = 69815

    private const val LIGHT_TREND_WINDOW_MS = 10 * 60 * 1000L
    private const val LIGHT_TREND_MIN_SPAN_MS = 5 * 60 * 1000L

    private val _state = MutableStateFlow(RecorderState())
    val state: StateFlow<RecorderState> = _state.asStateFlow()

    private var scope: CoroutineScope? = null
    private var liveJob: Job? = null
    private var sampleJob: Job? = null
    /** 体感传感器的脉冲调度协程（见 PULSE_INTERVAL_MS 的说明）。 */
    private var pulseJob: Job? = null

    /** 天气采集任务。与判定链路完全无关的旁路，独立协程。 */
    private var weatherJob: Job? = null
    /** 需要脉冲式开关的传感器（心率、腕温）。 */
    private var pulseSensors: List<Sensor> = emptyList()

    /** 脉冲窗口是否开着（传感器回调线程读，关窗时写）。 */
    @Volatile private var pulseWindowOpen = false
    private var pulseWindowStartedMs = 0L
    @Volatile private var pulseHrSeen = false
    @Volatile private var pulseTempSeen = false
    private var sensorManager: SensorManager? = null
    private var logger: CsvSessionLogger? = null
    /** 供采样子循环写回跨会话状态用（sampleLoop 里拿不到 start() 的局部变量）。 */
    private var appContext: Context? = null
    private var startedAtMs = 0L
    private var started = false

    private val engineFormal = PressureTrendEngine(windowMs = WINDOW_MS, minSamples = MIN_SAMPLES)
    private val engineFast = PressureTrendEngine(
        windowMs = FAST_WINDOW_MS,
        minSamples = FAST_MIN_SAMPLES,
        minAbsoluteDeltaHpa = FAST_MIN_ABSOLUTE_DELTA_HPA,
    )
    private val aggregator = SampleAggregator()

    /** 原始流排队区：传感器回调只入内存，刷盘交给采样循环（回调线程不做 I/O）。 */
    /** 当前核心采样周期（两档切换，见 SensorRatePlan）。 */
    private var currentRateUs = SensorRatePlan.LOW_US

    /** 高速档保持到什么时候（0 = 现在是低速档）。 */
    private var fastUntilMs = 0L

    private val rawLogQueue = ArrayDeque<String>(8192)
    private var rawLogWriter: java.io.BufferedWriter? = null
    private var rawLogEnabled = false
    private var rawLogBytes = 0L
    /** 开关文件的检查时刻：每秒 15 个事件，不能每次都去 stat 文件。 */
    private var rawLogFlagCheckedMs = 0L
    private val samples = ArrayList<PressureSample>(MAX_SAMPLES + 1)

    /** 累计的高度偏移（hPa）：所有被引擎判为高度事件的步进之和，跨会话持久化。 */
    private var elevationOffsetHpa = 0f
    private var lastPersistMs = 0L

    /** 竖直积分的中间量（只在传感器回调里更新）。 */
    private var verticalVelocity = 0f
    private var verticalDisplacement = 0f
    private var lastAccelNs = 0L

    /** 腕温的慢速滑动均值：腕温绝对值没有天气含义（环境与衣袖混淆最大），只能与自身基线比较。 */
    private var wristTempBaseline: Float? = null
    private var wristTempReadings = 0

    /** 小时归档累加器：整点切换时把上一小时落盘（长期历史靠它，原始文件可以放心裁剪）。 */
    private val hourAccumulator = HourAccumulator()

    /** 天气分量的近期轨迹（只留 [RECENT_FALL_WINDOW_MS]），用于算累计降幅。 */
    private val weatherTrace = ArrayList<Pair<Long, Float>>(MAX_SAMPLES)

    /** 最近 [RECENT_FALL_WINDOW_MS] 内的累计净降幅。 */
    private var recentFallHpa = 0f

    /** 天气过程状态机（主人提出的模型，取代了原先的 6 小时滑动窗口）。 */
    private val episodeTracker = WeatherEpisodeTracker()

    // 传感器原始读数
    private var latestPressure: Float? = null
    private var heartRate: Float? = null

    /**
     * 最近一次**有效**心率与它的时刻：只服务界面显示（见 core/HeartRateDisplay）。
     *
     * 真机现象：主人一直戴着，心率却时不时显示"—"。原因是脉冲窗口之间的无效读数
     * （0 = 没在测）被直接写成 null。统计口径不变——静息基线/归档仍只用新鲜值。
     */
    private var lastValidHeartRate: Float? = null
    private var lastValidHeartRateMs = 0L
    private var wristTemperature: Float? = null
    private var lightLux: Float? = null
    private val gravity = FloatArray(3)

    // 静息候选：只在没有垂直运动、没有步数的静止样本上采心率
    private val restingCandidates = ArrayList<Float>()
    private var restingBaseline: Float? = null

    // 环境光慢趋势（10 分钟尺度），目前只记录不改判定——没有真实降水标注前不下结论
    private val lightHistory = ArrayDeque<Pair<Long, Float>>()
    private var lightDelta10Min: Float? = null

    @Synchronized
    fun start(context: Context) {
        if (started) {
            Log.i(TAG, "已在采集中，忽略重复启动")
            return
        }
        val applicationContext = context.applicationContext
        val manager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager = manager
        appContext = applicationContext
        logger = CsvSessionLogger(applicationContext)
        samples.clear()
        // 档位复位：每次启动都从低速档开始（与回放器的建模一致）
        currentRateUs = SensorRatePlan.LOW_US
        fastUntilMs = 0L
        latestPressure = null
        heartRate = null
        wristTemperature = null
        lightLux = null
        restingCandidates.clear()
        restingBaseline = null
        lightHistory.clear()
        lightDelta10Min = null
        startedAtMs = System.currentTimeMillis()
        started = true
        _state.value = RecorderState(recording = true, logFileName = logger?.displayName ?: "")

        // 历史续接是磁盘 I/O，放到协程里做，别阻塞主线程
        val newScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        scope = newScope
        newScope.launch {
            // 【顺序很重要】先做归档迁移，再裁剪原始文件。
            // 反过来的话，首次运行新版会先把最旧的文件删掉，
            // 那些数据就永远进不了小时归档——等于静默丢失历史。
            if (HourlyArchive.loadAll(applicationContext).isEmpty()) {
                val seeded = HourlyArchive.seed(
                    applicationContext,
                    SessionHistory.loadHourlyRollup(
                        applicationContext,
                        SessionHistory.ALL_HISTORY_DAYS,
                        startedAtMs,
                        // 只迁移带天气分量的行：老数据的原始气压含高度变化，
                        // 混进归档会污染"气压大变化日"统计（宁可少一段历史）
                        onlyDecoupled = true,
                    ),
                )
                if (seeded > 0) Log.i(TAG, "小时归档迁移：从原始文件补齐 $seeded 个小时")
            }

            // 【归档修补】把"采样文件里有、归档里整点缺失"的小时补回来。
            //
            // 起因是 2026-09-12 的真机实证：睡眠模式在 02:59 把应用整个停掉，
            // 而 02:00 那个整点还差一分钟才到点，于是它在 hourly.csv 里整行消失，
            // 可样本文件里那 59 分钟是完整的。backfillCarriedOver 补不到它——
            // 那只覆盖"当前小时"，而这次重启已过去 5.23 小时，
            // 且 loadRecent 的"断档 > 5 分钟即停"把恢复样本清空了。
            //
            // 后果是**每晚都会丢一个整点**（睡着的那一小时），而归档是永不删除的长期存储，
            // 样本文件只留约两周——今天不补，两周后就真没了。所以放在启动时、只跑一次。
            val repairBuckets = runCatching {
                SessionHistory.loadTailHourlyBuckets(
                    context = applicationContext,
                    nowMs = startedAtMs,
                    withinMs = REPAIR_WINDOW_MS,
                )
            }.getOrElse { error ->
                Log.w(TAG, "读取归档修补数据失败: $error")
                emptyList()
            }
            val repairTargets = missingHourBuckets(
                existingHourStarts = HourlyArchive.loadAll(applicationContext)
                    .map { it.hourStartMs }.toHashSet(),
                buckets = repairBuckets,
                currentHourStartMs = (startedAtMs / 3_600_000L) * 3_600_000L,
                earliestHourStartMs = startedAtMs - REPAIR_WINDOW_MS,
            )
            val repaired = HourlyArchive.appendMissing(applicationContext, repairTargets)
            if (repaired > 0) {
                Log.i(TAG, "小时归档修补：补回 $repaired 个缺失的整点（只有气压，无体感）")
            }

            val restored = runCatching {
                // 单一文件 + 30MB 上限之后不再需要"删旧文件"：
                // 采样文件自己会在超限时紧凑化（丢最旧、留最新），
                // 而小时归档永不删除，长期趋势另有归属。
                SessionHistory.loadRecent(applicationContext, startedAtMs, WINDOW_MS, MAX_GAP_MS)
            }.getOrElse { error ->
                Log.w(TAG, "读取历史样本失败: $error")
                emptyList()
            }
            samples.addAll(restored)
            Log.i(TAG, "从历史续接 ${restored.size} 个样本")

            // 跨会话状态：静息基线 + 累计高度偏移（后者决定天气气压序列的连续性）
            val persisted = SessionHistory.loadRecorderState(applicationContext)
            elevationOffsetHpa = persisted.elevationOffsetHpa
            restingBaseline = persisted.restingHeartRateBpm
            persisted.restingHeartRateBpm?.let { restingCandidates += it }
            Log.i(TAG, "恢复状态：静息基线=${persisted.restingHeartRateBpm} 高度偏移=${persisted.elevationOffsetHpa}")

            // 天气过程锁存：**这才是「挂上就不摘」能扛住进程被回收的关键**。
            // 没有它，重启后第一帧样本就把当前气压当参照点，一场正在下的雨会被忘成「平稳」。
            // 过时判定：停机超过 RECENT_FALL_WINDOW_MS（6 小时）的旧账不恢复——
            // 隔夜再打开应用时，昨天那场雨不该继续算数。
            persisted.episode?.let { saved ->
                val ageMs = System.currentTimeMillis() - persisted.episodeWrittenMs
                if (persisted.episodeWrittenMs > 0L && ageMs <= RECENT_FALL_WINDOW_MS) {
                    episodeTracker.restore(saved)
                    Log.i(
                        TAG,
                        "恢复天气过程：active=${saved.active} 降幅=${csvNum(saved.dropHpa, 2)} " +
                            "快照年龄=${ageMs / 60000} 分钟",
                    )
                } else {
                    Log.i(TAG, "天气过程快照已过时（${ageMs / 60000} 分钟），丢弃")
                }
            }

            // 【小时归档的补齐】把"本次启动前、且属于当前这个小时"的样本先喂进累加器。
            // 完整理由与取舍写在 backfillCarriedOver 的注释里（含真机实证的 545/720）。
            val currentHourStartMs = (startedAtMs / 3_600_000L) * 3_600_000L
            val backfill = hourAccumulator.backfillCarriedOver(
                samples = restored,
                currentHourStartMs = currentHourStartMs,
                elevationOffsetHpa = elevationOffsetHpa,
            )
            backfill.completedRows.forEach { completed ->
                appContext?.let { HourlyArchive.append(it, completed) }
            }
            if (backfill.fedSamples > 0) {
                Log.i(TAG, "小时归档补齐：启动前本小时已有 ${backfill.fedSamples} 个样本")
            }

            // 光照趋势回填：读最近 10 分钟的历史读数，趋势立刻可用（不必再等 10 分钟）
            val since = startedAtMs - LIGHT_TREND_WINDOW_MS
            val seededLight = SessionHistory.loadRecentLight(applicationContext, since, startedAtMs)
            seededLight.forEach { (timestamp, lux) -> lightHistory.addLast(timestamp to lux) }
            seededLight.lastOrNull()?.let { lightLux = it.second }
            Log.i(TAG, "回填光照读数 ${seededLight.size} 个")

            registerSensors(manager)

            _state.value = _state.value.copy(restoredSamples = restored.size)
            liveJob = launch { liveLoop() }
            sampleJob = launch { sampleLoop() }
            Log.i(TAG, "采集已启动，日志文件 ${logger?.path}")
        }
    }

    @Synchronized
    fun stop() {
        runCatching { rawLogWriter?.flush(); rawLogWriter?.close() }
        rawLogWriter = null
        rawLogQueue.clear()
        if (!started) return
        // 脉冲窗口可能正好开着：先显式注销，否则 PPG 会一直亮到进程被杀
        sensorManager?.let { unregisterPulse(it) }
        pulseJob?.cancel()
        pulseJob = null
        weatherJob?.cancel()
        weatherJob = null
        liveJob?.cancel()
        sampleJob?.cancel()
        scope = null
        liveJob = null
        sampleJob = null
        sensorManager = null
        started = false
        // 停机前把当前这个不完整的小时也归档：否则每次重启都会丢掉最后一段，
        // 而那些正是用户刚刚经历的时间。
        flushCurrentHour()
        _state.value = _state.value.copy(recording = false)
        Log.i(TAG, "采集已停止，本次共落盘 ${logger?.rowCount ?: 0} 行")
    }

    /** 把当前未完成的小时写进归档（停机时调用）。 */
    @Synchronized
    private fun flushCurrentHour() {
        val row = hourAccumulator.snapshot() ?: return
        appContext?.let { HourlyArchive.append(it, row) }
    }

    /**
     * 当前尚未落盘的那个小时的归档行。
     * 关联视图与 AI 报告要用它补上"最后一小时"——归档只在整点切换时追加，
     * 不补的话最近一小时永远是空的。
     */
    fun currentHourRow(): HourlyRow? = hourAccumulator.snapshot()

    /**
     * 重置累计高度基准（一键）。
     *
     * 为什么需要它：累计偏移是持久化的，一旦被误判污染（例如 2026-09-11
     * 那次屋里活动被攒出 +10 米假位移），它会**一直留在磁盘上**。
     * 恒定偏移不影响"变化量"，所以对齐度没有危害；但绝对值显示会一直偏，
     * 而且下一次真实爬楼会从这个错误基准继续累加。给一个显式复位入口，
     * 比让它默默烂在那里好。
     */
    @Synchronized
    fun resetElevationBaseline() {
        elevationOffsetHpa = 0f
        verticalVelocity = 0f
        verticalDisplacement = 0f
        appContext?.let { context ->
            SessionHistory.saveRecorderState(
                context = context,
                restingHeartRateBpm = restingBaseline,
                elevationOffsetHpa = 0f,
                // 必须带上当前过程状态：这个方法只重置高度基准，
                // 不该顺手把「正在下雨」的锁存一起抹掉
                episode = episodeTracker.current(),
            )
        }
        _state.value = _state.value.copy(weatherPressureHpa = _state.value.pressureHpa)
        Log.i(TAG, "高度基准已重置")
    }

    fun isRunning(): Boolean = started

    /**
     * 竖直方向的带泄漏二次积分，用来估**净位移**。
     *
     * 为什么要它：判断"人是否真的在垂直运动"不能看加速度峰值——
     * 挥一下手就能到 10 m/s² 以上，比爬楼还大；而位移是持续与否的差别：
     * 往复运动互相抵消、净位移趋近 0，爬楼/电梯则稳定累积。
     *
     * 带泄漏（时间常数 [VERTICAL_TAU_S] 秒）是为了抑制积分漂移：
     * 目的是"有没有持续位移"，不是精确轨迹，所以宁可让久远的历史衰减掉。
     */
    private fun integrateVertical(eventTimestampNs: Long, verticalAccel: Float) {
        val dt = if (lastAccelNs == 0L) {
            0f
        } else {
            ((eventTimestampNs - lastAccelNs) / 1_000_000_000.0).toFloat().coerceIn(0f, 0.2f)
        }
        lastAccelNs = eventTimestampNs
        if (dt <= 0f) return
        val leak = kotlin.math.exp(-dt / VERTICAL_TAU_S)
        verticalVelocity = (verticalVelocity + verticalAccel * dt) * leak
        verticalDisplacement = (verticalDisplacement + verticalVelocity * dt) * leak
        aggregator.setVerticalDisplacement(verticalDisplacement)
    }

    private fun registerSensors(manager: SensorManager) {
        val listener = sensorListener

        // 【分两类注册，这是省电的核心】
        //
        // 第一类「核心」：气压 + 竖直运动证据。它们必须连续采样才能工作，
        // 请求速率维持 5 Hz 不动（主人的判断正确）。
        // 【两档速率】平时 1Hz，气压速率一超阈值就升 5Hz（见 SensorRatePlan 的实测依据）。
        // 计步是事件型传感器，速率参数对它没意义，永远留在"高速档"那一档的写法上。
        val core = listOf(
            Sensor.TYPE_PRESSURE to SensorRatePlan.LOW_US,
            // 【空闲档不注册运动流】它们被硬件钳在 5Hz，降 ODR 无效，只能整个注销；
            // 触发时由 switchRate 注册回来。计步器必须常驻：它是"电梯/爬楼 vs 平地走路"
            // 的判别证据，且是事件型、几乎不耗电。
            Sensor.TYPE_STEP_DETECTOR to SensorRatePlan.HIGH_US,
            // 光照单独给慢速率：10 分钟趋势用不上 5 Hz。
            // 但**别指望它省电**——真机上该传感器的 active-count = 2，
            // 系统自己的自动亮度也挂在上面（500 ms），物理器件本来就亮着，
            // 我们降速只减少自己的事件投递。
            Sensor.TYPE_LIGHT to LIGHT_SAMPLING_US,
        )
        var registered = 0
        core.forEach { (type, periodUs) ->
            val sensor = runCatching { manager.getDefaultSensor(type) }.getOrNull()
            if (sensor == null) {
                Log.w(TAG, "拿不到传感器 type=$type")
                return@forEach
            }
            val ok = runCatching {
                // 带批量注册：少投递、少唤醒，但样本与时间戳一个不少（见 BATCH_LATENCY_US）
                manager.registerListener(listener, sensor, periodUs, BATCH_LATENCY_US)
            }.getOrElse { error ->
                Log.w(TAG, "注册 type=$type 抛异常: $error")
                false
            }
            if (ok) registered++ else Log.w(TAG, "注册 type=$type 失败")
        }
        Log.i(
            TAG,
            "已注册 $registered/${core.size} 个核心传感器：采样周期 ${currentRateUs / 1000} 毫秒" +
                "（${if (currentRateUs == SensorRatePlan.LOW_US) "低速档 1Hz" else "高速档 5Hz"}）" +
                "，批量 ${BATCH_LATENCY_US / 1000} 毫秒",
        )

        // 第二类「体感」：心率与腕温 —— 改成脉冲式，默认关着，每 PULSE_INTERVAL_MS 开 PULSE_ON_MS。
        //
        // 为什么必须这样，而不是"把回调速率调低"：
        // 真机 `dumpsys sensorservice` 显示 HEART_RATE 的 maxRate 就是 1.00 Hz，
        // 我们请求 5 Hz 也被硬件钳到 1 Hz —— **降速率一点用都没有**。
        // 而它的 active-count = 1：全表只有我们一个客户端，
        // 也就是说这个应用的报错是"手表的 PPG 光电传感器 7×24 亮着"。
        // PPG 是手表上最贵的传感器（LED 驱动电流），
        // 主人说"耗电跟睡眠监测差不多"正是因为睡眠监测也在常开 PPG。
        // 唯一有效的动作是**停止注册**，让它真正断电。
        pulseSensors = listOf(Sensor.TYPE_HEART_RATE, TYPE_WRIST_TEMPERATURE)
            .mapNotNull { type -> runCatching { manager.getDefaultSensor(type) }.getOrNull() }
        pulseJob = scope?.launch {
            while (true) {
                registerPulse(manager)
                delay(PULSE_ON_MS)
                unregisterPulse(manager)
                delay(PULSE_INTERVAL_MS - PULSE_ON_MS)
            }
        }
        Log.i(TAG, "体感传感器改为脉冲采样：每 ${PULSE_INTERVAL_MS / 1000} 秒开 ${PULSE_ON_MS / 1000} 秒")
    }

    /** 打开心率/腕温的采样窗口。 */
    private fun registerPulse(manager: SensorManager) {
        pulseHrSeen = false
        pulseTempSeen = false
        pulseWindowStartedMs = System.currentTimeMillis()
        pulseWindowOpen = true
        pulseSensors.forEach { sensor ->
            runCatching { manager.registerListener(sensorListener, sensor, CORE_SAMPLING_US) }
                .onFailure { Log.w(TAG, "脉冲注册 type=${sensor.type} 失败: $it") }
        }
        Log.i(TAG, "体感采样窗口开启")
    }

    /** 关闭窗口——这一步才是省电发生的地方。 */
    private fun unregisterPulse(manager: SensorManager) {
        pulseWindowOpen = false
        pulseSensors.forEach { sensor ->
            runCatching { manager.unregisterListener(sensorListener, sensor) }
        }
        Log.i(TAG, "体感采样窗口关闭")
    }

    /**
     * 心率与腕温都到手了就**立刻**关窗，不等那个 20 秒定时器。
     *
     * ## 为什么必须这样（真机账，2026-09-12）
     *
     * `dumpsys batterystats` 里我们这一项：`Sensor 21`（心率）**注册了 7 小时 33 分**，
     * 而进程总共只跑了约 15.7 小时 —— 也就是说 PPG 亮了 **48%** 的时间，
     * 设计值（每 10 分钟开 20 秒）是 **3.3%**，差了一个数量级还多；
     * 腕温 `Sensor 69815` 同样是 7 小时 35 分。
     *
     * 机制不是"定时器不准"：真机日志里清醒时每对开/关都精确是 20.0 秒。
     * 出问题的是**进程被冻结或被杀的那一刻**——尤其是主人睡觉触发手表自己的睡眠模式时。
     * 关窗这个动作挂在协程的定时器上，进程一冻结，定时器就再也不会响：
     * 窗口从"开启"一直挂到进程被系统杀掉为止，PPG 就这么白亮了几小时。
     *
     * 改成"数据到手就关"之后，窗口的敞口只取决于**首个心率读数什么时候来**
     * （PPG 在腕时 1 Hz 上下，通常 1 秒内），而定时期只作为兜底留给"根本没数据"的情况。
     * 这样即使立刻被冻结，也已经关过了。
     *
     * 腕温给 [PULSE_TEMP_GRACE_MS] 的宽限：它常比心率晚一两拍，但绝不能为了等它
     * 把窗口敞着——腕温拿不到就是拿不到（离腕时它压根不上报有效值）。
     */
    private fun maybeClosePulseWindow() {
        if (!pulseWindowOpen) return
        if (!pulseHrSeen) return
        val waitedMs = System.currentTimeMillis() - pulseWindowStartedMs
        if (pulseTempSeen || waitedMs >= PULSE_TEMP_GRACE_MS) {
            sensorManager?.let { unregisterPulse(it) }
        }
    }

    /**
     * 原始流黑匣子：排队一条事件（**只在内存里排队**，传感器回调线程上不做 I/O）。
     *
     * 开关是文件 `files/raw_log_on`：用 `adb shell touch / rm` 控制，
     * 不为一次性实验往手表界面上加按钮（主人的规矩：界面不加没用的东西）。
     */
    private fun queueRawLog(timestampNs: Long, sensorType: Int, values: FloatArray) {
        if (!rawLogEnabled) {
            // 每秒 15 个事件，别每次都 stat 文件；每 5 秒看一次足够
            val nowMs = System.currentTimeMillis()
            if (nowMs - rawLogFlagCheckedMs < 5_000L) return
            rawLogFlagCheckedMs = nowMs
            val context = appContext ?: return
            val dir = context.getExternalFilesDir(null) ?: context.filesDir
            rawLogEnabled = File(dir, RAW_LOG_FLAG).exists()
            if (!rawLogEnabled) return
            Log.i(TAG, "原始流记录已开启（上限 ${RAW_LOG_MAX_BYTES / 1024 / 1024} MB ≈ 4 天）")
        }
        if (rawLogBytes > RAW_LOG_MAX_BYTES) return
        if (rawLogQueue.size >= 20_000) return
        val sb = StringBuilder(48)
        sb.append(timestampNs).append(',').append(sensorType)
        for (v in values) sb.append(',').append(v)
        rawLogQueue.addLast(sb.toString())
    }

    /**
     * 两档速率的切换判定（由采样循环每 5 秒调一次）。
     *
     * 判据用**内存里已有的样本**算 30 秒基线速率，不额外读盘、不额外唤醒。
     * 切档就是重新注册一次那三路传感器——会有几毫秒的空隙，对高度解耦没有影响
     * （回放器是按"逐事件投递"建模的，没建这个空隙；0.02 hPa 的误差里已经包含它的量级）。
     */
    private fun updateSamplingRate(nowMs: Long) {
        val manager = sensorManager ?: return
        // 30 秒基线速率（用已落盘的样本；样本不够就不判）
        val latest = samples.lastOrNull() ?: return
        val past = samples.lastOrNull { latest.timestampMs - it.timestampMs >= SensorRatePlan.SHORT_BASELINE_MS }
        val spanMs = past?.let { latest.timestampMs - it.timestampMs } ?: 0L
        val rate = if (spanMs >= SensorRatePlan.SHORT_BASELINE_MS) {
            (latest.pressureHpa - past!!.pressureHpa) / (spanMs / 60_000f)
        } else {
            0f
        }
        val far = samples.lastOrNull { latest.timestampMs - it.timestampMs >= SensorRatePlan.LONG_BASELINE_MS }
        val longSpan = far?.let { latest.timestampMs - it.timestampMs } ?: 0L
        val longRate = if (longSpan >= SensorRatePlan.LONG_BASELINE_MS) {
            (latest.pressureHpa - far!!.pressureHpa) / (longSpan / 60_000f)
        } else {
            0f
        }
        val shortFired = SensorRatePlan.shouldGoFast(rate, spanMs)
        val longFired = SensorRatePlan.shouldGoFast(longRate, longSpan)
        if (shortFired || longFired) {
            fastUntilMs = nowMs + SensorRatePlan.HOLD_MS
            switchRate(manager, SensorRatePlan.HIGH_US)
            Log.i(
                TAG,
                "触发高速档（${if (shortFired) "短尺度 ${csvNum(rate, 2)}" else "长尺度 ${csvNum(longRate, 2)}"} hPa/分）" +
                    " → 续期至 ${SensorRatePlan.HOLD_MS / 1000} 秒后",
            )
        } else if (fastUntilMs > 0L && SensorRatePlan.shouldReturnToLow(nowMs, fastUntilMs)) {
            fastUntilMs = 0L
            switchRate(manager, SensorRatePlan.LOW_US)
        }
    }

    /** 切换核心传感器的采样周期（重新注册；计步器不动）。 */
    private fun switchRate(manager: SensorManager, rateUs: Int) {
        if (rateUs == currentRateUs) return
        currentRateUs = rateUs
        val listener = sensorListener
        listOf(
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_LINEAR_ACCELERATION,
        ).forEach { type ->
            val sensor = runCatching { manager.getDefaultSensor(type) }.getOrNull() ?: return@forEach
            runCatching { manager.unregisterListener(listener, sensor) }
            // 空闲档只留气压：重力/线加速度被硬件钳在 5Hz，降频无效，只能整个注销
            val keep = type == Sensor.TYPE_PRESSURE || rateUs != SensorRatePlan.LOW_US
            if (keep) runCatching { manager.registerListener(listener, sensor, rateUs, BATCH_LATENCY_US) }
        }
        Log.i(
            TAG,
            if (rateUs == SensorRatePlan.LOW_US) {
                "已回到空闲档：只留气压 1Hz + 计步，运动流已注销"
            } else {
                "已切到高速档：气压/重力/线加速度 5Hz（含续期）"
            },
        )
    }

    /** 把排队的原始事件刷进 raw_sensors.csv（由采样循环每 5 秒调一次，顺带的那次唤醒）。 */
    private fun flushRawLog() {
        if (!rawLogEnabled || rawLogQueue.isEmpty()) return
        val context = appContext ?: return
        runCatching {
            val dir = context.getExternalFilesDir(null) ?: context.filesDir
            val file = File(dir, RAW_LOG_FILE)
            // File.writer(append=true) 是 Java 的 FileWriter；Kotlin 的 writer() 只接 Charset
            val writer = rawLogWriter ?: java.io.FileWriter(file, true).buffered().also {
                val fresh = file.length() == 0L
                rawLogBytes = file.length()
                if (fresh) it.append("timestamp_ns,sensor_type,v0,v1,v2").append('\n')
                // 每次开会话都写一行"墙上时间 ↔ 开机纳秒"映射：
                // 原始流用的是 event.timestamp（开机纳秒），没有它就没法把回放窗口
                // 对到"哪一段是电梯、哪一段是散步"。以 # 开头的行由解析器跳过。
                val bootNs = android.os.SystemClock.elapsedRealtimeNanos()
                val epochMs = System.currentTimeMillis()
                it.append("#clock,").append(epochMs.toString()).append(',').append(bootNs.toString())
                    .append('\n')
                rawLogWriter = it
            }
            var written = 0L
            while (rawLogQueue.isNotEmpty()) {
                val line = rawLogQueue.removeFirst()
                writer.append(line).append('\n')
                written += line.length + 1
            }
            writer.flush()
            rawLogBytes += written
            if (rawLogBytes > RAW_LOG_MAX_BYTES) {
                Log.i(TAG, "原始流记录到达上限，停止写入（删掉 $RAW_LOG_FLAG 可关闭）")
                rawLogEnabled = false
                rawLogQueue.clear()
            }
        }.onFailure { Log.w(TAG, "原始流刷盘失败: $it") }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                    Sensor.TYPE_PRESSURE -> {
                        latestPressure = event.values[0]
                        aggregator.addPressure(event.values[0])
                        queueRawLog(event.timestamp, event.sensor.type, event.values)
                    }

                    Sensor.TYPE_LIGHT -> lightLux = event.values[0]

                    // 【注意 0 不是心率】传感器在**离开手腕/没锁住**时会持续上报 0，
                    // 而不是不上报。0 的含义是"没在测"，不是"心率是 0"。
                    // 原样留着会一路乘进小时归档的中位数，于是 AI 报告里出现
                    // "这一小时平均心率 0 bpm"——归档是要长期保存的，脏值会传下去。
                    // 与腕温那条 `values[0] > 1f` 是同一个处理：无效读数一律变 null。
                    Sensor.TYPE_HEART_RATE -> {
                        val bpm = event.values.getOrNull(0) ?: 0f
                        heartRate = bpm.takeIf { it > MIN_VALID_HEART_RATE_BPM }
                        heartRate?.let {
                            lastValidHeartRate = it
                            lastValidHeartRateMs = System.currentTimeMillis()
                        }
                        // 拿到一次有效心率就够这一轮用了：立刻关窗（见 maybeClosePulseWindow）
                        if ((heartRate ?: 0f) > MIN_VALID_HEART_RATE_BPM) {
                            pulseHrSeen = true
                            maybeClosePulseWindow()
                        }
                    }

                    TYPE_WRIST_TEMPERATURE -> {
                        val values = event.values
                        if (values.isNotEmpty() && values[0] > 1f) {
                            wristTemperature = values[0]
                            if (pulseWindowOpen && !pulseTempSeen) {
                                pulseTempSeen = true
                                // 腕温也到手了：这一轮没有别的要等，立刻关窗
                                maybeClosePulseWindow()
                            }
                        }
                    }

                    Sensor.TYPE_GRAVITY -> {
                        event.values.copyInto(gravity)
                        queueRawLog(event.timestamp, event.sensor.type, event.values)
                    }

                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        queueRawLog(event.timestamp, event.sensor.type, event.values)
                        val gMagnitude = sqrt(
                            gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]
                        )
                        if (gMagnitude > 0.1f) {
                            // 投影到重力方向，只保留垂直分量：平地走路的水平摆动不算高度证据。
                            val vertical = (
                                event.values[0] * gravity[0] +
                                    event.values[1] * gravity[1] +
                                    event.values[2] * gravity[2]
                                ) / gMagnitude
                            aggregator.addVerticalAccel(abs(vertical))
                            integrateVertical(event.timestamp, vertical)
                        }
                    }

                    Sensor.TYPE_STEP_DETECTOR -> {
                        aggregator.addStep()
                        stepPulses++
                    }                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

    private var stepPulses = 0

    /** 界面实时读数：只更新「此刻」的值，不碰引擎与落盘。 */
    private suspend fun liveLoop() {
        while (true) {
            delay(LIVE_TICK_MS)
            val now = System.currentTimeMillis()

            // 静息基线：只用「这一段聚合区间内没有垂直运动、没有步数」的静止样本，
            // 取低分位数。瞬时值直接当静息心率是错的（爬楼时也会读到 120）。
            val currentHeartRate = heartRate
            if (currentHeartRate != null && currentHeartRate > MIN_VALID_HEART_RATE_BPM &&
                aggregator.currentSteps == 0 && aggregator.currentAccelPeak < 0.35f &&
                // 硬上限：静止但心率 >100 的多半是组间休息/刚运动完，
                // 不能算静息样本。取 10 百分位已经能挡住大部分，这条是第二道闸。
                currentHeartRate < 100f
            ) {
                restingCandidates += currentHeartRate
                if (restingCandidates.size > 900) {
                    restingCandidates.removeAt(0)
                }
                restingBaseline = percentile(restingCandidates, 0.1f)
            }

            // 环境光 10 分钟慢趋势：将来用于考察「光照下降 + 气压下降」是否同时出现，
            // 但在有真实降水标注之前，它只记录、不参与任何判定。
            lightLux?.let { lux ->
                lightHistory.addLast(now to lux)
                while (lightHistory.isNotEmpty() &&
                    now - lightHistory.first().first > LIGHT_TREND_WINDOW_MS
                ) {
                    lightHistory.removeFirst()
                }
                val oldest = lightHistory.firstOrNull()
                lightDelta10Min = if (oldest != null && now - oldest.first >= LIGHT_TREND_MIN_SPAN_MS) {
                    lux - oldest.second
                } else {
                    null
                }
            }

            // 腕温基线：慢速滑动均值。绝对值没有天气含义（环境与衣袖是最大混淆项），
            // 所以只在攒够读数之后才给出基线，避免刚启动时报出一次假「偏离」。
            wristTemperature?.let { temperature ->
                wristTempReadings++
                val previous = wristTempBaseline
                wristTempBaseline = if (previous == null) temperature else previous * 0.98f + temperature * 0.02f
            }

            _state.value = _state.value.copy(
                recording = true,
                pressureHpa = latestPressure,
                weatherPressureHpa = latestPressure?.minus(elevationOffsetHpa),
                // 跨窗口累计位移：与解耦用的是同一个偏移（界面显示的就是它）
                elevationOffsetMeters = Barometric.offsetToMeters(elevationOffsetHpa, latestPressure),
                // 新鲜值优先；两次脉冲之间显示最近一次有效值（超 30 分钟才显示"—"）
                heartRateBpm = HeartRateDisplay.pick(
                    fresh = currentHeartRate,
                    lastValid = lastValidHeartRate,
                    lastValidMs = lastValidHeartRateMs,
                    nowMs = now,
                ),
                restingHeartRateBpm = restingBaseline,
                wristTemperatureC = wristTemperature,
                wristTemperatureBaselineC = wristTempBaseline?.takeIf { wristTempReadings >= 20 },
                lightLux = lightLux,
                lightDelta10Min = lightDelta10Min,
                elapsedSeconds = (now - startedAtMs) / 1000,
            )
        }
    }

    /** 样本节奏：聚合 → 喂引擎 → 落盘。3 小时窗口下的真正输入。 */
    private suspend fun sampleLoop() {
        var nextDueMs = System.currentTimeMillis() + SAMPLE_INTERVAL_MS
        while (true) {
            val wait = nextDueMs - System.currentTimeMillis()
            if (wait > 0) delay(wait)
            nextDueMs += SAMPLE_INTERVAL_MS

            val now = System.currentTimeMillis()

            // 【脉冲窗口的第二道闸】按**墙钟**超时强关（见 core/PulseWindow.kt 的真机账）。
            //
            // 关窗原本只挂在协程的 delay(20s) 上，而 delay 走 uptime —— 手表熄屏空闲时整机挂起，
            // uptime 不走，于是"20 秒"在真机上被拖成平均 8.9 分钟：dumpsys 里心率传感器
            // 注册了 7h33m/51 次，PPG 亮了 48%（设计 3.3%），而**日志里清醒时每一对都精确是 20.0 秒**，
            // 光看日志根本发现不了。采样循环本来就按墙钟调度，顺手在这里判一次，
            // 下一次 tick（挂起结束后）就能把窗口关掉，不必等进程被杀。
            if (pulseWindowOpen && pulseWindowExpired(pulseWindowStartedMs, now, PULSE_FORCE_CLOSE_MS)) {
                Log.i(TAG, "脉冲窗口超时（墙钟），强关以免 PPG 空亮")
                sensorManager?.let { unregisterPulse(it) }
            }

            flushRawLog()
            updateSamplingRate(now)

            val sample = aggregator.flush(now) ?: continue

            samples += sample
            while (samples.size > MAX_SAMPLES) {
                samples.removeAt(0)
            }

            val formal = engineFormal.compute(samples)
            val fast = engineFast.compute(samples)

            // 跨窗口累积高度偏移：引擎只报「最后一步」被归为高度事件的量，
            // 由调用方累加起来，就得到与窗口无关的、只含天气分量的气压。
            formal.lastElevationStepHpa?.let { elevationOffsetHpa += it }
            val weatherPressure = sample.pressureHpa - elevationOffsetHpa

            // 记住"最近降过多少"：取近期最高点与当前值之差。
            // 气压降完转入长时间平稳正是雨在下/雨将至的形态，只看窗口斜率会把它忘掉。
            weatherTrace.add(sample.timestampMs to weatherPressure)
            while (weatherTrace.size > 1 &&
                sample.timestampMs - weatherTrace[0].first > RECENT_FALL_WINDOW_MS
            ) {
                weatherTrace.removeAt(0)
            }
            recentFallHpa = if (weatherTrace.size >= RECENT_FALL_MIN_SAMPLES) {
                weatherPressure - weatherTrace.maxOf { it.second }
            } else {
                0f
            }
            // 过程状态机：只看天气分量，与高度无关
            val episode = episodeTracker.add(sample.timestampMs, weatherPressure)

            // 小时归档：整点切换时把上一小时落盘。
            // 体感数据也一并归档——AI 报告需要它们来判断混淆因素（例如头痛是否来自发热）。
            // 心率/腕温/光照取 liveLoop 写入的最新读数（它们比 5 秒聚合节奏快）。
            val latest = _state.value
            hourAccumulator.add(
                timestampMs = sample.timestampMs,
                weatherHpa = weatherPressure,
                rawHpa = sample.pressureHpa,
                heartRateBpm = latest.heartRateBpm,
                restingHeartRateBpm = restingBaseline,
                wristTempC = latest.wristTemperatureC,
                lightLux = latest.lightLux,
            )?.let { completed ->
                appContext?.let { HourlyArchive.append(it, completed) }
            }

            val restingNow = aggregator.currentSteps == 0 && aggregator.currentAccelPeak < 0.35f
            val written = logger?.append(
                timestampMs = sample.timestampMs,
                pressureHpa = sample.pressureHpa,
                verticalAccel = sample.verticalAccel,
                stepsInWindow = sample.stepsInWindow,
                trend = formal,
                restingHeartRateBpm = restingBaseline,
                lightDelta10Min = lightDelta10Min,
                weatherPressureHpa = weatherPressure,
                lightLux = lightLux,
                verticalDisplacementM = sample.verticalDisplacementM,
                // 落盘用跨窗口累计位移：窗口内局部量会在 ±70 米之间翻，事后回看会误判成爬了 70 米
                elevationOffsetMeters = Barometric.offsetToMeters(elevationOffsetHpa, latestPressure),
            ) ?: false

            if (now - lastPersistMs >= PERSIST_INTERVAL_MS) {
                lastPersistMs = now
                appContext?.let { context ->
                    SessionHistory.saveRecorderState(
                        context = context,
                        restingHeartRateBpm = restingBaseline,
                        elevationOffsetHpa = elevationOffsetHpa,
                        episode = episode,
                    )
                    // 耗电自记录：电量 + 本进程 CPU 时间，供事后归因
                    PowerLogger.append(
                        context = context,
                        appElapsedMs = now - startedAtMs,
                        samplesLogged = logger?.rowCount ?: 0,
                    )
                }
            }

            // ── 天气采集（旁路，不参与任何判定）──────────────────────────
            // **必须异步派生**：这段代码所在的 sampleLoop 是传感器采样循环，
            // 每 5 秒转一圈；在里面直接发网络请求会把传感器回调堆爆（最坏 20 秒超时）。
            //
            // 先查同意再查到期：没同意时 isDue 会永远为真（因为 collect 提前返回、
            // 不更新 lastFetchAt），那样每 5 秒都会派生一个协程——虽然它们立刻返回，
            // 但白白的调度不该有。
            appContext?.let { context ->
                if (WeatherConsent.isGranted(context) &&
                    WeatherCollector.isDue(context, now) &&
                    weatherJob?.isActive != true
                ) {
                    weatherJob = scope?.launch { WeatherCollector.collect(context, now) }
                }
            }

            // 预约的测试提醒：**由服务循环兑现**，走的是与真实提醒同一条路。
            // 这是为了回答"服务能不能调起震动"——用界面里的 delay 验不出来。
            // 日志带上预约时的时长：文件开关既可能约 2 分钟也可能约 30 分钟，
            // 事后回看日志必须能一眼分清这次验的是哪个量级。
            AlertState.consumeDelayedTest(now)?.let { delayed ->
                appContext?.let { TrendNotifier.triggerTestAlert(it) }
                Log.i(TAG, "测试提醒（$delayed）已兑现")
            }

            // 主动提醒：转坏到「高」时发一条通知（声音与震动交给系统）。
            // 只在升级时发一次并带冷却，避免变成噪音源——被关掉通知的提醒等于不存在。
            runCatching {
                val formalAssessment = WeatherRule.assess(formal, recentFallHpa, episode)
                val fastAssessment = WeatherRule.assess(fast, recentFallHpa, episode)
                val active = if (formalAssessment.likelihood != RainLikelihood.UNKNOWN) {
                    formalAssessment to formal
                } else {
                    fastAssessment to fast
                }
                appContext?.let { context ->
                    TrendNotifier.maybeNotify(
                        context = context,
                        likelihood = active.first.likelihood,
                        assessment = active.first,
                        trend = active.second,
                    )
                }
            }

            _state.value = _state.value.copy(
                recording = true,
                trend = formal,
                trendFast = fast,
                isResting = restingNow,
                recentFallHpa = recentFallHpa,
                episode = episode,
                weatherPressureHpa = weatherPressure,
                elevationOffsetMeters = Barometric.offsetToMeters(elevationOffsetHpa, latestPressure),
                loggedRows = logger?.rowCount ?: 0,
                logFileName = logger?.displayName ?: "",
                logHealthy = written,
            )
        }
    }

    /** 取低分位数：静息心率本质上是「心率分布的下沿」，不是平均值。 */
    private fun percentile(values: List<Float>, fraction: Float): Float {
        if (values.isEmpty()) return 0f
        val sorted = values.sorted()
        val index = ((sorted.size - 1) * fraction).toInt().coerceIn(0, sorted.size - 1)
        return sorted[index]
    }
}
