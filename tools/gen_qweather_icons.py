#!/usr/bin/env python3
"""
重新生成 QweatherIcons.kt（和风天气图标字体的"天气码 → 字形码点"映射）。

## 为什么要有这个脚本

那个 Kotlin 文件是**生成的**，但生成脚本当初没进仓库——于是它不可重放：
想加新码、想换图标集版本，只能靠猜当初是怎么转的。文档里写着"由官方 JSON 生成"
却找不到生成器，这本身就是个坑。补上它。

## 用法

    python3 tools/gen_qweather_icons.py

会从官方仓库拉 font/qweather-icons.json，写出
app/src/main/java/com/yisiyun/guanfeng/ui/components/QweatherIcons.kt。

字体文件本身（res/font/qweather_icons.ttf）也要一同更新时，从同一仓库的
font/fonts/ 目录取，并把 qweather-icons.json 与 ttf 视为同一版本——两者版本不一致
会导致"码点对得上、字形却画错"，而且编译不会报错。
"""

import json
import urllib.request
from pathlib import Path

REPO_RAW = "https://raw.githubusercontent.com/qwd/Icons/main"
JSON_URL = f"{REPO_RAW}/font/qweather-icons.json"
OUT = Path(__file__).resolve().parent.parent / (
    "app/src/main/java/com/yisiyun/guanfeng/ui/components/QweatherIcons.kt"
)

HEAD = '''package com.yisiyun.guanfeng.ui.components

/**
 * 和风天气官方图标字体（qweather-icons）的"天气码 → 字形码点"映射。
 *
 * ## 为什么用字体而不是图片
 *
 * 官方图标库同时提供 SVG 与字体。**在 Android 上字体是唯一正确的选择**：
 *   · 矢量，任意尺寸都清晰（手表屏幕小，位图一放大就糊）
 *   · 可用 color 直接染色——官方 SVG 用的是 fill="currentColor"，
 *     意思就是"颜色由使用者决定"，字体天然继承这个语义
 *   · 只有一个文件（约 198 KB），而逐张位图要几十上百个
 *
 * 我最初试图把 SVG 转 PNG，结果 qlmanage 渲染出来的是**白底实心图**，
 * 一染色就变成白方块——所以换成了这条正路。
 *
 * ## 这个文件是生成的，不要手改
 *
 * 用 `python3 tools/gen_qweather_icons.py` 重新生成。
 * 数据来自官方仓库 qwd/Icons 的 font/qweather-icons.json；
 * 图标与 API 的 condition.code 一一对应（3 位是天气现象、4 位是预警事件）。
 * 许可：代码 MIT、**图标 CC BY 4.0（需署名）**——署名已写在天气页底部。
 */
object QweatherIcons {

    private val codepoints: Map<String, Int> = mapOf(
'''

TAIL = '''
    )

    /** 天气码 → 字形。未知码返回 null，调用方自己决定显示什么。 */
    fun glyphFor(code: String): String? {
        val point = codepoints[code] ?: return null
        return String(Character.toChars(point))
    }
}
'''


def main() -> None:
    with urllib.request.urlopen(JSON_URL, timeout=30) as response:
        data = json.load(response)

    # 只收数字码（"100"、"1006"），丢掉 "100-fill" 这类样式变体
    entries = sorted(
        ((k, v) for k, v in data.items() if k.split("-")[0].isdigit()),
        key=lambda kv: (len(kv[0]), kv[0]),
    )
    body = "\n".join('        "%s" to 0x%04X,' % (k, v) for k, v in entries)

    OUT.write_text(HEAD + body + TAIL, encoding="utf-8")
    print(f"✓ 已生成 {OUT}:{len(entries)} 个码")

    third = sum(1 for k, _ in entries if len(k) == 3)
    fourth = sum(1 for k, _ in entries if len(k) == 4)
    print(f"  3 位（天气现象）{third} 个，4 位（预警事件）{fourth} 个，其余 {len(entries) - third - fourth} 个")


if __name__ == "__main__":
    main()
