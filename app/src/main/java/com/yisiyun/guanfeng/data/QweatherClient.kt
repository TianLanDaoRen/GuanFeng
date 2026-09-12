package com.yisiyun.guanfeng.data

import android.util.Log
import com.yisiyun.guanfeng.BuildConfig
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * 和风天气的取数客户端 —— **经由自己部署的 Vercel 代理**，不直连和风。
 *
 * ## 为什么不直连（这是本文件存在的前提）
 *
 * 和风的 JWT 要用 Ed25519 **私钥签名**，而私钥一旦进 APK 就等于公开。
 * 所以私钥与 API Host 都留在 Vercel 的环境变量里，手表只发一个路径过去，
 * 由那边的函数签名并转发。客户端从头到尾没有见过私钥。
 *
 * ## 三个端点都有，靠路径区分
 *
 * 新 v1 API 把**坐标直接写进路径**（`/weather/v1/current/{lat}/{lon}`），
 * 所以**不需要 GeoAPI 反查城市**——少一次调用、少一个失败点，
 * 也正好绕开和风"地理信息数据不得批量缓存"那条限制（我们一个字节的地理数据都不存）。
 *
 * ## 时间口径
 *
 * `current` 的响应里**没有时间戳**（新 v1 与旧 v7 不同），所以取数时间由我们自己打标；
 * `hourly` 每条里有 `forecastTime`，那是**预报的目标时刻**（UTC，带 Z），
 * 与取数时刻是两个不同的东西，落盘时分成两列，别混。
 */
object QweatherClient {

    private const val TAG = "QweatherClient"

    /**
     * 代理地址。不是机密——真正的门是 [GATE_HEADER]，而私钥在服务端。
     *
     * **必须是自己的域名，不能用 `*.vercel.app`**：实测 `vercel-qweather.vercel.app`
     * 在国内被解析到 108.160.167.159（典型污染地址），而 `qweather.gualing.top`
     * 走 Cloudflare DNS 正常解析到 Vercel，实测耗时还更短（0.66s vs 1.03s）。
     * 换域名不影响流量走向——请求仍然完全由 Vercel 处理，不经过自家服务器。
     */
    const val PROXY_BASE = "https://qweather.gualing.top"

    /**
     * 门的钥匙。**刻意从 local.properties 注入而不是写在这里**：
     * 本仓库是公开的，写死就等于没有门。值由
     * `app/build.gradle.kts` 从 `local.properties` 读进 BuildConfig。
     */
    private const val GATE_HEADER = "x-gf-key"
    val gateKey: String get() = BuildConfig.QWEATHER_GATE_KEY

    private const val CONNECT_TIMEOUT_MS = 10_000
    private const val READ_TIMEOUT_MS = 20_000

    /** 取数是否可用：没有 gate key 就不发请求（克隆仓库的人也不会误打我们的代理）。 */
    val isConfigured: Boolean get() = gateKey.isNotBlank()

    /** 实时天气。字段比逐小时少，但它是"此刻"，校准要靠它对齐手表读数。 */
    data class Now(
        val conditionCode: String,
        val conditionText: String,
        val tempC: Double?,
        val feelsLikeC: Double?,
        val humidity: Double?,
        val windDegree: Int?,
        val windCompass: String,
        val windSpeedMs: Double?,
        val windScale: Int?,
        val windGustMs: Double?,
        val precipMm: Double?,
        val precipIntensityMmh: Double?,
        val precipType: String,
        val pressureHpa: Double?,
        val visibilityM: Double?,
        val dewPointC: Double?,
        val cloudCover: Double?,
        val uvIndex: Int?,
    )

    /** 逐小时预报的一条。24 条一组。 */
    data class Hour(
        val forecastTime: String,
        val conditionCode: String,
        val conditionText: String,
        val tempC: Double?,
        val feelsLikeC: Double?,
        val humidity: Double?,
        val windDegree: Int?,
        val windCompass: String,
        val windSpeedMs: Double?,
        val windScale: Int?,
        val windGustMs: Double?,
        val precipMm: Double?,
        val precipIntensityMmh: Double?,
        val precipProbability: Double?,
        val precipType: String,
        val pressureHpa: Double?,
        val visibilityM: Double?,
        val dewPointC: Double?,
        val cloudCover: Double?,
        val uvIndex: Int?,
    )

    data class Fetch<T>(val data: T?, val httpStatus: Int, val elapsedMs: Long, val error: String?)

    /** 网络推断出来的位置。见 [fetchNetworkLocation]。 */
    data class NetworkPlace(val lat: Double, val lon: Double, val city: String?, val accuracyKm: Int)

    /**
     * **网络定位**——按请求来源 IP 反推城市，等价于高德的「IP 定位」。
     *
     * ## 为什么需要它
     *
     * 这台手表上 GPS 是唯一能用的定位源（`dumpsys location` 里只有 passive 与 gps，
     * 没有 network provider），而 GPS 要见天——室内定不上。于是"在屋里就采不到天气"。
     *
     * ## 它为什么能成立
     *
     * Vercel 给每个进来的请求注入了来源 IP 的粗略位置（`x-vercel-ip-*`），
     * 代理把它转成一个坐标返回。**不需要设备任何定位权限、不需要第三方 SDK、
     * 也不需要把 IP 交给任何第三方**——IP 本来就在请求头里。
     *
     * ## 精度与它必须被标注的原因
     *
     * 城市级（几公里到十几公里）。对天气够用（气压场本就平滑、预报本身就是城市级），
     * 但**必须如实记录来源**：事后没人分得清哪条 CSV 是 GPS 来的、哪条是 IP 推的，
     * 校准就会把两种精度的数据混在一起算。
     */
    suspend fun fetchNetworkLocation(): Fetch<NetworkPlace> =
        fetch("gf/locate") { json ->
            val o = JSONObject(json)
            NetworkPlace(
                lat = o.getDouble("lat"),
                lon = o.getDouble("lon"),
                city = o.optString("city").takeIf { it.isNotBlank() && it != "null" },
                accuracyKm = o.optInt("accuracyKm", 10),
            )
        }

    /**
     * 坐标 → 城市名（和风 GeoAPI 的逆地理）。
     *
     * 只为让界面能说出"你在哪"，**不改变坐标本身**：IP 定位给出的坐标就是网络出口的位置，
     * 反查只是给它一个人类可读的名字。注意 location 参数是 **经度在前**。
     */
    suspend fun fetchCityName(lat: Double, lon: Double): String? =
        fetch("geo/v2/city/lookup?location=$lon,$lat") { json ->
            val list = JSONObject(json).optJSONArray("location") ?: return@fetch ""
            if (list.length() == 0) return@fetch ""
            val first = list.getJSONObject(0)
            val province = first.optString("adm1")
            val city = first.optString("adm2")
            val name = first.optString("name")
            // 省 + 市 + 区：直辖市会重复（北京市/北京市），去一下重
            listOf(province, city, name).filter { it.isNotBlank() }.distinct().joinToString(" ")
        }.data?.takeIf { it.isNotBlank() }

    suspend fun fetchNow(lat: Double, lon: Double): Fetch<Now> =
        fetch("weather/v1/current/$lat/$lon") { parseNow(JSONObject(it)) }

    suspend fun fetchHourly(lat: Double, lon: Double): Fetch<List<Hour>> =
        fetch("weather/v1/hourly/$lat/$lon") { json ->
            val root = JSONObject(json)
            val hours = root.getJSONArray("hours")
            (0 until hours.length()).map { parseHour(hours.getJSONObject(it)) }
        }

    private suspend fun <T> fetch(path: String, parse: (String) -> T): Fetch<T> =
        withContext(Dispatchers.IO) {
            if (!isConfigured) {
                return@withContext Fetch<T>(null, 0, 0, "未配置 gate key（local.properties 缺 qweather.gateKey）")
            }
            val startedAt = System.currentTimeMillis()
            var connection: HttpURLConnection? = null
            try {
                connection = (URL("$PROXY_BASE/$path").openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty(GATE_HEADER, gateKey)
                    connectTimeout = CONNECT_TIMEOUT_MS
                    readTimeout = READ_TIMEOUT_MS
                }
                val status = connection.responseCode
                val elapsed = System.currentTimeMillis() - startedAt
                val body = (if (status in 200..299) connection.inputStream else connection.errorStream)
                    ?.bufferedReader()?.use { it.readText() } ?: ""
                if (status !in 200..299) {
                    return@withContext Fetch<T>(null, status, elapsed, "HTTP $status: ${body.take(160)}")
                }
                Fetch(parse(body), status, elapsed, null)
            } catch (t: Throwable) {
                Fetch<T>(null, 0, System.currentTimeMillis() - startedAt, t.message ?: t.toString())
                    .also { Log.w(TAG, "取数失败：$path", t) }
            } finally {
                connection?.disconnect()
            }
        }

    private fun optDouble(parent: JSONObject, key: String): Double? =
        parent.optJSONObject(key)?.takeIf { !it.isNull("value") }?.optDouble("value")

    private fun parseNow(root: JSONObject): Now = Now(
        conditionCode = root.optJSONObject("condition")?.optString("code").orEmpty(),
        conditionText = root.optJSONObject("condition")?.optString("text").orEmpty(),
        tempC = optDouble(root, "temperature"),
        feelsLikeC = optDouble(root, "feelsLike"),
        humidity = root.optDoubleOrNull("humidity"),
        windDegree = root.optJSONObject("wind")?.optJSONObject("direction")?.optInt("degree"),
        windCompass = root.optJSONObject("wind")?.optJSONObject("direction")?.optString("compass").orEmpty(),
        windSpeedMs = optDouble(root.optJSONObject("wind") ?: JSONObject(), "speed"),
        windScale = root.optJSONObject("wind")?.optInt("scale"),
        windGustMs = optDouble(root, "windGust"),
        precipMm = optDouble(root.optJSONObject("precipitation") ?: JSONObject(), "amount"),
        precipIntensityMmh = optDouble(root.optJSONObject("precipitation") ?: JSONObject(), "intensity"),
        precipType = root.optJSONObject("precipitation")?.optString("type").orEmpty(),
        pressureHpa = optDouble(root, "pressure"),
        visibilityM = optDouble(root, "visibility"),
        dewPointC = optDouble(root, "dewPoint"),
        cloudCover = root.optDoubleOrNull("cloudCover"),
        uvIndex = root.optIntOrNull("uvIndex"),
    )

    private fun parseHour(o: JSONObject): Hour {
        val wind = o.optJSONObject("wind") ?: JSONObject()
        val precip = o.optJSONObject("precipitation") ?: JSONObject()
        return Hour(
            forecastTime = o.optString("forecastTime"),
            conditionCode = o.optJSONObject("condition")?.optString("code").orEmpty(),
            conditionText = o.optJSONObject("condition")?.optString("text").orEmpty(),
            tempC = optDouble(o, "temperature"),
            feelsLikeC = optDouble(o, "feelsLike"),
            humidity = o.optDoubleOrNull("humidity"),
            windDegree = wind.optJSONObject("direction")?.optInt("degree"),
            windCompass = wind.optJSONObject("direction")?.optString("compass").orEmpty(),
            windSpeedMs = optDouble(wind, "speed"),
            windScale = wind.optIntOrNull("scale"),
            windGustMs = optDouble(o, "windGust"),
            precipMm = optDouble(precip, "amount"),
            precipIntensityMmh = optDouble(precip, "intensity"),
            precipProbability = precip.optDoubleOrNull("probability"),
            precipType = precip.optString("type"),
            pressureHpa = optDouble(o, "pressure"),
            visibilityM = optDouble(o, "visibility"),
            dewPointC = optDouble(o, "dewPoint"),
            cloudCover = o.optDoubleOrNull("cloudCover"),
            uvIndex = o.optIntOrNull("uvIndex"),
        )
    }
}

/** org.json 的 optDouble 在字段缺失时返回 NaN，而我们要的是 null——两者语义不同。 */
private fun JSONObject.optDoubleOrNull(key: String): Double? =
    if (has(key) && !isNull(key)) optDouble(key) else null

private fun JSONObject.optIntOrNull(key: String): Int? =
    if (has(key) && !isNull(key)) optInt(key) else null
