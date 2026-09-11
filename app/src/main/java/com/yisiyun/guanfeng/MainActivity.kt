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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate sdk=${Build.VERSION.SDK_INT} model=${Build.MODEL}")

        // 采集由前台服务托管，屏幕不必强制常亮（省电，也让佩戴更自然）。
        WatchSessionService.start(this)

        val bodySensorsGranted = hasPermission(Manifest.permission.BODY_SENSORS)
        val notificationsGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            hasPermission(Manifest.permission.POST_NOTIFICATIONS)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val bodyLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { ok -> Log.i(TAG, "BODY_SENSORS 授权结果 = $ok") }
                val notificationLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { ok -> Log.i(TAG, "POST_NOTIFICATIONS 授权结果 = $ok") }

                LaunchedEffect(Unit) {
                    if (!bodySensorsGranted) {
                        Log.i(TAG, "申请 BODY_SENSORS…")
                        bodyLauncher.launch(Manifest.permission.BODY_SENSORS)
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
