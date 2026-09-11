package com.yisiyun.guanfeng.ai

import android.util.Log
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.security.PublicKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * 卦灵公共 AI 流（免 JWT、免 apikey）的客户端。
 *
 * 加解密全部委托给 [EnvelopeCrypto]——那是纯 JVM 类，有本地往返单测与
 * 真实接口的集成测试；本类只负责「取公钥 / 发请求 / 逐帧取文本」这三件事，
 * 这样加密逻辑只有一份实现，不会两处漂移。
 *
 * 定位：这是**锦上添花**的功能。核心功能（气压趋势、高度解耦、打卡、关联视图）
 * 全部离线；本客户端只在用户于界面上显式同意后才会被调用。
 * 模型由服务端固定（gemini-3.5-flash-lite），无 SLA 无配额。
 */
object PublicAiClient {

    private const val TAG = "GuanFengAi"

    const val BASE_URL = "https://yunsisanren.top/gualing-core"
    private const val PUBLIC_KEY_URL = "$BASE_URL/public-key"
    private const val STREAM_URL = "$BASE_URL/ai/public-stream"

    private const val CONNECT_TIMEOUT_MS = 15_000

    /** 公共口无 SLA、高峰可能排队，读超时放长一些。 */
    private const val READ_TIMEOUT_MS = 120_000

    private var cachedPublicKey: PublicKey? = null

    /**
     * 流式生成：每收到一段文本回调一次 [onDelta]，返回完整文本。
     * 任一帧报错则整次判失败——协议要求不保留半截文本。
     */
    suspend fun stream(prompt: String, onDelta: (String) -> Unit): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val publicKey = cachedPublicKey ?: fetchPublicKey().also { cachedPublicKey = it }
                val sealed = EnvelopeCrypto.seal(publicKey, buildBusinessJson(prompt))
                val requestBody = JSONObject().apply {
                    put("sign", sealed.signBase64)
                    put("payload", sealed.payloadBase64)
                }.toString()

                Log.i(TAG, "请求公共 AI 流（提示词 ${prompt.length} 字）")
                val connection = (URL(STREAM_URL).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    connectTimeout = CONNECT_TIMEOUT_MS
                    readTimeout = READ_TIMEOUT_MS
                    setRequestProperty("Content-Type", "application/json")
                }
                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use {
                    it.write(requestBody)
                }

                val code = connection.responseCode
                if (code !in 200..299) error("服务返回 HTTP $code")

                val collected = StringBuilder()
                connection.inputStream.bufferedReader().use { reader ->
                    while (true) {
                        val line = reader.readLine() ?: break
                        val trimmed = line.trim()
                        if (!trimmed.startsWith("data:")) continue
                        val frame = trimmed.removePrefix("data:").trim()
                        if (frame.isEmpty()) continue
                        if (frame == "[DONE]") break
                        if (!EnvelopeCrypto.looksLikeBase64(frame)) {
                            Log.w(TAG, "跳过非加密帧")
                            continue
                        }
                        val json = runCatching { EnvelopeCrypto.openFrame(sealed.aesKey, frame) }
                            .getOrElse {
                                Log.w(TAG, "跳过无法解密的帧")
                                continue
                            }
                        if (json.contains("\"error\"")) error("服务端返回错误帧")
                        val delta = extractText(json)
                        if (!delta.isNullOrEmpty()) {
                            collected.append(delta)
                            onDelta(delta)
                        }
                    }
                }
                Log.i(TAG, "生成完成，共 ${collected.length} 字")
                collected.toString()
            }.onFailure { Log.w(TAG, "AI 请求失败: $it") }
        }

    /** 服务端换钥后可主动丢弃缓存。 */
    fun invalidatePublicKey() {
        cachedPublicKey = null
    }

    fun fetchPublicKey(): PublicKey {
        val connection = (URL(PUBLIC_KEY_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = CONNECT_TIMEOUT_MS
        }
        val pem = connection.inputStream.bufferedReader().use { it.readText() }
        return EnvelopeCrypto.parsePublicKey(pem)
    }

    /** 业务 JSON：标准 Gemini REST 请求体，模型由服务端固定。 */
    fun buildBusinessJson(prompt: String): String = JSONObject().apply {
        put("meta", JSONObject().put("type", "chat"))
        put(
            "gemini_req",
            JSONObject().apply {
                put(
                    "contents",
                    JSONArray().put(
                        JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                        }
                    ),
                )
                put("generationConfig", JSONObject().put("temperature", 0.7))
            },
        )
    }.toString()

    private fun extractText(json: String): String? =
        runCatching {
            JSONObject(json)
                .optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")
        }.getOrNull()
}
