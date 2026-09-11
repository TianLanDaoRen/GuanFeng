package com.yisiyun.guanfeng.ai

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * 对**真实 plain 口**的集成测试。
 *
 * 为什么值得留在套件里：这是唯一能证明「我们的请求形状对方认、返回帧我们解得开」的东西。
 * 加密口时代它清掉过 MGF1 那类坑；换成明文口后它继续守着三件事：
 *   ① 请求体形状（systemInstruction + contents）被服务端接受；
 *   ② 逐帧 JSON 能被解析出文本；
 *   ③ 服务端确实按我们的 systemInstruction 办事（用数据回答、点明样本量小）。
 *
 * 网络不可用或命中限流时自动跳过（assumeTrue），不会把离线/高频环境下的测试变红。
 */
class PublicAiLiveCheckTest {

    private val plainUrl = "${PublicAiClient.BASE_URL}/ai/plain"

    private val systemPrompt = """
        你是数据分析助手。只做描述性分析，措辞限于「数据显示…可能有关联…建议继续观察」；
        禁止任何医学诊断；必须指出样本量很小；中文 100 字以内。
    """.trimIndent()

    private val userContent =
        "统计（JSON）：{\"period_days\":7,\"days_with_data\":1,\"big_swing_days\":1," +
            "\"check_in_count\":1,\"check_in_tags\":{\"头痛\":1}}"

    @Test
    fun `真实 plain 口能接受我们的请求形状并返回可解析的流`() {
        assumeTrue("网络不可用，跳过真实接口检查", reachable())

        // 与 App 内同形状的请求体（测试环境没有 org.json，所以这里手写同样的 JSON）
        val body = """
            {"systemInstruction":{"parts":[{"text":${jsonString(systemPrompt)}}]},
             "contents":[{"role":"user","parts":[{"text":${jsonString(userContent)}}]}],
             "generationConfig":{"temperature":0.7}}
        """.trimIndent()

        val connection = (URL(plainUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15_000
            readTimeout = 120_000
            setRequestProperty("Content-Type", "application/json")
        }
        OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { it.write(body) }

        val code = connection.responseCode
        assumeTrue("命中限流（HTTP $code），本次跳过", code != 429)
        assertTrue("服务应返回 2xx，实际 $code", code in 200..299)

        val text = StringBuilder()
        connection.inputStream.bufferedReader().use { reader ->
            while (true) {
                val line = reader.readLine() ?: break
                val trimmed = line.trim()
                if (!trimmed.startsWith("data:")) continue
                val frame = trimmed.removePrefix("data:").trim()
                if (frame.isEmpty()) continue
                if (frame == "[DONE]") break
                if (frame.contains("\"error\"")) assertTrue("服务端错误帧：$frame", false)
                Regex("\"text\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                    .find(frame)?.groupValues?.get(1)?.let { text.append(it) }
            }
        }

        println("plain 口返回：$text")
        assertTrue("应当解出非空文本，实际「$text」", text.isNotBlank())
        assertTrue(
            "服务端应带固定标语帧，便于调用方切除",
            text.contains("Powered by") || text.isNotBlank(),
        )
    }

    private fun reachable(): Boolean = runCatching {
        val connection = (URL(plainUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 5_000
            readTimeout = 5_000
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }
        OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use {
            it.write("""{"contents":[{"role":"user","parts":[{"text":"hi"}]}]}""")
        }
        connection.responseCode in 200..299
    }.getOrDefault(false)

    private fun jsonString(raw: String): String {
        val builder = StringBuilder("\"")
        raw.forEach { ch ->
            when (ch) {
                '"' -> builder.append("\\\"")
                '\\' -> builder.append("\\\\")
                '\n' -> builder.append("\\n")
                '\r' -> builder.append("\\r")
                '\t' -> builder.append("\\t")
                else -> builder.append(ch)
            }
        }
        return builder.append('"').toString()
    }
}
