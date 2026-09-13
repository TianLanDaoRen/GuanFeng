package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 风向必须写成中文方位。
 *
 * 真机截图里显示的是 "NNW 3 级" —— 英文缩写是开发者黑话，
 * 与之前改过的 `amap:4`、`recalled:system:gps` 属于同一类问题。
 */
class WindDirectionTest {

    @Test
    fun `角度优先_落在中国人报的八方位上`() {
        assertEquals("北", WindDirection.describe("0", ""))
        assertEquals("东", WindDirection.describe("90", ""))
        assertEquals("南", WindDirection.describe("180", ""))
        assertEquals("西", WindDirection.describe("270", ""))
        // 真机 18:33 那条：wind_degree=206 → 西南（截图里写的是 NNW ✗）
        assertEquals("西南", WindDirection.describe("206", "nnw"))
        assertEquals("西北", WindDirection.describe("315", ""))
        assertEquals("东南", WindDirection.describe("135", ""))
    }

    @Test
    fun `边界角度归到更近的那个方位`() {
        assertEquals("北", WindDirection.describe("22", ""))      // 22.5 以下算北
        assertEquals("东北", WindDirection.describe("23", ""))    // 过了 22.5 算东北
        assertEquals("北", WindDirection.describe("338", ""))     // 337.5 以上回到北
        assertEquals("西北", WindDirection.describe("337", ""))
        assertEquals("北", WindDirection.describe("360", ""))     // 360 就是北
    }

    @Test
    fun `没有角度时退回英文缩写映射`() {
        assertEquals("北", WindDirection.describe("", "n"))
        assertEquals("北", WindDirection.describe("", "N"))       // 大小写不敏感
        // nnw = 337.5°，与"北"和"西北"各差 22.5°；两条路（角度/缩写）必须给同一个答案
        assertEquals("北", WindDirection.describe("", "nnw"))
        assertEquals("西", WindDirection.describe("", "w"))
    }

    @Test
    fun `两个都拿不到时给空串_由调用方显示破折号`() {
        assertEquals("", WindDirection.describe("", ""))
        assertEquals("", WindDirection.describe("abc", "xyz"))
    }
}
