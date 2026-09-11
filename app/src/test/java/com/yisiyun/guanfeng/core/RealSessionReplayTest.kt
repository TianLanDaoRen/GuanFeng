package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 真机实测会话回放 —— 把 2026-09-11 早晨的真实测试数据固化为永久回归测试。
 *
 * 夹具内容（OWW221，主人在自家楼上实测）：
 *   09:14:54 起在 26 楼静止 → 坐电梯直达 1 楼 → 等几秒 → 坐回 26 楼 → 等几秒
 *   → 下楼 3 层（每层 17 阶，逐层停顿）→ 再上楼 3 层
 *
 * 这份数据的价值在于它同时覆盖了两个最难的情形：
 *   ① 电梯单趟真实落差约 70 米（25 层 × ~2.8 米），是最强的高度污染信号；
 *   ② 电梯匀速段没有步态、加速度也接近 0，正是状态机存在的理由。
 *
 * 断言口径全部来自实测，不是拍的：
 *   - 累计位移应识别出约 70 米的下行；
 *   - 全程不得出现任何「降」类判定（行程期间引擎必须老老实实报平稳）；
 *   - 往返结束应回到起点附近（闭合误差实测量级 0.4 米）。
 */
class RealSessionReplayTest {

    private data class Row(
        val timestampMs: Long,
        val pressureHpa: Float,
        val verticalAccel: Float,
        val steps: Int
    )

    private fun loadFixture(): List<PressureSample> {
        val stream = javaClass.getResourceAsStream("/real_session_elevator_stairs.csv")
            ?: error("找不到夹具 real_session_elevator_stairs.csv")
        val lines = stream.bufferedReader().readLines()
        val header = lines.first().split(",")
        val indexOfTimestamp = header.indexOf("timestamp_ms")
        val indexOfPressure = header.indexOf("pressure_hpa")
        val indexOfAccel = header.indexOf("vertical_accel")
        val indexOfSteps = header.indexOf("steps")

        return lines.drop(1).filter { it.isNotBlank() }.map { line ->
            val cells = line.split(",")
            PressureSample(
                timestampMs = cells[indexOfTimestamp].toLong(),
                pressureHpa = cells[indexOfPressure].toFloat(),
                verticalAccel = cells[indexOfAccel].toFloat(),
                stepsInWindow = cells[indexOfSteps].toInt()
            )
        }
    }

    @Test
    fun `真机会话回放_电梯往返与三层楼梯都必须被解耦掉`() {
        val samples = loadFixture()
        assertTrue("夹具样本数异常：${samples.size}", samples.size > 200)

        // 回放：逐点推进，模拟应用里滚动窗口的效果。
        val engine = PressureTrendEngine(windowMs = 30L * 60L * 1000L, minSamples = 8)
        var deepestMeters = 0f
        var fallingRows = 0
        var risingRows = 0
        var lastResult: TrendResult = TrendResult.insufficient(0)

        for (end in 8..samples.size) {
            val result = engine.compute(samples.subList(0, end))
            deepestMeters = minOf(deepestMeters, result.elevationMeters)
            when (result.grade) {
                TrendGrade.FALLING, TrendGrade.FALLING_FAST -> fallingRows++
                TrendGrade.RISING, TrendGrade.RISING_FAST -> risingRows++
                else -> Unit
            }
            lastResult = result
        }

        // ① 电梯下行被识别出来，且量级与 25 层楼相符
        assertTrue(
            "应识别出约 70 米下行，实测最深 ${deepestMeters} 米",
            deepestMeters < -65f
        )

        // ② 全程零「升降」误判——这是整个解耦方案的核心命题
        assertEquals("行程期间不得出现「降」类判定（朴素做法会报 400+ hPa/h）", 0, fallingRows)
        assertEquals("行程期间不得出现「升」类判定", 0, risingRows)

        // ③ 往返回到起点附近
        assertTrue(
            "往返闭合误差应接近 0，实测 ${lastResult.elevationMeters} 米",
            kotlin.math.abs(lastResult.elevationMeters) < 1.5f
        )

        // ④ 事件计数不得被平地段噪声虚高。
        // 这个计数的含义是「被判定为高度事件的样本数」，因此它应当约等于真实运动样本数：
        // 本会话按原始数据数是——电梯下行 22 + 上行 21 + 楼梯往返约 30 ≈ 75 个。
        // 加单步门限之前它会一路涨到 145（平地段噪声也在计数），所以这里卡一个区间守住回归。
        assertTrue(
            "事件计数应约等于真实运动样本数（约 75），实测 ${lastResult.elevationEvents}",
            lastResult.elevationEvents in 40..110
        )
    }
}
