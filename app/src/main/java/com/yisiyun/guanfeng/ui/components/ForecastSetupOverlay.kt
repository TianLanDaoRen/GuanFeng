package com.yisiyun.guanfeng.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 天气采集的**三步设置流程**：说明 → 定位 → （失败时的）出路。
 *
 * ## 为什么是三步而不是"问一句就完事"
 *
 * 第一版只问了同意，然后把取坐标交给"读系统上次的位置"。真机实测发现那条路
 * **永远走不通**：这块表的 gps 与 passive provider 都是 `enabled=true` 却没有缓存位置
 * ——运动手表的 GPS 平时待机。我当时给它配了个配置里的兜底坐标，主人一眼看出问题：
 * **兜底等于替你猜你在哪**，而这个项目自己的规矩是「宁可不取数，也不用错的坐标」。
 *
 * 所以改成：点同意之后**真的去取一次定位**，拿到才继续。
 *
 * ## 失败必须给出路
 *
 * 室内锁不上星是真实存在的（GPS 要见天）。所以超时不是死循环、也不是悄悄用兜底值，
 * 而是问使用者：再试一次、沿用上次成功记录的位置、还是暂时不采集。
 * **三条路都要他自己选**——替他选就是替他猜。
 */
sealed interface WeatherSetupStep {
    /** 还没问过：说明会上传什么。 */
    data object Consent : WeatherSetupStep

    /**
     * 已同意定位，但手表的 **Wi-Fi 模块没开**。
     *
     * 顺序是主人定的：**先问同意，同意了才门控 Wi-Fi**——不同意就什么都不做，
     * 不该拿 Wi-Fi 去为难一个根本不想用天气功能的人。
     */
    data object NeedWifi : WeatherSetupStep

    /** 正在取定位。 */
    data class Locating(val elapsedSeconds: Int) : WeatherSetupStep

    /** 定位失败，给出路。 */
    data class Failed(val hasRemembered: Boolean) : WeatherSetupStep

    /**
     * GPS 定不上，但**网络推断出了位置**（IP 定位）。
     * 这是测量而不是猜测，所以可以给"就用这个"——但仍要使用者点头，并告知精度。
     */
    data class NetworkFound(val city: String?, val accuracyKm: Int) : WeatherSetupStep
}

@Composable
fun ForecastSetupOverlay(
    step: WeatherSetupStep,
    onOpenWifi: () -> Unit = {},
    onExitApp: () -> Unit = {},
    onAllow: () -> Unit,
    onDecline: () -> Unit,
    onCancelLocating: () -> Unit,
    onRetry: () -> Unit,
    onUseRemembered: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        when (step) {
            is WeatherSetupStep.Consent -> ConsentBody(onAllow = onAllow, onDecline = onDecline)

            is WeatherSetupStep.NeedWifi -> {
                Text("需要打开 Wi-Fi", color = Color(0xFF7FD1E8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(7.dp))
                Text(
                    "定位要扫描周边热点来判断你在哪，取天气也要联网。",
                    color = Color(0xFFD0D0D0),
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "打开即可，连不连上热点都行——扫描本身就够用。",
                    color = Color(0xFF9A9A9A),
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                )
                Spacer(Modifier.weight(1f))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FullWidthButton("打开 Wi-Fi 设置", Color(0xFF2A6C86), Color(0xFFD8F2FA), onOpenWifi)
                    FullWidthButton("退出应用", Color(0xFF242424), Color(0xFF9A9A9A), onExitApp)
                }
            }

            is WeatherSetupStep.Locating -> {
                Text("正在定位…", color = Color(0xFF7FD1E8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(7.dp))
                Text(
                    "已等 ${step.elapsedSeconds} 秒。卫星定位要看得见天空，" +
                        "而且同时看到至少四颗卫星才算数，所以室内通常定不上——" +
                        "到户外或窗边再试。",
                    color = Color(0xFFD0D0D0),
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                )
                Spacer(Modifier.height(7.dp))
                Text(
                    "拿到坐标才动。之后每轮采集都会静默更新一次定位，" +
                        "更新不上就沿用上次的，不会因为定位失败而停止采集。",
                    color = Color(0xFF8A8A8A),
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                )
                Spacer(Modifier.weight(1f))
                FullWidthButton("取消", Color(0xFF242424), Color(0xFF9A9A9A), onCancelLocating)
            }

            is WeatherSetupStep.NetworkFound -> {
                Text("在屋里，定不上星", color = Color(0xFFF2C14E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(7.dp))
                Text(
                    "但按这台手表所在网络的出口位置，推断出你在" +
                        "「${step.city ?: "未知城市"}」，精度约 ${step.accuracyKm} 公里。",
                    color = Color(0xFFD0D0D0),
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "这是网络出口所在的区域，常常是运营商在省内的节点，" +
                        "不一定正好是你所在的城市。天气本身是城市级的，一般够用；" +
                        "要更准就到户外点「再试定位」拿卫星定位。来源会如实记进 CSV。",
                    color = Color(0xFF8A8A8A),
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                )
                Spacer(Modifier.weight(1f))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FullWidthButton("就用这个", Color(0xFF2A6C86), Color(0xFFD8F2FA), onUseRemembered)
                    FullWidthButton("再试定位", Color(0xFF242424), Color(0xFF9A9A9A), onRetry)
                }
            }

            is WeatherSetupStep.Failed -> {
                Text("没定上位", color = Color(0xFFF2C14E), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(7.dp))
                Text(
                    "室内或阴天常会这样。天气采集需要坐标才能开始，" +
                        "所以现在还没有采任何数据。",
                    color = Color(0xFFD0D0D0),
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                )
                Spacer(Modifier.weight(1f))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (step.hasRemembered) {
                        // 只有**历史上真的成功取到过**才给这个选项——那不是猜，是用事实
                        FullWidthButton("用上次记录的坐标", Color(0xFF2A6C86), Color(0xFFD8F2FA), onUseRemembered)
                    }
                    FullWidthButton("再试一次", Color(0xFF2A6C86), Color(0xFFD8F2FA), onRetry)
                    FullWidthButton("暂不采集", Color(0xFF242424), Color(0xFF9A9A9A), onCancelLocating)
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text("天气数据由和风天气提供", color = Color(0xFF5A5A5A), fontSize = 7.sp)
    }
}

@Composable
private fun ColumnScope.ConsentBody(onAllow: () -> Unit, onDecline: () -> Unit) {
    Text("开启天气校准？", color = Color(0xFF7FD1E8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(6.dp))
    Text(
        "每 30 分钟取一次和风天气，存进本机 CSV，供日后校准判据。",
        color = Color(0xFFD0D0D0), fontSize = 9.sp, lineHeight = 12.sp,
    )
    Spacer(Modifier.height(7.dp))
    Text("会上传", color = Color(0xFF6EE7A8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
    Text("你的坐标（约 1 公里精度）", color = Color(0xFFB8B8B8), fontSize = 9.sp, lineHeight = 12.sp)
    Spacer(Modifier.height(5.dp))
    Text("不会上传", color = Color(0xFFF2C14E), fontSize = 9.sp, fontWeight = FontWeight.Bold)
    Text(
        "心率、腕温、气压读数、体感打卡、备注原文",
        color = Color(0xFFB8B8B8), fontSize = 9.sp, lineHeight = 12.sp,
    )
    Spacer(Modifier.height(6.dp))
    Text("点开启后需要先取一次定位。只问这一次。", color = Color(0xFF7A7A7A), fontSize = 8.sp)
    Spacer(Modifier.weight(1f))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onDecline,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF242424), contentColor = Color(0xFF9A9A9A),
            ),
        ) { Text("不用", fontSize = 11.sp) }
        Button(
            onClick = onAllow,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A6C86), contentColor = Color(0xFFD8F2FA),
            ),
        ) { Text("开启", fontSize = 11.sp) }
    }
}

@Composable
private fun FullWidthButton(
    label: String,
    container: Color,
    content: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
    ) { Text(label, fontSize = 11.sp) }
}
