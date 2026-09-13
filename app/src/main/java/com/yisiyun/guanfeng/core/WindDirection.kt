package com.yisiyun.guanfeng.core

/**
 * 风向写成**中文方位**。
 *
 * 和风给的是 `wind_compass`，形如 `n` / `w` / `nnw` —— 那是**开发者黑话**，
 * 手表上写 "NNW 3 级" 等于没写（2026-09-13 真机截图里就是这么显示的，主人一眼看出不对）。
 * 本项目已经为同类问题改过一次文案（`amap:4` → 高德缓存、`recalled:system:gps` → 系统卫星），
 * 规矩是同一条：**界面只写人话**。
 *
 * 用 8 方位而不是 16 方位：手表上一行只放得下两三个字，而且人报风向本来就说"西北"。
 * 优先用角度（更准）；角度缺失时退回英文缩写映射。
 */
object WindDirection {

    private val ZH_8 = listOf("北", "东北", "东", "东南", "南", "西南", "西", "西北")

    /** 英文缩写（1~3 字母，和风的写法）→ 中文 8 方位。 */
    private val FROM_COMPASS = mapOf(
        "n" to 0, "nne" to 1, "ne" to 2, "ene" to 3,
        "e" to 4, "ese" to 5, "se" to 6, "sse" to 7,
        "s" to 8, "ssw" to 9, "sw" to 10, "wsw" to 11,
        "w" to 12, "wnw" to 13, "nw" to 14, "nnw" to 15,
    )

    /**
     * @param degrees 风向角度（正北 = 0°，顺时针）。空或非法时退回 [compass]。
     * @param compass 英文缩写，如 `nnw`。
     * @return 中文方位；两者都拿不到时返回空串（调用方显示"—"）。
     */
    fun describe(degrees: String, compass: String): String {
        degrees.trim().toDoubleOrNull()?.let { deg ->
            // 每 45° 一个方位，+22.5 是为了让边界落在两个方位之间
            val index = (((deg % 360.0 + 360.0) % 360.0 + 22.5) / 45.0).toInt() % 8
            return ZH_8[index]
        }
        val key = compass.trim().lowercase()
        // 兜底也走**同一套规则**：先把 16 方位缩成角度，再按角度取 8 方位。
        // 第一版这里单独写了个 (i+1)/2 的映射，于是同一个 nnw 在两条路上给出不同答案
        // （角度说"北"、缩写说"西北"）—— 一处规则两处实现，迟早对不上。
        val sixteen = FROM_COMPASS[key] ?: return ""
        val deg = sixteen * 22.5
        return ZH_8[((deg + 22.5) / 45.0).toInt() % 8]
    }
}
