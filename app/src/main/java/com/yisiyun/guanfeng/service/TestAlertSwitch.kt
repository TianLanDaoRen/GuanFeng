package com.yisiyun.guanfeng.service

import android.content.Context
import android.util.Log
import java.io.File

/**
 * 「预约 N 分钟后的测试提醒」——**用文件开关**，不给界面加任何东西。
 *
 * ## 为什么需要它
 *
 * 界面上那个「测试提醒」按钮预约的是 **5 秒**，它回答的是
 * "服务能不能调起震动"——这条链路上真机已经成立（退出应用 5 秒后照样震到）。
 * 但它证明不了**长时间**这件事：主人真正要问的是"应用在前台挂了半小时、
 * 再回到表盘之后，提醒还来不来"。5 秒与 30 分钟之间隔着睡眠模式、
 * 后台回收、indicator 换代整整一套东西，量级不同就不能互相背书。
 *
 * ## 怎么用（全程 adb，界面零改动）
 *
 * ```
 * adb -s <序列号> shell "echo 30 > /sdcard/Android/data/com.yisiyun.guanfeng/files/test_alert_after_min"
 * # 然后启动应用，30 分钟后由服务循环兑现（震动 + 一条待确认提醒）
 * adb -s <序列号> logcat -d | grep "已兑现"
 * ```
 *
 * ## 三个刻意的选择
 *
 * ① **读到就删（一次性开关）**：若留到"触发之后再删"，进程在等待期间被重启
 *    就会重新读到同一个文件、把预约再顺延 N 分钟——每次重启都续约，永远等不到兑现。
 *    读一次删一次之后，语义就是"这一次"。
 * ② **预约走 [AlertState.requestDelayedTest]**：与真实提醒、与界面那个按钮
 *    **完全相同的那条路**（服务循环兑现 → [TrendNotifier.triggerTestAlert] → 直驱马达）。
 *    另起一条路验出来的东西，不能给主链路背书。
 * ③ **只在服务启动时读一次**：读盘落在启动路径上就够，不做轮询——
 *    为一次性实验付一份常驻成本不值得。
 */
object TestAlertSwitch {

    const val FILE_NAME = "test_alert_after_min"

    /**
     * 上限一天。这是个验证用的旋钮，不是排程系统：离谱的值几乎一定是手滑写错了，
     * 早点在日志里骂一声，比默默地预约到明年强。
     */
    const val MAX_MINUTES = 24 * 60

    private const val TAG = "GuanFengService"

    /**
     * 读开关文件并预约。返回预约的分钟数；没有文件 / 内容不合法返回 null。
     *
     * 不抛异常：它在服务启动路径上，一个写歪的文件不该让采集起不来。
     */
    fun armFromFile(context: Context): Int? {
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        val file = File(dir, FILE_NAME)
        if (!file.exists()) return null

        val raw = runCatching { file.readText() }.getOrNull()
        // 不论内容是否合法都删掉：留着它，每次启动都会重新读同一个坏值。
        runCatching { file.delete() }

        val minutes = parseMinutes(raw)
        if (minutes == null) {
            Log.w(TAG, "$FILE_NAME 不是 1~$MAX_MINUTES 的整数（读到「${raw?.trim()}」），已删掉且不预约")
            return null
        }
        AlertState.requestDelayedTest(minutes * 60_000L)
        Log.i(TAG, "读到 $FILE_NAME=$minutes，已预约 $minutes 分钟后兑现测试提醒（开关已删，一次性）")
        return minutes
    }

    /** 纯函数：文件内容 → 分钟数。单测覆盖（解析错一个值，验的就是另一个量级）。 */
    fun parseMinutes(raw: String?): Int? =
        raw?.trim()?.toIntOrNull()?.takeIf { it in 1..MAX_MINUTES }
}
