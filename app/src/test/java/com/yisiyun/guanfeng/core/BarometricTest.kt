package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 气压高度换算的标定测试。
 *
 * ## 真值从哪来
 *
 * 2026-09-13 真机实测：主人从 26 楼乘电梯到 -1 楼，26 层 × 2.8 米 ≈ **72.8 米**，
 * 手表记录的对应气压落差为 **8.69 hPa**（原始气压 1001.79 → 1010.48）。
 * 这是一个**已知真值**的样本，所以能直接拿来钉住换算公式。
 *
 * 旧实现用固定系数 `0.12 hPa/米`（线性），在同一段给出 72.4 米；换成本文件的气压高度公式后
 * 是 72.8 米。线性近似在几十米内够用，但"26 层楼"这种量级就已经能看出系统偏差。
 */
class BarometricTest {

    private val groundHpa = 1001.79f      // 26 楼
    private val bottomHpa = 1010.48f      // -1 楼（实测）
    private val trueHeight = 72.8f        // 26 层 × 2.8 米

    @Test
    fun `26 层电梯的实测样本必须命中真值`() {
        val h = Barometric.heightMeters(groundHpa, bottomHpa)
        assertEquals("该段实测高度应≈72.8 米（线性系数给 72.4，物理海平面值给 74.1）", trueHeight, h, 0.6f)
    }

    @Test
    fun `线性系数在这段上确实差得更多`() {
        val linear = (bottomHpa - groundHpa) / 0.12f
        val baro = Barometric.heightMeters(groundHpa, bottomHpa)
        assertTrue(
            "气压高度公式应比线性系数更接近真值（线性 ${"%.1f".format(linear)}、公式 ${"%.1f".format(baro)}）",
            kotlin.math.abs(baro - trueHeight) < kotlin.math.abs(linear - trueHeight),
        )
    }

    @Test
    fun `低压基准下的偏差更大_所以不能写死系数`() {
        // 山城/高层住宅的本地气压可能低到 930 hPa；同样的 8.69 hPa 落差，
        // 线性系数仍然给 72.4 米，而真实高度比低海拔处更高（尺度高度变小）
        val low = Barometric.heightMeters(930f, 938.69f)
        assertTrue("低气压基准下同样落差应给出更大的高度（实际 ${"%.1f".format(low)} 米）", low > 73.5f)
    }

    @Test
    fun `正负与零点自洽`() {
        assertEquals("同一气压应为 0", 0f, Barometric.heightMeters(1005f, 1005f), 0.01f)
        assertTrue("气压更低 = 升高 = 正", Barometric.heightMeters(1000f, 1005f) > 0f)
        assertTrue("气压更高 = 下降 = 负", Barometric.heightMeters(1010f, 1005f) < 0f)
        // 偏移换算：扣掉 8.69 hPa ≈ 下降 72.8 米。
        // 参考气压要取**当时的实际读数**（下降段＝底部 1010.48），不是起始值 ——
        // 取起始值会得到 73.4 米，差 0.6 米，那是"参考面本身气压不同"带来的固有差别。
        assertEquals(-trueHeight, Barometric.offsetToMeters(8.69f, bottomHpa), 0.6f)
    }

    @Test
    fun `逆运算与正运算互逆`() {
        val d = Barometric.pressureDeltaHpa(72.8f, bottomHpa)
        assertEquals("72.8 米 对应 8.69 hPa 左右", 8.69f, d, 0.05f)
        assertEquals(72.8f, Barometric.heightMeters(bottomHpa - d, bottomHpa), 0.1f)
    }
}
