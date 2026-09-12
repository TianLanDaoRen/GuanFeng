package com.yisiyun.guanfeng.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 取坐标 —— **不主动定位，只捡系统已有的**。
 *
 * ## 为什么不请求新的定位
 *
 * 手表上开一次 GPS 是很贵的事：要独占射频、要几十秒、掉电明显。
 * 而我们要的只是"这半小时大概在哪"——天气的空间尺度是公里级，
 * 城市内移动几公里对气压几乎没有影响（气压场是最平滑的气象要素之一）。
 * 所以 `getLastKnownLocation` 完全够用：它**不触发任何硬件动作**，
 * 只是问系统"你最近一次知道我在哪"。
 *
 * ## 只用 COARSE 权限
 *
 * 粗定位的精度（约 1–3 公里）对天气绰绰有余，而它是比精确定位低一档的权限：
 * 手表上少弹一次"精确位置"的授权，对使用者是实打实的减负。
 *
 * ## 三级回退
 *
 *   1. 系统的最后已知位置（被动，不要 GPS、不要网络定位）
 *   2. **上次成功用过的坐标**（记在 SharedPreferences 里）
 *   3. 都没有 → 返回 null，**宁可不取数也不用错的坐标**
 *
 * 第 3 条是刻意的：用北京坐标去标定一个在别处的手表，得到的是看起来正常、
 * 实际全错的数据——那比没有数据危险得多。真取不到时界面上会说明原因。
 */
object SiteLocation {

    private const val TAG = "SiteLocation"
    private const val PREFS = "guanfeng_weather"
    private const val KEY_LAT = "site_lat"
    private const val KEY_LON = "site_lon"
    private const val KEY_SOURCE = "site_source"

    /** 坐标 + 它是怎么来的（落盘进 CSV，事后能看出位置有没有变过）。 */
    data class Fix(val lat: Double, val lon: Double, val source: String)

    /**
     * GPS 单次等待上限。主人定 30 秒，理由是实测出来的：
     * **星况好时 30 秒足够锁上，星况不好再等也没用**——室内等 78 秒与等 30 秒结果一样。
     * 与其耗着，不如早点落到下一步（网络推断或沿用上次坐标）。
     */
    const val GPS_TIMEOUT_MS = 30_000L

    /** 网络定位（provider 存在时）的等待上限；不存在时它是瞬间返回的。 */
    private const val NETWORK_TIMEOUT_MS = 8_000L

    /** 坐标的小数位。天气尺度用不到更多精度，粗一点反而更稳。 */
    private const val DECIMALS = 3

    /** 是否**历史上真的成功取到过**坐标。失败界面靠它决定要不要给"用上次记录的坐标"。 */
    fun hasRemembered(context: Context): Boolean =
        prefs(context).getString(KEY_LAT, null) != null

    /** 粗定位：Wi-Fi/网络定位够用。 */
    fun hasPermission(context: Context): Boolean = granted(context, Manifest.permission.ACCESS_COARSE_LOCATION)

    /**
     * 精定位：**GPS 只认这个权限**。
     *
     * 第一版只有 COARSE，结果 `requestLocationUpdates(GPS_PROVIDER, ...)` 抛 SecurityException，
     * 而那句异常被 runCatching 吞掉、日志写成"超时（室内常见）"——把一个权限问题
     * 误诊成了信号问题。所以这个判断必须单独存在，且失败原因必须打印出来。
     */
    fun hasFinePermission(context: Context): Boolean = granted(context, Manifest.permission.ACCESS_FINE_LOCATION)

    private fun granted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    /**
     * 解析当前坐标。**可能返回 null**，调用方必须处理（不要假装有坐标）。
     */
    fun resolve(context: Context): Fix? {
        fromSystem(context)?.let {
            remember(context, it) // 每次成功都记下来，作为下次的兜底
            return it
        }
        recalled(context)?.let {
            Log.i(TAG, "系统没有已知位置，沿用上次记录的坐标：${it.source}")
            return it
        }
        // **刻意没有"配置兜底坐标"这一层**。
        // 兜底等于替你猜你在哪，而本项目的规矩是「宁可不取数，也不用错的坐标」。
        // 取不到就是取不到，由设置流程明确问使用者要怎么办。
        return null
    }

    /**
     * **主动取一次定位**，带超时。这是"点开启之后进入定位界面"的实现。
     *
     * ## 为什么必须有这一步（而不是只靠 getLastKnownLocation）
     *
     * 实测这块表（OWW221）的 `gps` 与 `passive` 两个 provider 都是
     * `enabled=true, allowed=true, last location=null`——**硬件在、却没有任何缓存位置**，
     * 因为运动手表的 GPS 平时待机、只在开始运动时才开。
     *
     * 所以"读一下上次的位置"这条路在这台设备上**永远走不通**。第一版我给它配了个
     * 配置里的兜底坐标，那是错的：兜底等于替你猜你在哪，而这个项目自己的规矩是
     * 「宁可不取数，也不用错的坐标」。正确做法就是这里——**真的去取一次**，
     * 拿到才继续。
     *
     * ## 代价与它的边界
     *
     * 只在"用户明确开启"和"换过地方"时才走这条路，取到之后长期复用；
     * 而采集本身（每 30 分钟）只读已记录的坐标，不会再碰 GPS。
     * 室内锁不上星是真实存在的，所以**超时后必须给明确的失败出口**，不能卡死。
     */
    suspend fun acquire(
        context: Context,
        gpsTimeoutMs: Long = GPS_TIMEOUT_MS,
        networkTimeoutMs: Long = NETWORK_TIMEOUT_MS,
    ): Fix? {
        // 先捡现成的：瞬时、零成本
        fromSystem(context)?.let {
            remember(context, it)
            return it
        }
        if (!hasPermission(context)) {
            Log.w(TAG, "没有定位权限，无法定位")
            return null
        }

        val manager = runCatching {
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }.getOrNull() ?: return null

        // **混合定位**：先走网络/Wi-Fi（室内也能用、通常几秒），再走 GPS（精确但要求见天）。
        // 网络那一路只吃 COARSE 权限，所以用户即便只给了粗定位，这条仍然可用。
        // 每个源给**各自**的超时，而不是从总预算里对半分——否则网络那一截会把 GPS 的
        // 30 秒吃掉一半。本机没有 network provider 时它是瞬间返回的，不吃时间。
        val plan = listOf(
            Triple(LocationManager.NETWORK_PROVIDER, networkTimeoutMs, hasPermission(context)),
            Triple(LocationManager.GPS_PROVIDER, gpsTimeoutMs, hasFinePermission(context)),
        )

        for ((provider, timeout, permitted) in plan) {
            if (!permitted) {
                Log.i(TAG, "$provider 跳过：缺权限")
                continue
            }
            if (!runCatching { manager.isProviderEnabled(provider) }.getOrDefault(false)) {
                Log.i(TAG, "$provider 跳过：provider 未启用")
                continue
            }
            val location = withTimeoutOrNull(timeout) { singleUpdate(manager, provider) }
            if (location == null) {
                Log.i(TAG, "$provider 未在 ${timeout}ms 内给出位置")
                continue
            }
            val fix = Fix(round(location.latitude), round(location.longitude), "acquired:$provider")
            remember(context, fix)
            Log.i(TAG, "定位成功：$provider ${fix.lat},${fix.lon}")
            return fix
        }
        Log.w(TAG, "所有定位源都没给出位置")
        return null
    }

    /**
     * 用**网络推断**的位置作为兜底（IP 定位，见 QweatherClient.fetchNetworkLocation）。
     *
     * 与"配置里的兜底坐标"有本质区别：那个是**替使用者猜**，这个是**测量**——
     * 测的是"这台设备所在的网络出口在哪"，来源会如实写进 CSV。
     * 主人对前者的反对是对的，对后者不成立。
     */
    suspend fun networkFix(context: Context): Fix? {
        val result = QweatherClient.fetchNetworkLocation()
        val place = result.data ?: run {
            Log.w(TAG, "网络定位失败：${result.error}")
            return null
        }
        val fix = Fix(round(place.lat), round(place.lon), "network-ip:" + (place.city ?: "?"))
        remember(context, fix) // 记住它，否则每个采集周期都会重复问一次网络
        return fix
    }

    /**
     * 采集周期里的**静默刷新**：试一次定位（默认 100 秒），失败就沿用历史坐标。
     *
     * 这是主人定的策略——每 30 分钟采数据时顺带更新一次位置，但不为此打断任何东西：
     * 拿到新的就用新的，拿不到就继续用上次真取到过的那个，**绝不因此停止采集**。
     * 与设置流程里那次"拿到才继续"是两回事：第一次必须确知在哪，之后只需保持新鲜。
     */
    suspend fun refreshOrRemember(context: Context, gpsTimeoutMs: Long = GPS_TIMEOUT_MS): Fix? =
        acquire(context, gpsTimeoutMs = gpsTimeoutMs)
            ?: recalled(context)?.also { Log.i(TAG, "静默刷新未成功，沿用历史坐标：${it.source}") }
            // 连历史都没有（还没设置过就自己跑起来了）→ 问一次网络。
            // 拿到就会被记住，所以这条路径最多走一次。
            ?: networkFix(context)

    /** 请求一次定位更新，拿到第一个就撤监听。用老 API 是为了 minSdk 27 也走得通。 */
    private suspend fun singleUpdate(manager: LocationManager, provider: String): Location? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    runCatching { manager.removeUpdates(this) }
                    if (continuation.isActive) continuation.resume(location)
                }

                @Deprecated("Android S 之前必须实现", ReplaceWith(""))
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
                override fun onProviderEnabled(provider: String) = Unit
                override fun onProviderDisabled(provider: String) = Unit
            }
            continuation.invokeOnCancellation { runCatching { manager.removeUpdates(listener) } }
            runCatching {
                manager.requestLocationUpdates(
                    provider, 0L, 0f, listener, Looper.getMainLooper(),
                )
            }.onFailure {
                // **必须打出来**。第一版把这里吞了，于是 SecurityException（缺 FINE 权限）
                // 被我自己写成了"超时（室内常见）"，白白绕了一大圈。
                Log.w(TAG, "$provider 注册失败：${it.javaClass.simpleName} ${it.message}")
                if (continuation.isActive) continuation.resume(null)
            }
        }

    private fun fromSystem(context: Context): Fix? {
        if (!hasPermission(context)) {
            Log.i(TAG, "没有定位权限，无法读系统位置")
            return null
        }
        return runCatching {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // 依次尝试：被动（最省）→ 粗网络 → GPS。取到第一个可用的就返回。
            val order = listOf(
                LocationManager.PASSIVE_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.GPS_PROVIDER,
            )
            for (provider in order) {
                val location = runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
                if (location != null) {
                    return@runCatching Fix(
                        lat = round(location.latitude),
                        lon = round(location.longitude),
                        source = "system:$provider",
                    )
                }
            }
            null
        }.getOrNull()
    }

    private fun round(value: Double): Double {
        var factor = 1.0
        repeat(DECIMALS) { factor *= 10 }
        return Math.round(value * factor) / factor
    }

    private fun remember(context: Context, fix: Fix) {
        prefs(context).edit()
            .putString(KEY_LAT, fix.lat.toString())
            .putString(KEY_LON, fix.lon.toString())
            .putString(KEY_SOURCE, fix.source)
            .apply()
    }

    private fun recalled(context: Context): Fix? {
        val p = prefs(context)
        val lat = p.getString(KEY_LAT, null)?.toDoubleOrNull() ?: return null
        val lon = p.getString(KEY_LON, null)?.toDoubleOrNull() ?: return null
        return Fix(lat, lon, "recalled:" + (p.getString(KEY_SOURCE, "?")))
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}

/**
 * 天气采集的同意开关。
 *
 * 主人要求："依旧是用户同意了（但这次仅需同意一次，直接用安卓最轻量级的持久化系统记忆）"。
 * `SharedPreferences` 正是那个"最轻量级"——一个布尔值，不需要数据库、不需要 DataStore。
 *
 * 为什么联网这件事必须单独同意：观风的核心功能**一个网络请求都不发**，
 * 这是它跟别的天气应用最本质的区别。天气采集是唯一的外联功能，
 * 所以它必须是一个用户可以单独关掉、且默认关闭的开关。
 */
object WeatherConsent {

    private const val PREFS = "guanfeng_weather"
    private const val KEY_GRANTED = "forecast_consent_granted"
    private const val KEY_DECIDED = "forecast_consent_decided"

    /** 是否已经同意。默认 false —— 没问过就是没同意。 */
    fun isGranted(context: Context): Boolean = prefs(context).getBoolean(KEY_GRANTED, false)

    /** 是否已经问过（用来决定要不要弹那张一次性卡片）。 */
    fun hasDecided(context: Context): Boolean = prefs(context).getBoolean(KEY_DECIDED, false)

    fun grant(context: Context) {
        prefs(context).edit().putBoolean(KEY_GRANTED, true).putBoolean(KEY_DECIDED, true).apply()
    }

    fun decline(context: Context) {
        prefs(context).edit().putBoolean(KEY_GRANTED, false).putBoolean(KEY_DECIDED, true).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
