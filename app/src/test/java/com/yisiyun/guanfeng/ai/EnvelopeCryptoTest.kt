package com.yisiyun.guanfeng.ai

import java.security.KeyPairGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 信封加解密的本地往返测试。
 *
 * 这套协议最危险的地方在于「本地看着对、服务端解不开」——
 * 尤其是 OAEP 的 MGF1 摘要与 GCM 的 tag 长度。这里用
 * 「自己生成密钥对 → 自己封 → 自己解」把算法链路钉住，
 * 不依赖网络，也不依赖 Android 运行时。
 */
class EnvelopeCryptoTest {

    private fun newKeyPair() =
        KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()

    @Test
    fun `封装的载荷能用同一把 AES 解回原文`() {
        val keyPair = newKeyPair()
        val business = """{"meta":{"type":"chat"},"gemini_req":{"contents":[]}}"""

        val sealed = EnvelopeCrypto.seal(keyPair.public, business)

        // 服务端收到的是 base64(IV ‖ 密文)；这里用同一把 AES 解回来验证链路
        val opened = EnvelopeCrypto.openFrame(sealed.aesKey, sealed.payloadBase64)
        assertEquals(business, opened)
    }

    @Test
    fun `sign 是 RSA 密文_长度符合 2048 位密钥`() {
        val keyPair = newKeyPair()

        val sealed = EnvelopeCrypto.seal(keyPair.public, "{}")

        // RSA-2048 的密文恒为 256 字节
        val signBytes = java.util.Base64.getDecoder().decode(sealed.signBase64)
        assertEquals(256, signBytes.size)
        assertEquals(32, sealed.aesKey.size)
    }

    @Test
    fun `两把不同的 AES 密钥无法互相解开`() {
        val keyPair = newKeyPair()
        val first = EnvelopeCrypto.seal(keyPair.public, """{"a":1}""")
        val second = EnvelopeCrypto.seal(keyPair.public, """{"b":2}""")

        assertFalse("密钥不同时必须解不开", runCatching {
            EnvelopeCrypto.openFrame(second.aesKey, first.payloadBase64)
        }.isSuccess)
    }

    @Test
    fun `每次封装的 IV 都不同_不是固定值`() {
        val keyPair = newKeyPair()

        val a = EnvelopeCrypto.seal(keyPair.public, "{}")
        val b = EnvelopeCrypto.seal(keyPair.public, "{}")

        // 明文相同但 IV 随机 → 载荷必然不同
        assertTrue("IV 必须每次随机", a.payloadBase64 != b.payloadBase64)
    }

    @Test
    fun `公钥解析能吃下带换行的标准 PEM`() {
        val keyPair = newKeyPair()
        val encoded = java.util.Base64.getEncoder().encodeToString(keyPair.public.encoded)
        val pem = "-----BEGIN PUBLIC KEY-----\n" +
            encoded.chunked(64).joinToString("\n") +
            "\n-----END PUBLIC KEY-----\n"

        val parsed = EnvelopeCrypto.parsePublicKey(pem)

        assertEquals("RSA", parsed.algorithm)
        // 解析出来的公钥能用于封装，说明确实是同一把钥匙
        val sealed = EnvelopeCrypto.seal(parsed, "{}")
        assertTrue(sealed.signBase64.isNotEmpty())
    }

    @Test
    fun `base64 形状判定用于跳过非加密帧`() {
        assertTrue(EnvelopeCrypto.looksLikeBase64("QUJDRA=="))
        assertFalse(EnvelopeCrypto.looksLikeBase64(""))
        assertFalse(EnvelopeCrypto.looksLikeBase64("Powered by 卦灵AI"))
    }
}
