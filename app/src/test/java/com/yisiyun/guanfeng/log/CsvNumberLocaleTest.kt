package com.yisiyun.guanfeng.log

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 落成 CSV 的数值**必须**固定 Locale.US —— 静态守卫。
 *
 * ## 为什么写这条
 *
 * CSV 的列分隔符是半角逗号。默认 Locale 在德语、法语、俄语等区域会把小数点写成逗号，
 * 于是 `1013.25` 变成 `1013,25`：那一行凭空多出一列，**整行静默错位**，
 * 而且中文手表上永远复现不了——只有换设备或改系统语言才会现形。
 *
 * `QweatherLogger` 早就为它固定了 Locale 并写了用例，但同一条规矩当时只覆盖了和风那几个文件。
 * 2026-09-12 全量核查时发现：采样文件、小时归档、天气实况、打卡、耗电
 * 一共 **42 处**还在用默认 Locale。全部改成了 [csvNum]。
 *
 * 靠记性不如靠测试：以后谁再写 `"%.2f".format(x)`，这里会带着行号把它拦下来。
 */
class CsvNumberLocaleTest {

    /** 需要守住的目录：凡是写 CSV 或读 CSV 的地方。 */
    private val watched = listOf(
        "src/main/java/com/yisiyun/guanfeng/log",
        "src/main/java/com/yisiyun/guanfeng/data",
    )

    /** 浮点格式化字面量：`"%.2f"` / `"%.0f"` / `"%.3f"` …… */
    private val floatFormat = Regex("""\d?"%\.[0-9]f""")

    @Test
    fun `CSV 模块里不得出现未固定 Locale 的浮点格式化`() {
        val roots = watched.map { File(it) }
        roots.forEach { assertTrue("找不到目录：${it.absolutePath}", it.isDirectory) }

        val offenders = roots
            .flatMap { it.walkTopDown().filter { f -> f.isFile && f.extension == "kt" }.toList() }
            .flatMap { file ->
                file.readLines().withIndex().mapNotNull { (index, line) ->
                    val trimmed = line.trimStart()
                    val isComment = trimmed.startsWith("*") ||
                        trimmed.startsWith("//") ||
                        trimmed.startsWith("/*")
                    if (isComment || !floatFormat.containsMatchIn(line)) return@mapNotNull null
                    // 放行条件：这一行显式写了 Locale.US，或用了统一的 csvNum
                    val safe = line.contains("Locale.US") || line.contains("csvNum(")
                    if (safe) null else "${file.name}:${index + 1}  ${line.trim()}"
                }
            }
            .toList()

        assertTrue(
            "写进 CSV 的数值必须用 csvNum(...)（内部固定 Locale.US），" +
                "否则在把小数点写成逗号的区域里，整行会被多切出一列、静默错位。\n" +
                offenders.joinToString("\n"),
            offenders.isEmpty(),
        )
    }
}
