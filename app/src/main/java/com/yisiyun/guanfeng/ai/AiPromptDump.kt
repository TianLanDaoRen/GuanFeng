package com.yisiyun.guanfeng.ai

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 把**实际发给 AI 的两段提示词**逐字落到 `files/ai_prompt.txt`。
 *
 * ## 为什么要有它
 *
 * 系统提示词与用户提示词原先只活在内存里：`AiDigest.buildSystemInstruction()` 造出来、
 * 直接塞进请求体发走，谁都看不见。而"发给模型的到底是什么"恰恰是排查
 * "AI 为什么这么答"的第一手证据——事后读代码复现，复现的是我以为发了什么，
 * 不是当时真的发出去的东西。
 *
 * ## 三个刻意的选择
 *
 * ① **每次覆盖，不追加**：这是"这一次请求长什么样"的快照，不是日志。
 *    追加会让文件无限长大，而手表取文件要先上充电底座，越长越难读。
 * ② **逐字原样**：不截断、不美化、不省略。截断过的提示词会让人把
 *    "我们根本没发这段"与"这段被省略号吃掉了"混在一起——证据一旦经过加工就不再是证据。
 * ③ **写失败不许影响报告**：落盘纯属旁路，用 `runCatching` 包住，失败只留一行 `Log.w`。
 *    报告生成是用户在界面上按了按钮、正等着的东西，不能因为磁盘写不进去而失败。
 *
 * 不需要额外的开关文件：只有用户显式点「生成 AI 报告」时才会写一次，
 * 频率低到不值得再加一个开关；不想要这个文件就删掉它。
 */
object AiPromptDump {

    /** 直接落在外层 files 目录：`adb pull` 不需要 run-as 就能取。 */
    const val FILE_NAME = "ai_prompt.txt"

    private const val TAG = "GuanFengAi"

    private const val SYSTEM_HEADER = "=== system ==="
    private const val USER_HEADER = "=== user ==="

    /**
     * 拼出落盘全文。**纯函数**（不碰 Context、不碰 Log），单测直接调——
     * 这里唯一要保证的事情就是"逐字"，而只靠肉眼是保证不了的。
     *
     * 格式：一行时间戳，然后 system 段、user 段，各段内容原样。
     * 末尾补一个换行，否则用文本工具看时最后一行会和提示符黏在一起。
     */
    fun render(nowMs: Long, systemPrompt: String, userContent: String): String = buildString {
        append(stamp(nowMs)).append('\n')
        appendSection(SYSTEM_HEADER, systemPrompt)
        appendSection(USER_HEADER, userContent)
    }

    /**
     * 写文件。**任何异常都不许冒出去**：调用方在"生成报告"的关键路径上，
     * 它只该因为网络/服务端失败，不该因为磁盘失败。
     */
    fun write(context: Context, nowMs: Long, systemPrompt: String, userContent: String) {
        runCatching {
            val dir = context.getExternalFilesDir(null) ?: context.filesDir
            val file = File(dir, FILE_NAME)
            file.writeText(render(nowMs, systemPrompt, userContent), Charsets.UTF_8)
            Log.i(TAG, "已落盘发给 AI 的提示词：${file.absolutePath}（${file.length()} 字节）")
        }.onFailure { Log.w(TAG, "提示词落盘失败（不影响本次请求）: $it") }
    }

    private fun StringBuilder.appendSection(header: String, body: String) {
        append(header).append('\n')
        append(body)
        if (!body.endsWith("\n")) append('\n')
    }

    /**
     * 时间戳按 `Locale.US` 格式化。
     *
     * 与项目里 CSV 的规矩同源（见 `log/CsvNumbers`）：默认 Locale 在小数点/数字分组上
     * 因地而异，凡是"给机器看的"格式都不许跟着系统语言走。
     * 每次都新建 formatter，而不是共享一个静态实例——SimpleDateFormat 不是线程安全的，
     * 而这里一次报告只调一次，省下那点分配换不来任何东西。
     */
    private fun stamp(nowMs: Long): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US).format(Date(nowMs))
}
