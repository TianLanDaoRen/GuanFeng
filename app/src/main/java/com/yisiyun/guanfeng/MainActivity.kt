package com.yisiyun.guanfeng

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.yisiyun.guanfeng.ui.GuanFengScreen

/**
 * 观风 · 主界面宿主。
 *
 * MVP-1 的探针结论已归档：第三方应用可调起系统输入法、文本能 commit 到 state、
 * 气压/光照/9 轴零权限可用、心率与腕温实测可读。
 * 因此这里直接挂上 MVP-2 的真机验证界面：气压趋势 + 高度解耦。
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate sdk=${android.os.Build.VERSION.SDK_INT} model=${android.os.Build.MODEL}")

        // 走动测试期间必须让屏幕别熄：OPPO Watch 熄屏后第三方应用会被压制，
        // 采样一旦断掉，气压记录就会出现空洞。代价是耗电，测试结束关掉即可。
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val alreadyGranted = hasBodySensors()

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                var granted by remember { mutableStateOf(alreadyGranted) }
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { ok ->
                    granted = ok
                    Log.i(TAG, "BODY_SENSORS 授权结果 = $ok")
                }

                LaunchedEffect(Unit) {
                    if (!granted) {
                        Log.i(TAG, "申请 BODY_SENSORS 运行时权限…")
                        launcher.launch(Manifest.permission.BODY_SENSORS)
                    }
                }

                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    GuanFengScreen()
                }
            }
        }
    }

    private fun hasBodySensors(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) ==
            PackageManager.PERMISSION_GRANTED

    companion object {
        const val TAG = "GuanFengProbe"
    }
}
