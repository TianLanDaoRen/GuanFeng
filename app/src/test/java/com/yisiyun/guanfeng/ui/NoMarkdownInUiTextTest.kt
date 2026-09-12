package com.yisiyun.guanfeng.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 界面文案里不许出现 markdown 标记。
 *
 * ## 为什么值得专门写一条测试
 *
 * 因为我在同一个坑里**摔了两次**：
 *   · 一次写了带星号的「这个要见到天空」，表上原样显示星号
 *   · 修完之后又写了一次带星号的「连不连上热点都行」
 *
 * Compose 的 `Text` 不渲染 markdown——星号会**原样**打到手表屏幕上。
 * 这不是风格问题，是用户一眼能看见的缺陷，而我显然记不住。
 * 靠记性不如靠测试：这条会在跑测试时就把它拦下来。
 *
 * ## 怎么区分"文案"与"注释"
 *
 * 正则要求星号出现在**成对的引号之间**，所以 KDoc 里不带引号的加粗写法不会被误判；
 * 再跳过整行注释，避免行尾注释里提到引号加粗这种极少见的误报。
 */
class NoMarkdownInUiTextTest {

    private val markdownInsideString = Regex("\"[^\"]*\\*\\*[^\"]*\"")

    @Test
    fun `界面源码里不得出现带markdown的字符串`() {
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
                    if (!isComment && markdownInsideString.containsMatchIn(line)) {
                        "${file.name}:${index + 1}  ${line.trim()}"
                    } else {
                        null
                    }
                }
            }
            .toList()

        assertTrue(
            "界面文案里出现了 markdown 星号。Compose 不会渲染它们，会原样显示在手表上：\n" +
                offenders.joinToString("\n"),
            offenders.isEmpty(),
        )
    }
}
