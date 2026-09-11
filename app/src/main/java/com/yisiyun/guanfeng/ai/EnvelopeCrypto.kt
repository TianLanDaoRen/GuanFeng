package com.yisiyun.guanfeng.ai

import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec

/**
 * 卦灵公共 AI 流的信封加解密。
 *
 * **刻意做成纯 JVM（不引 android.util.Base64 / org.json）**：加密是这条链路上
 * 最容易出错、也最难在手表上调试的一环，所以把它隔离出来，用本地往返单测证死，
 * 而不是等真机上报错再猜。
 *
 * 协议（移植自对方前端实现，已核对其 crypto.subtle 调用）：
 *   · 一次性 AES-256 密钥 + 12 字节 IV，AES-GCM 加密业务 JSON；
 *     载荷 = IV ‖ 密文（WebCrypto 与 Java 都把 16 字节认证 tag 拼在密文尾部）
 *   · 用 RSA-OAEP-SHA256 加密那把 AES 密钥作为 sign
 *
 * 移植时两个必须注意的点：
 *  1. Java 的 "OAEPWithSHA-256AndMGF1Padding" 其 MGF1 默认摘要可能是 SHA-1，
 *     而 WebCrypto 的 RSA-OAEP 用的是与 hash 相同的 SHA-256；
 *     必须显式传 OAEPParameterSpec，否则服务端解不开。
 *  2. GCM 的 tag 长度要显式声明为 128 位。
 */
object EnvelopeCrypto {

    private const val IV_LENGTH = 12
    private const val AES_KEY_LENGTH = 32
    private const val GCM_TAG_BITS = 128

    data class Sealed(val signBase64: String, val payloadBase64: String, val aesKey: ByteArray)

    /** 解析 SPKI/PEM 格式的 RSA 公钥（`-----BEGIN PUBLIC KEY-----` 包裹、可含换行）。 */
    fun parsePublicKey(pem: String): PublicKey {
        val base64 = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace(Regex("\\s"), "")
        require(base64.isNotEmpty()) { "公钥内容为空" }
        val spec = X509EncodedKeySpec(Base64.getDecoder().decode(base64))
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }

    /** 组信封：sign = RSA-OAEP-SHA256(AES 密钥)，payload = base64(IV ‖ AES-GCM(业务 JSON))。 */
    fun seal(publicKey: PublicKey, businessJson: String): Sealed {
        val aesKey = ByteArray(AES_KEY_LENGTH).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_LENGTH).also { SecureRandom().nextBytes(it) }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(aesKey, "AES"), GCMParameterSpec(GCM_TAG_BITS, iv))
        val ciphertext = cipher.doFinal(businessJson.toByteArray(Charsets.UTF_8))

        val rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        rsa.init(
            Cipher.ENCRYPT_MODE,
            publicKey,
            OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT),
        )
        val sealedKey = rsa.doFinal(aesKey)

        val encoder = Base64.getEncoder()
        return Sealed(
            signBase64 = encoder.encodeToString(sealedKey),
            payloadBase64 = encoder.encodeToString(iv + ciphertext),
            aesKey = aesKey,
        )
    }

    /** 解开一帧响应：base64 → 前 12 字节 IV → 用同一把 AES 解出明文。 */
    fun openFrame(aesKey: ByteArray, frameBase64: String): String {
        val raw = Base64.getMimeDecoder().decode(frameBase64)
        require(raw.size > IV_LENGTH) { "帧长度异常：${raw.size}" }
        val iv = raw.copyOfRange(0, IV_LENGTH)
        val ciphertext = raw.copyOfRange(IV_LENGTH, raw.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(aesKey, "AES"), GCMParameterSpec(GCM_TAG_BITS, iv))
        return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
    }

    /** 帧是否是像样的 base64（服务端可能夹杂非加密行，按参考实现跳过即可）。 */
    fun looksLikeBase64(value: String): Boolean =
        value.isNotEmpty() && value.all { it.isLetterOrDigit() || it == '+' || it == '/' || it == '=' }
}
