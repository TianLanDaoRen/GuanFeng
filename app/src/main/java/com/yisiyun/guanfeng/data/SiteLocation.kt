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

    /** 坐标的小数位。天气尺度用不到更多精度，粗一点反而更稳。 */
    private const val DECIMALS = 3

    /** 是否**历史上真的成功取到过**坐标。失败界面靠它决定要不要给"用上次记录的坐标"。 */
    fun hasRemembered(context: Context): Boolean =
        prefs(context).getString(KEY_LAT, null) != null

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

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
    suspend fun acquire(context: Context, timeoutMs: Long = 90_000L): Fix? {
        // 先捡现成的：瞬时、零成本
        fromSystem(context)?.let {
            remember(context, it)
            return it
        }
        if (!hasPermission(context)) return null

        val manager = runCatching {
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }.getOrNull() ?: return null

        // 实测本机只有 passive 与 gps 两个 provider，没有 network provider
        for (provider in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)) {
            if (runCatching { manager.isProviderEnabled(provider) }.getOrDefault(false).not()) continue
            val location = withTimeoutOrNull(timeoutMs) { singleUpdate(manager, provider) } ?: continue
            val fix = Fix(
                lat = round(location.latitude),
                lon = round(location.longitude),
                source = "acquired:$provider",
            )
            remember(context, fix)
            Log.i(TAG, "定位成功：$provider ${fix.lat},${fix.lon}")
            return fix
        }
        Log.w(TAG, "主动定位超时或失败（室内常见）")
        return null
    }

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
