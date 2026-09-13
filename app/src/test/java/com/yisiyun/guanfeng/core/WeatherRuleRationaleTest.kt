package com.yisiyun.guanfeng.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 判定依据的文案必须**真的被格式化过**，不能把 `%.1f` 原样打到手表上。
 *
 * ## 为什么值得单独守
 *
 * 这条坑本项目踩过 **两次**：`"A" + "B".format(...)` 里 `.format` 只作用于
 * 「紧邻的那个字面量」，于是 A 中的占位符原样显示——界面出现
 * "路径 %.1f hPa 而净变 %.1f hPa"。第一次修的是净变幅那条，第二次（2026-09-12 夜）
 * 才发现振荡那条也中了同一个坑。
 *
 * 静态扫源码很难判"这个 .format 有没有漏"，所以这里用**行为**守：
 * 把各分支都造出来跑一遍，断言文案里不许残留 `%`，且必须真的写进了数字。
 */
class WeatherRuleRationaleTest {

    private val engine = PressureTrendEngine(windowMs = 3L * 60L * 60L * 1000L, minSamples = 20)

    private fun samples(count: Int = 400, shape: (Int) -> Float): List<PressureSample> =
        (0 until count).map { index -> PressureSample(index * 15_000L, shape(index)) }

    /** 收集若干典型情形的判定依据，逐一检查占位符是否被吃掉。 */
    private fun rationales(): List<Pair<String, String>> {
        val cases = listOf(
            // 来回振荡：先升后降、幅度相当 → 路径远大于净变，应给出"振荡"结论
            "来回振荡" to samples { i -> 1008f + kotlin.math.sin(i / 12.0).toFloat() * 1.2f },
            // 急降后转平
            "急降后转平" to samples { i -> if (i < 130) 1010f - i * (5f / 130f) else 1005f },
            // 稳定缓升
            "稳定缓升" to samples { i -> 1000f + i * 0.004f },
            // 一路平稳
            "一路平稳" to samples { _ -> 1005f },
            // 净变够大但曲线不直：先降、中途抬一下、再降 → 专门打"R² 低"那条分支
            "净变大但曲线不直" to samples { i ->
                when {
                    i < 120 -> 1010f - i * (2f / 120f)
                    i < 200 -> 1008f + (i - 120) * (0.6f / 80f)
                    else -> 1008.6f - (i - 200) * (2.5f / 200f)
                }
            },
            // 稳定缓降（把 3 小时变压那几条分支也走到）
            "稳定缓降" to samples { i -> 1012f - i * 0.012f },
            // 急降
            "急降" to samples { i -> 1012f - i * 0.03f },
        )
        return cases.map { (name, data) ->
            name to WeatherRule.assess(engine.compute(data), 0f, null).rationale
        }
    }

    /** 残留的格式占位符：`%.1f`、`%d`、`%.0f%%` 里的前一半。
     *  注意**不能简单地禁 `%`**：`效率 5%` 里的百分号是合法输出
     *  （格式串里的 `%%` 本来就该渲染成一个 %）——第一版守卫就是这么误报的。 */
    private val leftoverSpecifier = Regex("%[.0-9]*[a-zA-Z]")

    @Test
    fun `各分支的判定依据都不许残留百分号占位符`() {
        val bad = rationales().filter { (_, text) -> leftoverSpecifier.containsMatchIn(text) }
        assertFalse(
            "文案里残留了未被 format 吃掉的占位符（.format 只绑紧邻字面量，整段拼接要加括号）：\n" +
                bad.joinToString("\n") { "  ${it.first}: ${it.second}" },
            bad.isNotEmpty(),
        )
    }

    @Test
    fun `判定依据必须真的写进了数字`() {
        rationales().forEach { (name, text) ->
            assertTrue("$name 的依据里没有数字，可能整段都没被格式化：$text", text.any { it.isDigit() })
        }
    }
}
