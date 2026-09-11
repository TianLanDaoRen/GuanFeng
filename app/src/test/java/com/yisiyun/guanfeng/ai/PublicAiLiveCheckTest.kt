package com.yisiyun.guanfeng.ai

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * 对**真实服务端**的集成测试：验证 [EnvelopeCrypto] 的封装能被对方解开、
 * 且返回帧能被本地解密。
 *
 * 为什么值得单独测：这套信封最容易出现「本地自测通过、对端解不开」——
 * OAEP 的 MGF1 摘要或 GCM 的 tag 长度只要差一点就会失败，
 * 而这类错误在手表上调试代价极高。这里用同一份 [EnvelopeCrypto] 直连一次，
 * 把风险在这里清掉。
 *
 * 网络不可用时自动跳过（assumeTrue），不会让离线环境下的测试变红。
 */
class PublicAiLiveCheckTest {

    private val publicKeyUrl = "${PublicAiClient.BASE_URL}/public-key"
    private val streamUrl = "${PublicAiClient.BASE_URL}/ai/public-stream"

    private fun reachable(): Boolean = runCatching {
        val connection = (URL(publicKeyUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 5_000
            readTimeout = 5_000
            requestMethod = "GET"
        }
        connection.responseCode in 200..299
    }.getOrDefault(false)

    @Test
    fun `真实服务端能解开我们的信封并返回可解密的流`() {
        assumeTrue("网络不可用，跳过真实接口检查", reachable())

        // 1) 取公钥
        val pem = (URL(publicKeyUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 10_000
        }.inputStream.bufferedReader().use { it.readText() }
        val publicKey = EnvelopeCrypto.parsePublicKey(pem)

        // 2) 组信封（与 App 内完全同一条代码路径）
        val prompt = "用十个字以内说一句关于天气的话。"
        val business =
            """{"meta":{"type":"chat"},"gemini_req":{"contents":[{"role":"user","parts":[{"text":"$prompt"}]}],"generationConfig":{"temperature":1}}}"""
        val sealed = EnvelopeCrypto.seal(publicKey, business)

        // 3) 发送
        val connection = (URL(streamUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15_000
            readTimeout = 120_000
            setRequestProperty("Content-Type", "application/json")
        }
        val body = """{"sign":"${sealed.signBase64}","payload":"${sealed.payloadBase64}"}"""
        OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { it.write(body) }

        val code = connection.responseCode
        assertTrue("服务应返回 2xx，实际 $code", code in 200..299)

        // 4) 逐帧解密并拼接
        val text = StringBuilder()
        connection.inputStream.bufferedReader().use { reader ->
            while (true) {
                val line = reader.readLine() ?: break
                val trimmed = line.trim()
                if (!trimmed.startsWith("data:")) continue
                val frame = trimmed.removePrefix("data:").trim()
                if (frame.isEmpty()) continue
                if (frame == "[DONE]") break
                if (!EnvelopeCrypto.looksLikeBase64(frame)) continue
                val json = RunCatchingFrame(sealed.aesKey, frame) ?: continue
                if (json.contains("\"error\"")) {
                    assertTrue("服务端返回错误帧：$json", false)
                }
                Regex("\"text\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                    .find(json)?.groupValues?.get(1)?.let { text.append(it) }
            }
        }

        println("真实接口返回：$text")
        assertTrue("应当解出非空文本，实际「$text」", text.isNotBlank())
    }

    /** 单独包一层，避免解密异常把测试直接打红而看不到上下文。 */
    private fun RunCatchingFrame(aesKey: ByteArray, frame: String): String? =
        runCatching { EnvelopeCrypto.openFrame(aesKey, frame) }
            .onFailure { println("解帧失败：$it") }
            .getOrNull()
}
