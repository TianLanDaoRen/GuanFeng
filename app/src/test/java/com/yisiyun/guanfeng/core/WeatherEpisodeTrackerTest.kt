package com.yisiyun.guanfeng.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 天气过程状态机的单测。
 *
 * 这些用例对应主人提出的三种情形：
 *   ① 气压持续下降 → 进入「可能下雨」；
 *   ② 气压回升 → 解除；
 *   ③ **雨时下时停** → 不能来回翻状态（这是迟滞存在的理由）。
 * 另加一条最重要的：**过程持续很久也不能因为"时间过了"而忘掉**——
 * 那正是滑动窗口方案的原罪。
 */
class WeatherEpisodeTrackerTest {

    private val minute = 60_000L

    private fun feed(tracker: WeatherEpisodeTracker, values: List<Float>, startMinutes: Int = 0): WeatherEpisode {
        var state = tracker.current()
        values.forEachIndexed { index, value ->
            state = tracker.add((startMinutes + index).toLong() * minute, value)
        }
        return state
    }

    @Test
    fun `持续下降越过阈值即进入过程`() {
        val tracker = WeatherEpisodeTracker()

        val state = feed(tracker, listOf(1010f, 1009.5f, 1009f, 1008.4f))

        assertTrue("累计降 1.6 hPa 应当进入过程", state.active)
        assertEquals(-1.6f, state.dropHpa, 0.05f)
    }

    @Test
    fun `小幅抖动不得进入过程`() {
        val tracker = WeatherEpisodeTracker()

        val state = feed(tracker, listOf(1010f, 1009.8f, 1009.9f, 1009.7f, 1009.8f))

        assertFalse("降幅不到 1.5 hPa 不该报警", state.active)
    }

    @Test
    fun `自最低点回升一点即解除`() {
        val tracker = WeatherEpisodeTracker()
        feed(tracker, listOf(1010f, 1008f, 1007f))
        assertTrue(tracker.current().active)

        val state = feed(tracker, listOf(1008.6f), startMinutes = 10)

        assertFalse("回升 1.6 hPa 表示低压已过", state.active)
        assertNotNull("应当记下解除时的回升幅度", state.clearedRiseHpa)
        assertEquals(1.6f, state.clearedRiseHpa!!, 0.05f)
    }

    @Test
    fun `过程持续很久也不会因为时间流逝而忘掉`() {
        // 这正是滑动窗口方案的原罪：降完一直下着雨超过 6 小时，窗口一滑就忘。
        val tracker = WeatherEpisodeTracker()
        feed(tracker, listOf(1010f, 1008f))
        assertTrue(tracker.current().active)

        // 之后 12 小时气压基本不动（雨一直下着），模拟时间流逝
        var state = tracker.current()
        for (index in 0 until 720) {
            state = tracker.add((20 + index).toLong() * minute, 1008f + if (index % 2 == 0) 0.02f else -0.02f)
        }

        assertTrue("12 小时没有回升，过程必须仍然挂着", state.active)
        assertEquals("降幅照样记得", -2f, state.dropHpa, 0.1f)
    }

    @Test
    fun `雨时下时停不得来回翻状态`() {
        // 雨带间歇：气压在 1008 附近小幅上下抖（±0.6，峰峰 1.2），不到解除门限 1.5
        val tracker = WeatherEpisodeTracker()
        feed(tracker, listOf(1010f, 1008.2f))
        assertTrue(tracker.current().active)

        var flips = 0
        var previous = tracker.current().active
        var state = tracker.current()
        for (index in 0 until 120) {
            val wobble = 1008.2f + if (index % 4 < 2) 0.6f else -0.6f
            state = tracker.add((10 + index).toLong() * minute, wobble)
            if (state.active != previous) flips++
            previous = state.active
        }

        assertEquals("迟滞的作用就是不让状态来回翻", 0, flips)
        assertTrue(state.active)
    }

    @Test
    fun `解除之后再次下降会开启新一轮`() {
        val tracker = WeatherEpisodeTracker()
        feed(tracker, listOf(1010f, 1007f))
        feed(tracker, listOf(1008.6f), startMinutes = 10) // 自最低点 1007 回升 1.6 → 解除
        assertFalse(tracker.current().active)

        val state = feed(tracker, listOf(1006f), startMinutes = 20)

        assertTrue("新的一轮下降应当重新进入过程", state.active)
    }

    @Test
    fun `未进入过程前参照点随回升抬高`() {
        val tracker = WeatherEpisodeTracker()
        // 先降 1 hPa（不够），再升到更高，再降 1.4 hPa —— 仍不该进入
        feed(tracker, listOf(1010f, 1009f))
        feed(tracker, listOf(1011f), startMinutes = 5)
        val state = feed(tracker, listOf(1009.7f), startMinutes = 10)

        assertFalse("应当以抬高的参照点计算降幅", state.active)
    }

    @Test
    fun `锁存状态可持久化并恢复`() {
        // 这是"挂上就不摘"能否扛住进程被回收的关键：快照存下来，重启后装回去。
        val original = WeatherEpisodeTracker()
        feed(original, listOf(1010f, 1008.5f, 1008f))
        val snapshot = original.current()
        assertTrue(snapshot.active)

        val restored = WeatherEpisodeTracker()
        restored.restore(snapshot)

        val after = restored.current()
        assertTrue("恢复后必须仍然是「过程中」", after.active)
        assertEquals(-2f, after.dropHpa, 0.05f)
        assertEquals(1008f, after.minHpa, 0.05f)

        // 重启后继续喂：从最低点回升 1.5 就应当解除，说明 min 也被正确带回来了
        val cleared = restored.add(60 * minute, 1009.5f)
        assertFalse("恢复的最低点必须参与解除判定", cleared.active)
    }

    @Test
    fun `空快照恢复后不得凭空报警`() {
        val tracker = WeatherEpisodeTracker()
        tracker.restore(WeatherEpisode(false, 0f, 0L, 0f, 0f, null))

        assertFalse(tracker.current().active)
        assertEquals(0f, tracker.current().peakHpa, 0.001f)
    }
}
