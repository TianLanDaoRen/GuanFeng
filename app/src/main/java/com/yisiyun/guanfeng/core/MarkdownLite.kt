package com.yisiyun.guanfeng.core

/**
 * 极简 Markdown 解析。
 *
 * 为什么需要它：接口返回的是**模型原生输出**（含 Markdown 符号），
 * 而手表上没有渲染器——不处理就会出现一排 `**` 与 `---` 当正文显示。
 *
 * 为什么不引第三方渲染库：189dp 宽的屏幕上，真正会出现的语法就那几种
 * （粗体、分隔线、短标题、项目符号），自己解析几十行就够，
 * 没必要为此背一个依赖；而表格/代码块在腕上本来就不该出现，
 * 提示词里已经禁止模型输出它们。
 *
 * 另外要剥掉**服务端追加的标语帧**——那不是模型写的，是后端拼的，
 * 格式固定：`\n\n---\n\n✨  **Powered by 卦灵AI · 融古通今，智解天机**  `。
 */
object MarkdownLite {

    private val RULE_PATTERN = Regex("^([-*_])\\1{2,}$")
    private val ORDERED_ITEM = Regex("^\\d+[.、]\\s*")
    private val SLOGAN_MARKER = "Powered by"

    sealed interface Block {
        data class Heading(val level: Int, val text: String) : Block
        data class Bullet(val text: String) : Block
        data class Paragraph(val text: String) : Block
        data object Rule : Block
    }

    /** 一段行内文本，标出它是否是 `**加粗**` 的内容。 */
    data class Inline(val text: String, val bold: Boolean)

    /**
     * 切除服务端标语。做法是从后往前找标语行，连同它上方紧邻的空行与分隔线一起去掉——
     * 不写死那串精确文本，这样即使标语前的装饰（✨、空格）微调也能吃掉。
     */
    fun stripSlogan(raw: String): String {
        val lines = raw.lines().toMutableList()
        val sloganIndex = lines.indexOfLast { it.contains(SLOGAN_MARKER) }
        if (sloganIndex < 0) return raw.trimEnd()
        var cutFrom = sloganIndex
        while (cutFrom > 0 && lines[cutFrom - 1].isBlank()) cutFrom--
        if (cutFrom > 0 && RULE_PATTERN.matches(lines[cutFrom - 1].trim())) cutFrom--
        while (cutFrom > 0 && lines[cutFrom - 1].isBlank()) cutFrom--
        return lines.subList(0, cutFrom).joinToString("\n").trimEnd()
    }

    fun parse(raw: String): List<Block> {
        val text = stripSlogan(raw)
        val blocks = mutableListOf<Block>()
        val paragraph = StringBuilder()

        fun flushParagraph() {
            if (paragraph.isNotEmpty()) {
                blocks += Block.Paragraph(paragraph.toString())
                paragraph.clear()
            }
        }

        text.lines().forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.isEmpty() -> flushParagraph()

                RULE_PATTERN.matches(trimmed) -> {
                    flushParagraph()
                    blocks += Block.Rule
                }

                trimmed.startsWith("#") -> {
                    flushParagraph()
                    val level = trimmed.takeWhile { it == '#' }.length.coerceIn(1, 3)
                    blocks += Block.Heading(level, trimmed.trimStart('#').trim())
                }

                trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("+ ") -> {
                    flushParagraph()
                    blocks += Block.Bullet(trimmed.drop(2).trim())
                }

                ORDERED_ITEM.containsMatchIn(trimmed) -> {
                    flushParagraph()
                    blocks += Block.Bullet(ORDERED_ITEM.replaceFirst(trimmed, "").trim())
                }

                else -> appendLine(paragraph, trimmed)
            }
        }
        flushParagraph()
        return blocks
    }

    /** 把 `**粗体**` 拆成若干段；未闭合的 `**` 原样保留，不当成错误。 */
    fun inline(text: String): List<Inline> {
        val result = mutableListOf<Inline>()
        var index = 0
        while (index < text.length) {
            val start = text.indexOf("**", index)
            if (start < 0) {
                result += Inline(text.substring(index), false)
                break
            }
            val end = text.indexOf("**", start + 2)
            if (end < 0) {
                result += Inline(text.substring(index), false)
                break
            }
            if (start > index) result += Inline(text.substring(index, start), false)
            result += Inline(text.substring(start + 2, end), true)
            index = end + 2
        }
        return result.filter { it.text.isNotEmpty() }
    }

    /** 拼接被硬换行拆开的段落：中文之间不补空格，英文/数字之间补。 */
    private fun appendLine(paragraph: StringBuilder, line: String) {
        if (paragraph.isEmpty()) {
            paragraph.append(line)
            return
        }
        val previous = paragraph.last()
        val next = line.firstOrNull() ?: return
        val needsSpace = previous.code < 128 && next.code < 128
        if (needsSpace) paragraph.append(' ')
        paragraph.append(line)
    }
}
