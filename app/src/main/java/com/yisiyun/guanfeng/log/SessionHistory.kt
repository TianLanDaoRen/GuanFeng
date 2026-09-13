package com.yisiyun.guanfeng.log

import android.content.Context
import com.yisiyun.guanfeng.core.PressureSample
import com.yisiyun.guanfeng.core.WeatherEpisode
import java.io.File

/**
 * 历史会话读取：让「正式 3 小时窗口」在服务重启后依然连续。
 *
 * 为什么必须有它：前台服务能保住进程，但服务重启、应用更新、或系统回收后
 * START_STICKY 重建，都会让内存里的样本全丢、窗口从零开始攒——
 * 3 小时窗口下这意味着要再等一小时才有可信读数。
 * 把最近一段历史从 CSV 读回来，窗口就能接着算。
 *
 * 两条铁规则：
 *   1. 只回填窗口内的样本（更早的对趋势没有贡献）；
 *   2. **遇到断档就停**——进程没运行的那段时间没有数据，
 *      若把空洞两端的样本接起来做回归，会算出一段根本不存在的趋势。
 *      宁可窗口短一点，也不要跨空洞拟合。
 */
object SessionHistory {

    private val SAMPLE_FILE = CsvSessionLogger.FILE_NAME
    private const val MAX_ROWS_SCANNED = 20_000

    /**
     * 恢复历史时只读文件**尾部**这么多字节。
     *
     * 单一文件 + 30MB 上限之后，文件可能有十几万行；从头读会白白扫过整周数据。
     * 尾部 2MB 在 5 秒采样下约合 1.7 万行（≈24 小时），远超 3 小时窗口所需。
     */
    private const val TAIL_BYTES = 2L * 1024L * 1024L
    private const val RECORDER_STATE_FILE = "recorder_state.txt"
    private const val KEY_RESTING_HR = "resting_hr"
    private const val KEY_ELEVATION_OFFSET = "elevation_offset_hpa"

    // 天气过程状态机的锁存字段。见 WeatherEpisodeTracker.restore() 的说明：
    // 没有这几个键，进程一被回收，"正在下雨"就会被忘成「平稳」。
    private const val KEY_EPISODE_ACTIVE = "episode_active"
    private const val KEY_EPISODE_PEAK = "episode_peak_hpa"
    private const val KEY_EPISODE_MIN = "episode_min_hpa"
    private const val KEY_EPISODE_START_MS = "episode_start_ms"
    private const val KEY_EPISODE_DROP = "episode_drop_hpa"
    private const val KEY_EPISODE_WRITTEN_MS = "episode_written_ms"

    /**
     * 纯解析：CSV 文本 → 样本列表。
     * 列位置由表头决定而不是写死下标，这样将来增删列也不会读错。
     * 坏行直接跳过——不让一行脏数据毁掉整次恢复。
     */
    fun parse(lines: List<String>): List<PressureSample> {
        if (lines.isEmpty()) return emptyList()
        val header = lines.first().split(',')
        val indexOfTimestamp = header.indexOf("timestamp_ms")
        val indexOfPressure = header.indexOf("pressure_hpa")
        val indexOfAccel = header.indexOf("vertical_accel")
        val indexOfSteps = header.indexOf("steps")
        val indexOfDisplacement = header.indexOf("vertical_displacement_m")
        if (indexOfTimestamp < 0 || indexOfPressure < 0) return emptyList()

        val required = maxOf(indexOfTimestamp, indexOfPressure, indexOfAccel, indexOfSteps)
        val result = ArrayList<PressureSample>()
        for (line in lines.drop(1)) {
            if (line.isBlank()) continue
            val cells = line.split(',')
            if (cells.size <= required) continue
            val timestamp = cells[indexOfTimestamp].toLongOrNull() ?: continue
            val pressure = cells[indexOfPressure].toFloatOrNull() ?: continue
            val accel = if (indexOfAccel >= 0) cells[indexOfAccel].toFloatOrNull() ?: 0f else 0f
            val steps = if (indexOfSteps >= 0) cells[indexOfSteps].toIntOrNull() ?: 0 else 0
            // 净位移列是后加的：老文件没有该列时留 null，分类器会自动退回旧判据
            val displacement = indexOfDisplacement
                .takeIf { it >= 0 }
                ?.let { cells.getOrNull(it)?.toFloatOrNull() }
            // 用具名参数：样本字段会增加（净位移就是这样加进来的），
            // 位置参数在字段插入顺序变化时会静默错位。
            result += PressureSample(
                timestampMs = timestamp,
                pressureHpa = pressure,
                verticalAccel = accel,
                stepsInWindow = steps,
                verticalDisplacementM = displacement,
            )
        }
        return result
    }

    /**
     * 从末尾往前取，直到超出窗口、或遇到超过 maxGapMs 的断档为止。
     * 返回按时间升序排列（引擎要求有序）。
     */
    fun tailWithoutGaps(
        samples: List<PressureSample>,
        nowMs: Long,
        windowMs: Long,
        maxGapMs: Long,
    ): List<PressureSample> {
        if (samples.isEmpty()) return emptyList()
        val ordered = samples.sortedBy { it.timestampMs }
        val collected = ArrayDeque<PressureSample>()
        var previous: PressureSample? = null
        for (index in ordered.indices.reversed()) {
            val sample = ordered[index]
            if (nowMs - sample.timestampMs > windowMs) break
            val prev = previous
            if (prev != null && prev.timestampMs - sample.timestampMs > maxGapMs) break
            collected.addFirst(sample)
            previous = sample
        }
        return collected.toList()
    }

    /** 从唯一文件尾部恢复最近一段样本（供 3 小时趋势窗口续接）。 */
    fun loadRecent(
        context: Context,
        nowMs: Long,
        windowMs: Long,
        maxGapMs: Long,
    ): List<PressureSample> {
        val file = sampleFile(context)
        if (!file.isFile) return emptyList()
        val collected = runCatching { readTail(file) }.getOrElse { emptyList() }
        return tailWithoutGaps(collected, nowMs, windowMs, maxGapMs)
    }

    /** 唯一采样文件。 */
    fun sampleFile(context: Context): File {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        return File(directory, SAMPLE_FILE)
    }

    /** 从文件尾部读若干字节并解析（跳过可能被切断的首行）。 */
    private fun readTail(file: File, maxBytes: Long = TAIL_BYTES): List<PressureSample> =
        parse(readTailLines(file, maxBytes))

    /**
     * 只读文件**尾部**的行（跳过可能被切断的首行）。
     *
     * 注意：返回的第一行**不是表头**（表头在文件开头）。要按列名解析，
     * 得另外读一次首行——见 [loadTailHourlyBuckets]。
     *
     * **装满上限时保留的是最后那些行**（`takeLast`），不是最前那些行：
     * 这个窗口就是给"3 小时窗口续接"和"归档修补"用的，要的永远是**最新**的一段。
     * 第一版写的是"攒满 `MAX_ROWS_SCANNED` 就不再往里加"，那等于留下窗口里**最旧**的两万行、
     * 把最新的一段丢掉——比不设上限还坏，因为它不报错、也不变慢。
     */
    internal fun readTailLines(file: File, maxBytes: Long = TAIL_BYTES): List<String> {
        val (text, truncated) = file.inputStream().use { input ->
            val channel = input.channel
            val size = channel.size()
            val startAt = (size - maxBytes).coerceAtLeast(0L)
            channel.position(startAt)
            input.reader(Charsets.UTF_8).readText() to (startAt > 0L)
        }
        val usable = if (truncated) text.substringAfter('\n', text) else text
        return usable.lineSequence()
            .filter { it.isNotBlank() }
            .toList()
            .takeLast(MAX_ROWS_SCANNED)
    }

    /**
     * 把采样文件**尾部**折叠成小时桶，供归档修补使用（见 [missingHourBuckets]）。
     *
     * 为什么不复用 [loadHourlyRollup]：那个要**从文件开头**逐行扫完整份
     * （它服务的是"全部历史迁移"和按天窗口，没法假定数据在尾部），
     * 而 30MB 文件是 25 万行。修补只关心最近一小时上下，读尾部就够了。
     *
     * 表头必须单独从文件首行取：尾部读出来的第一行是被切断的半行数据。
     */
    fun loadTailHourlyBuckets(
        context: Context,
        nowMs: Long,
        withinMs: Long,
        onlyDecoupled: Boolean = true,
    ): List<com.yisiyun.guanfeng.core.HourlyBucket> {
        val file = sampleFile(context)
        if (!file.isFile) return emptyList()
        val header = runCatching { file.bufferedReader().use { it.readLine() } }
            .getOrNull()?.split(',') ?: return emptyList()
        val indexOfTimestamp = header.indexOf("timestamp_ms")
        val indexOfWeather = header.indexOf("weather_pressure_hpa")
        val indexOfRaw = header.indexOf("pressure_hpa")
        if (indexOfTimestamp < 0) return emptyList()
        // 要求已解耦时缺列就放弃：口径不同的历史不如不要（与 loadHourlyRollup 同一条规矩）
        if (onlyDecoupled && indexOfWeather < 0) return emptyList()
        val indexOfPressure = if (indexOfWeather >= 0) indexOfWeather else indexOfRaw
        if (indexOfPressure < 0) return emptyList()

        val cutoff = nowMs - withinMs
        val lines = runCatching { readTailLines(file) }.getOrElse { return emptyList() }
        val accumulator = com.yisiyun.guanfeng.core.HourlyAccumulator()
        for (line in lines) {
            val cells = line.split(',')
            val timestamp = cells.getOrNull(indexOfTimestamp)?.toLongOrNull() ?: continue
            if (timestamp < cutoff) continue
            val pressure = cells.getOrNull(indexOfPressure)?.toFloatOrNull() ?: continue
            accumulator.add(timestamp, pressure)
        }
        return accumulator.buckets().filter { it.hourStartMs >= cutoff }
    }

    /**
     * 长视图用的小时级降采样：扫描 [days] 天内的会话文件，
     * **边读边折叠成小时桶，不把整周样本留在内存里**。
     *
     * 关键：喂给桶的是 **weather_pressure_hpa（解耦掉高度后的天气分量）**，
     * 不是气压计原始读数。否则坐一趟电梯（约 9 hPa）会被算成一次「气压大变化日」，
     * 整张关联图的结论都会被污染。旧文件没有该列时退回原始读数。
     */
    fun loadHourlyRollup(
        context: Context,
        days: Int,
        nowMs: Long,
        /**
         * 只接受带天气分量列的行。
         *
         * 迁移历史时必须打开：老 CSV 没有 weather_pressure_hpa 列，只能拿原始气压兜底，
         * 而原始气压里含着坐电梯/爬楼的高度变化（实测一段里有 9.5 hPa 的人为落差）。
         * 那种数据进了归档只会污染「气压大变化日」这类统计——
         * 宁可少一段历史，也不要一段口径不同的历史。
         */
        onlyDecoupled: Boolean = false,
    ): List<com.yisiyun.guanfeng.core.HourlyBucket> {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        val cutoff = if (days >= ALL_HISTORY_DAYS) 0L
        else nowMs - days.toLong() * 24 * 60 * 60 * 1000
        val single = File(directory, SAMPLE_FILE)
        val files = if (single.isFile && single.lastModified() >= cutoff) listOf(single) else emptyList()

        val accumulator = com.yisiyun.guanfeng.core.HourlyAccumulator()
        for (file in files) {
            runCatching {
                file.bufferedReader().useLines { sequence ->
                    val iterator = sequence.iterator()
                    if (!iterator.hasNext()) return@useLines
                    // 用文件真实的表头解析，而不是猜列序——将来增删列也不会读错
                    val header = iterator.next().split(',')
                    val iTimestamp = header.indexOf("timestamp_ms")
                    val iWeather = header.indexOf("weather_pressure_hpa")
                    val iRaw = header.indexOf("pressure_hpa")
                    if (iTimestamp < 0) return@useLines
                    // 要求已解耦时，缺天气分量列就放弃整个文件（口径不同的历史不如不要）
                    if (onlyDecoupled && iWeather < 0) return@useLines
                    val iPressure = if (iWeather >= 0) iWeather else iRaw
                    if (iPressure < 0) return@useLines

                    feedRollup(
                        lines = iterator.asSequence(),
                        iTimestamp = iTimestamp,
                        iPressure = iPressure,
                        cutoff = cutoff,
                        accumulator = accumulator,
                    )
                }
            }
        }
        return accumulator.buckets().filter { it.hourStartMs >= cutoff }
    }

    /**
     * 把一段 CSV 行喂进小时桶（纯函数：不碰 Context、不开文件，好让"截断"这类错误能被单测钉死）。
     *
     * ## 这里为什么**没有**"扫满几万行就停"
     *
     * 原先有一道 `if (scanned > MAX_ROWS_SCANNED) break`，而且是从**文件开头**扫。
     * 文件现在约 1.7 万行/天、上限 30MB（约 25 万行），所以跨过两万行之后，
     * 它看到的**永远是最旧那一段**——最近的数据被静默丢掉，还符合"没报错、也没变慢"的假象。
     * 最直接的受害者是首次归档迁移（[ALL_HISTORY_DAYS]，本该把整份历史搬进归档），
     * 它只会搬最旧的两万行。
     *
     * 现在改为：**先用行首的时间戳做一次廉价筛选**（只取第一个逗号前那一段，
     * 不 split 整行），窗口外的行直接跳过。25 万行里绝大多数都落在窗口之外，
     * 省下的正是这些行的整行拆分——代价可以接受，而"少读一段历史"不可接受。
     */
    internal fun feedRollup(
        lines: Sequence<String>,
        iTimestamp: Int,
        iPressure: Int,
        cutoff: Long,
        accumulator: com.yisiyun.guanfeng.core.HourlyAccumulator,
    ) {
        // 采样文件的表头永远是 timestamp_ms 打头（唯一写入方是 CsvSessionLogger），
        // 所以常走的都是"廉价筛选"这条路；万一哪天列序变了，下面的分支仍按列名解析。
        val timestampFirst = iTimestamp == 0
        for (line in lines) {
            if (line.isBlank()) continue
            val cells: List<String>
            val timestamp: Long
            if (timestampFirst) {
                val comma = line.indexOf(',')
                if (comma <= 0) continue
                timestamp = line.substring(0, comma).toLongOrNull() ?: continue
                if (timestamp < cutoff) continue
                cells = line.split(',')
            } else {
                cells = line.split(',')
                timestamp = cells.getOrNull(iTimestamp)?.toLongOrNull() ?: continue
                if (timestamp < cutoff) continue
            }
            val pressure = cells.getOrNull(iPressure)?.toFloatOrNull() ?: continue
            accumulator.add(timestamp, pressure)
        }
    }

    /** 传这个天数表示「全部可用历史」，不受时间窗裁剪。 */
    const val ALL_HISTORY_DAYS = 100_000

    /** 读取最近一段时间的**原始环境光**读数，用于启动时立刻恢复光照趋势。 */
    fun loadRecentLight(
        context: Context,
        sinceMs: Long,
        nowMs: Long,
    ): List<Pair<Long, Float>> {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        val file = File(directory, SAMPLE_FILE).takeIf { it.isFile } ?: return emptyList()

        return runCatching {
            // 表头从**文件第一行**读（尾部窗口里没有表头），与 [loadTailHourlyBuckets] 同一条规矩
            val header = file.bufferedReader().use { it.readLine() }?.split(',') ?: return emptyList()
            val iTimestamp = header.indexOf("timestamp_ms")
            val iLight = header.indexOf("light_lux")
            if (iTimestamp < 0 || iLight < 0) return emptyList()
            val result = ArrayList<Pair<Long, Float>>()
            // 只读尾部 2MB（约 27 小时），不从文件开头扫：它的窗口只有 10 分钟
            // （调用方传 LIGHT_TREND_WINDOW_MS），却跑在**每次启动**的路径上——
            // 文件长到 30MB 时，从头扫一遍就是白等几百毫秒。
            for (line in readTailLines(file)) {
                val cells = line.split(',')
                val timestamp = cells.getOrNull(iTimestamp)?.toLongOrNull() ?: continue
                if (timestamp < sinceMs || timestamp > nowMs) continue
                val lux = cells.getOrNull(iLight)?.toFloatOrNull() ?: continue
                result += timestamp to lux
            }
            result.sortedBy { it.first }
        }.getOrElse { emptyList() }
    }

    /** 跨会话需要保留的一点点状态：静息心率基线、累计高度偏移，以及天气过程锁存。 */
    data class RecorderPersistedState(
        val restingHeartRateBpm: Float?,
        val elevationOffsetHpa: Float,
        /** 天气过程状态机的快照；没有记录过时为 null。 */
        val episode: WeatherEpisode? = null,
        /** 快照的写入时刻，用来判断它是否已经过时。 */
        val episodeWrittenMs: Long = 0L,
    )

    /**
     * 读回跨会话状态。
     *
     * `elevationOffsetHpa` 尤其重要：它是「解耦掉的高度分量」的累计值。
     * 若每次重启都从 0 开始，天气气压序列会在重启处跳一下（一次电梯就是 9 hPa），
     * 长视图会把它当成一次「气压大变化日」。
     */
    fun loadRecorderState(context: Context): RecorderPersistedState {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        val file = File(directory, RECORDER_STATE_FILE)
        if (!file.isFile) return RecorderPersistedState(null, 0f)
        return runCatching {
            var resting: Float? = null
            var offset = 0f
            var active = false
            var peak = 0f
            var minHpa = 0f
            var startMs = 0L
            var drop = 0f
            var writtenMs = 0L
            var sawEpisode = false
            file.readLines().forEach { line ->
                val parts = line.split('=')
                if (parts.size != 2) return@forEach
                val value = parts[1].trim()
                when (parts[0].trim()) {
                    KEY_RESTING_HR -> resting = value.toFloatOrNull()
                    KEY_ELEVATION_OFFSET -> offset = value.toFloatOrNull() ?: 0f
                    KEY_EPISODE_ACTIVE -> { active = value == "1"; sawEpisode = true }
                    KEY_EPISODE_PEAK -> peak = value.toFloatOrNull() ?: 0f
                    KEY_EPISODE_MIN -> minHpa = value.toFloatOrNull() ?: 0f
                    KEY_EPISODE_START_MS -> startMs = value.toLongOrNull() ?: 0L
                    KEY_EPISODE_DROP -> drop = value.toFloatOrNull() ?: 0f
                    KEY_EPISODE_WRITTEN_MS -> writtenMs = value.toLongOrNull() ?: 0L
                }
            }
            val episode = if (sawEpisode) {
                WeatherEpisode(
                    active = active,
                    dropHpa = drop,
                    startMs = startMs,
                    minHpa = minHpa,
                    peakHpa = peak,
                    clearedRiseHpa = null,
                )
            } else {
                null
            }
            RecorderPersistedState(resting, offset, episode, writtenMs)
        }.getOrElse { RecorderPersistedState(null, 0f) }
    }

    fun saveRecorderState(
        context: Context,
        restingHeartRateBpm: Float?,
        elevationOffsetHpa: Float,
        episode: WeatherEpisode? = null,
    ): Boolean = runCatching {
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        val text = buildString {
            append(KEY_RESTING_HR).append('=')
            append(csvNum(restingHeartRateBpm, 1)).append('\n')
            append(KEY_ELEVATION_OFFSET).append('=').append(csvNum(elevationOffsetHpa, 3)).append('\n')
            if (episode != null) {
                append(KEY_EPISODE_ACTIVE).append('=').append(if (episode.active) "1" else "0").append('\n')
                append(KEY_EPISODE_PEAK).append('=').append(csvNum(episode.peakHpa, 3)).append('\n')
                append(KEY_EPISODE_MIN).append('=').append(csvNum(episode.minHpa, 3)).append('\n')
                append(KEY_EPISODE_START_MS).append('=').append(episode.startMs).append('\n')
                append(KEY_EPISODE_DROP).append('=').append(csvNum(episode.dropHpa, 3)).append('\n')
                append(KEY_EPISODE_WRITTEN_MS).append('=').append(System.currentTimeMillis()).append('\n')
            }
        }
        File(directory, RECORDER_STATE_FILE).writeText(text)
        true
    }.getOrDefault(false)
}
