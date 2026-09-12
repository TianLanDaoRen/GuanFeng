package com.yisiyun.guanfeng

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.yisiyun.guanfeng.service.WatchSessionService
import com.yisiyun.guanfeng.ui.GuanFengApp

/**
 * 观风 · 宿主 Activity。
 *
 * 职责已收窄：申请权限、拉起前台服务、把界面挂上去。
 * 采样与落盘不在界面里做——那样一旦熄屏回收 Activity，记录就断了（实测踩过）。
 */
class MainActivity : ComponentActivity() {

    /**
     * 每次回到前台都让界面去查一次"有没有未确认的提醒"。
     *
     * 这是提醒链路的最后一环：手表的通知与 indicator 都被平台限制
     * （通知被 canPost 白名单拦掉、indicator 只显示图标），
     * 所以"发生了什么"只能等用户回到应用时讲清楚。
     */
    override fun onResume() {
        super.onResume()
        com.yisiyun.guanfeng.data.PendingAlertStore.checkNow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate sdk=${Build.VERSION.SDK_INT} model=${Build.MODEL}")

        // 采集由前台服务托管，屏幕不必强制常亮（省电，也让佩戴更自然）。
        WatchSessionService.start(this)

        val bodySensorsGranted = hasPermission(Manifest.permission.BODY_SENSORS)
        // 【曾经的静默失败】ACTIVITY_RECOGNITION 只在清单里声明过，**从没在运行时申请**。
        // 后果不是崩溃，而是计步传感器注册被 SensorService 直接拒绝：
        //   `Tried enabling a sensor (Step_detector) without holding android.permission.ACTIVITY_RECOGNITION`
        // 而采集器只是记一行日志继续跑，于是 CSV 里的 steps 列长期全是 0——
        // 一个"看起来在记录、其实一直是空"的字段，比缺列更危险。
        // 2026-09-11 装新构建时在真机 logcat 里抓到。
        val activityRecognitionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
            hasPermission(Manifest.permission.ACTIVITY_RECOGNITION)
        val notificationsGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            hasPermission(Manifest.permission.POST_NOTIFICATIONS)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val bodyLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { ok -> Log.i(TAG, "BODY_SENSORS 授权结果 = $ok") }
                val activityLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { ok -> Log.i(TAG, "ACTIVITY_RECOGNITION 授权结果 = $ok") }
                val notificationLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { ok -> Log.i(TAG, "POST_NOTIFICATIONS 授权结果 = $ok") }

                LaunchedEffect(Unit) {
                    if (!bodySensorsGranted) {
                        Log.i(TAG, "申请 BODY_SENSORS…")
                        bodyLauncher.launch(Manifest.permission.BODY_SENSORS)
                    }
                    if (!activityRecognitionGranted) {
                        Log.i(TAG, "申请 ACTIVITY_RECOGNITION…")
                        activityLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                    if (!notificationsGranted) {
                        Log.i(TAG, "申请 POST_NOTIFICATIONS…")
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    GuanFengApp()
                }
            }
        }
    }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    companion object {
        const val TAG = "GuanFengProbe"
    }
}
