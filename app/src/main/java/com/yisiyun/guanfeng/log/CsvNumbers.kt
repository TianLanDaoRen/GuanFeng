package com.yisiyun.guanfeng.log

import java.util.Locale

/**
 * 写进 CSV 的数值**一律**用 `Locale.US` 格式化。
 *
 * ## 为什么值得单开一个文件
 *
 * CSV 的列分隔符是**半角逗号**，而默认 Locale 在德语、法语、俄语等区域
 * 会把小数点写成**逗号**：`1013.25` 变成 `1013,25`，那一行凭空多出一列，
 * 整行从此**错位**——而且这类错误在中文手表上**永远复现不了**，
 * 要么换台设备，要么等你哪天在系统里改了语言。
 *
 * QweatherLogger 早就为它固定了 Locale 并写了测试（`QweatherLoggerTest` 里那条德语区域的用例），
 * 但同一条规矩当时**只覆盖了和风那几个文件**：采样文件、小时归档、天气实况、打卡、耗电
 * 都还在用默认 Locale。这个文件把规矩补齐，也让"新写的 CSV 忘了固定 Locale"
 * 能被 [CsvNumberLocaleTest] 在跑测试时拦下。
 *
 * 空值一律输出**空串**，不输出 `0`：`0` 是一个真实的读数，
 * 而"上游没给这个值"和"这个值是零"在事后统计里必须能区分开。
 */
internal fun csvNum(value: Double?, digits: Int = 2): String =
    if (value == null || value.isNaN() || value.isInfinite()) {
        ""
    } else {
        String.format(Locale.US, "%.${digits}f", value)
    }

/**
 * 与 [csvNum] 同理，但保留 `Float` 类型本身。
 *
 * **不要在这里 `toDouble()` 再格式化**：`Float` 转 `Double` 会带出二进制尾数
 * （`1001.40f` 转成 `1001.4000244140625`），保留多少位就变成了另一场赌博。
 */
internal fun csvNum(value: Float?, digits: Int = 2): String =
    if (value == null || value.isNaN() || value.isInfinite()) {
        ""
    } else {
        String.format(Locale.US, "%.${digits}f", value)
    }
