package com.yisiyun.guanfeng.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 白色文字只有三档明度（见 `ui/theme/Ink.kt`）。
 *
 * ## 为什么用测试来守
 *
 * "别写第五种灰"这种约定，靠记性是记不住的：这份源码里曾经同时躺着
 * `#8A8A8A`、`#9A9A9A`、`#7A7A7A`、`#6E6E6E`、`#5E5E5E`、`#5A5A5A`、`#4E4E4E`
 * 七种中性灰——它们谁也不比谁差一点，但合在一起就是"每个页面的字都不一样亮"，
 * 而手表上最该看清的东西恰好被压在最暗的那档里。
 *
 * ## 判定规则
 *
 * 扫界面源码里的颜色字面量，凡**中性灰**（R=G=B）：
 *   · `#40` 以下 —— 放行（那是背景、分隔线、底纹、指示点）
 *   · 恰好是 `#D0D0D0` / `#DCDCDC` / `#E8E8E8` 且完全不透明 —— 放行（三档本体）
 *   · 其它一律违规（包括纯白 `#FFFFFF`：纯白不是第四档，它就是"没按守则选"）
 *
 * 用**字节精确**的规则而不是"看起来差不多"：规则的边界要么能算，要么就会被绕过去。
 */
class WhiteTextTierTest {

    /** 低/中/高三档的 RGB 字节（与 ui/theme/Ink.kt 一一对应）。 */
    private val tiers = setOf("D0", "DC", "E8")

    /** 中性灰（含 alpha 字节）的字面量。 */
    private val neutralGrey = Regex("0x([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})(?:\\2){2}")

    private val namedGreys = Regex("Color\\.(White|LightGray|Gray|DarkGray)\\b")

    @Test
    fun `界面里的白色文字只能落在三档明度里`() {
        val uiRoot = File("src/main/java/com/yisiyun/guanfeng/ui")
        assertTrue("找不到界面源码目录：${uiRoot.absolutePath}", uiRoot.isDirectory)

        val offenders = uiRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .flatMap { file ->
                file.readLines().withIndex().mapNotNull { (index, line) ->
                    val trimmed = line.trimStart()
                    val isComment = trimmed.startsWith("*") ||
                        trimmed.startsWith("//") ||
                        trimmed.startsWith("/*")
                    if (isComment) return@mapNotNull null

                    namedGreys.find(line)?.let {
                        return@mapNotNull "${file.name}:${index + 1}  ${it.value}  ${line.trim()}"
                    }
                    neutralGrey.findAll(line).forEach { match ->
                        val alpha = match.groupValues[1].uppercase()
                        val grey = match.groupValues[2].uppercase()
                        val dark = grey.toInt(16) < 0x40
                        val isTier = alpha == "FF" && grey in tiers
                        if (!dark && !isTier) {
                            return@mapNotNull "${file.name}:${index + 1}  ${match.value}  ${line.trim()}"
                        }
                    }
                    null
                }
            }
            .toList()

        assertTrue(
            "白色文字的明度只允许三档：#D0D0D0（低 · 说明/来源/单位）、" +
                "#DCDCDC（中 · 标签/副标题）、#E8E8E8（高 · 数值/正文），" +
                "见 ui/theme/Ink.kt。深于 #40 的灰是背景与线条，不在此限。\n" +
                "大部分白字应该用中或高；只有不重要、说明、信息类的文字才用低。\n" +
                offenders.joinToString("\n"),
            offenders.isEmpty(),
        )
    }

    @Test
    fun `三档的颜色值本身必须与守则一致`() {
        val ink = File("src/main/java/com/yisiyun/guanfeng/ui/theme/Ink.kt")
        assertTrue("找不到调色板文件：${ink.absolutePath}", ink.isFile)
        val text = ink.readText()
        listOf("INK_LOW" to "0xFFD0D0D0", "INK_MID" to "0xFFDCDCDC", "INK_HIGH" to "0xFFE8E8E8")
            .forEach { (name, value) ->
                assertTrue(
                    "Ink.kt 里 $name 必须是 $value：三档的色值一旦被随手改掉，" +
                        "全应用的可读性基准就跟着漂了",
                    text.contains("val $name = Color($value)"),
                )
            }
    }
}
