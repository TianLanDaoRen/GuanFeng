package com.yisiyun.guanfeng.ai

import android.content.Context
import android.util.Log
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/** 服务端限流：每 IP 每 10 分钟 10 次新建流。 */
class RateLimitedException : Exception("请求过频，请稍后再试")

/** 中继也没兜住（502），直接重试即可。 */
class EngineBusyException : Exception("AI 引擎暂时阻滞，请重试")

/**
 * 卦灵公共 AI 流 · plain 口客户端。
 *
 * 走 `/ai/plain`：明文 JSON 进出，无需加密信封、无需 apikey。
 * （此前实现的 RSA-OAEP + AES-GCM 信封口保留在 git 历史里，
 *   当前用不上就不留在代码库里——两套并存的复杂度不值得。）
 *
 * 请求体就是标准 Gemini REST 形状：
 *   · `systemInstruction` 放**约束**（有就原样采用，服务端不强加人设）
 *   · `contents` 放**数据**
 * 模型由服务端固定为 gemini-3.5-flash-lite，调用方无需关心。
 *
 * 定位：锦上添花功能。核心能力（气压趋势、高度解耦、打卡、关联视图）全部离线；
 * 本客户端只在用户于界面显式确认后才会被调用。
 */
object PublicAiClient {

    private const val TAG = "GuanFengAi"

    const val BASE_URL = "https://yunsisanren.top/gualing-core"
    private const val PLAIN_URL = "$BASE_URL/ai/plain"

    private const val CONNECT_TIMEOUT_MS = 15_000

    /** 公共口无 SLA、高峰可能排队，读超时放长。 */
    private const val READ_TIMEOUT_MS = 120_000

    /** 命中限流后的退避时长（按接口说明「请调用方做好退避重试」）。 */
    private const val RATE_LIMIT_BACKOFF_MS = 15_000L

    /**
     * 流式生成：每收到一段文本回调一次 [onDelta]，返回完整文本。
     *
     * [onNotice] 用于把「命中限流、正在退避重试」这类状态告诉界面——
     * 否则手表上会静默卡十几秒，看起来像挂了。
     *
     * [context] 只用于落盘一份提示词快照（见 [AiPromptDump]），不影响请求本身：
     * 落盘失败也不会让这次请求失败。
     */
    suspend fun stream(
        context: Context,
        systemPrompt: String,
        userContent: String,
        onDelta: (String) -> Unit,
        onNotice: (String) -> Unit = {},
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            // 【发出去之前】把两段提示词逐字落盘。
            // 放在这里而不是调用方：退避重试也走同一条路，落的就是**这次真正发出去的**内容，
            // 中间没有任何一处可能被谁改掉。
            AiPromptDump.write(context, System.currentTimeMillis(), systemPrompt, userContent)
            val body = buildRequestBody(systemPrompt, userContent)
            try {
                request(body, onDelta)
            } catch (limited: RateLimitedException) {
                Log.w(TAG, "命中限流，${RATE_LIMIT_BACKOFF_MS / 1000} 秒后退避重试一次")
                onNotice("请求过频，${RATE_LIMIT_BACKOFF_MS / 1000} 秒后自动重试")
                delay(RATE_LIMIT_BACKOFF_MS)
                request(body, onDelta)
            }
        }.onFailure { Log.w(TAG, "AI 请求失败: $it") }
    }

    private fun request(body: String, onDelta: (String) -> Unit): String {
        val connection = (URL(PLAIN_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            setRequestProperty("Content-Type", "application/json")
        }
        OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { it.write(body) }

        when (val code = connection.responseCode) {
            429 -> throw RateLimitedException()
            502 -> throw EngineBusyException()
            in 200..299 -> Unit
            else -> error("服务返回 HTTP $code")
        }

        val collected = StringBuilder()
        connection.inputStream.bufferedReader().use { reader ->
            while (true) {
                val line = reader.readLine() ?: break
                val trimmed = line.trim()
                if (!trimmed.startsWith("data:")) continue
                val frame = trimmed.removePrefix("data:").trim()
                if (frame.isEmpty()) continue
                if (frame == "[DONE]") break
                val json = runCatching { JSONObject(frame) }.getOrNull() ?: continue
                if (json.has("error")) error("服务端返回错误帧")
                val delta = extractText(json) ?: continue
                if (delta.isNotEmpty()) {
                    collected.append(delta)
                    onDelta(delta)
                }
            }
        }
        Log.i(TAG, "生成完成，共 ${collected.length} 字")
        return collected.toString()
    }

    /** 标准 Gemini REST 请求体：systemInstruction 放约束、contents 放数据。 */
    fun buildRequestBody(systemPrompt: String, userContent: String): String = JSONObject().apply {
        put(
            "systemInstruction",
            JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt))),
        )
        put(
            "contents",
            JSONArray().put(
                JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", userContent)))
                }
            ),
        )
        put(
            "generationConfig",
            JSONObject().apply {
                put("temperature", 0.7)
                // 思考力度拉到 high（主人要求）：Gemini 3 代际用 thinkingLevel。
                // 已实测中继接受该字段且不报错；若将来模型换代导致字段不认，
                // 服务端一般会忽略未知字段而不是报错，不会让整次请求失败。
                put("thinkingConfig", JSONObject().put("thinkingLevel", "high"))
                // 思考会占用输出预算，所以显式给足；不设则由服务端默认，
                // 在 high 思考下可能把正文挤掉。
                put("maxOutputTokens", 4096)
            },
        )
    }.toString()

    private fun extractText(frame: JSONObject): String? =
        frame.optJSONArray("candidates")
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optString("text")
}
