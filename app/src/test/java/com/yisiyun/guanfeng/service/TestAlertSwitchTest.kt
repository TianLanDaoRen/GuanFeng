package com.yisiyun.guanfeng.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 文件开关「test_alert_after_min」的解析单测。
 *
 * 这个值决定的是"验哪一个量级"：解析错一位，验出来的就不再是要验的那件事。
 * 文件是手写的（`adb shell echo`），所以脏输入（空格、换行、空文件、负数）
 * 都必须有确定的归宿，而不是抛出去把服务启动路径搞崩。
 */
class TestAlertSwitchTest {

    @Test
    fun `正常值_按分钟解析`() {
        assertEquals(2, TestAlertSwitch.parseMinutes("2"))
        assertEquals(30, TestAlertSwitch.parseMinutes("30"))
    }

    @Test
    fun `echo 写出来的尾换行与空格都要容忍`() {
        assertEquals(30, TestAlertSwitch.parseMinutes("30\n"))
        assertEquals(5, TestAlertSwitch.parseMinutes("  5  \n"))
    }

    @Test
    fun `空文件与垃圾内容返回 null_不预约`() {
        assertNull(TestAlertSwitch.parseMinutes(null))
        assertNull(TestAlertSwitch.parseMinutes(""))
        assertNull(TestAlertSwitch.parseMinutes("   \n"))
        assertNull(TestAlertSwitch.parseMinutes("abc"))
        assertNull(TestAlertSwitch.parseMinutes("2min"))
        assertNull(TestAlertSwitch.parseMinutes("1.5"))
    }

    @Test
    fun `零与负数不预约_上限一天`() {
        assertNull("0 分钟没有验证意义，别默默当成「立刻」", TestAlertSwitch.parseMinutes("0"))
        assertNull(TestAlertSwitch.parseMinutes("-30"))
        assertEquals(TestAlertSwitch.MAX_MINUTES, TestAlertSwitch.parseMinutes("1440"))
        assertNull("超过上限说明是手滑写错了", TestAlertSwitch.parseMinutes("1441"))
        assertNull(TestAlertSwitch.parseMinutes("99999"))
    }
}
