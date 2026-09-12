package com.yisiyun.guanfeng.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yisiyun.guanfeng.log.QweatherLogger
import com.yisiyun.guanfeng.ui.components.PageHeader
import com.yisiyun.guanfeng.ui.components.WeatherIcon

/**
 * 负一屏 · 天气预报。
 *
 * ## 为什么放在"负一屏"而不是加在末尾
 *
 * 主人定的：**默认仍然是观风页**（那是这个应用的主角，戴着表一抬手要看到的是气压），
 * 预报是"往左多看一眼"的东西，所以放在主屏**之前**。
 *
 * ## 它只读已落盘的数据，不自己发请求
 *
 * 页面上的每一行都是**某次采集当时拿回来的**，所以断网也能看——这与整个应用
 * "核心不依赖网络"的性格一致。要刷新就等下一轮采集，或去设置流程里重新定位。
 * 代价是它可能落后最多一个采集周期（30 分钟），所以**必须把取数时刻写在脸上**，
 * 不能让人以为是此刻的实时天气。
 *
 * ## 没同意也要能用
 *
 * 主人要求"如果用户没同意，依旧给他留个按钮"——所以这里不拦人：
 * 没数据就说明为什么、给一个按钮；有旧数据就先显示旧数据。
 */
@Composable
fun ForecastPage(
    snapshot: QweatherLogger.Snapshot?,
    days: List<QweatherLogger.DayLine>,
    air: QweatherLogger.AirLine?,
    consentGranted: Boolean,
    onRequestConsent: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        PageHeader(
            title = "天气预报",
            titleColor = Color(0xFF7FD1E8),
            subtitle = snapshot?.let { "取自 ${it.fetchedClock}" } ?: "尚未采集",
        )

        // 诊断：页面渲染时它手里到底有什么。数据"在文件里却显示没有"时，
        // 这一行能立刻分清是"读不到"还是"没接到"。
        android.util.Log.i(
            "ForecastPage",
            "渲染：snapshot=" + (snapshot != null) + " days=" + days.size +
                " air=" + (air != null) + " 已同意=" + consentGranted,
        )
        when {
            !consentGranted -> EmptyState(
                text = "还没开启天气采集。开启后每 30 分钟取一次和风天气，" +
                    "存进本机 CSV，供日后校准判据。",
                buttonLabel = "开启天气采集",
                onButton = onRequestConsent,
            )

            snapshot == null -> EmptyState(
                text = "还没有采到数据。手表需要定位权限与网络；第一次采集通常在开启后几分钟内完成。",
                buttonLabel = "去设置",
                onButton = onRequestConsent,
            )

            else -> SnapshotBody(snapshot, days, air)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.SnapshotBody(
    snapshot: QweatherLogger.Snapshot,
    days: List<QweatherLogger.DayLine>,
    air: QweatherLogger.AirLine?,
) {
    // **整页只用一个竖向滚动容器**。
    // 第一版把横条放在滚动区外面，又让小时列表用 weight(1f) 撑剩余空间——
    // 固定内容一超高，列表就被压成 0 高度，于是"整页滑不动"。
    // 教训：在一个已经会溢出的页面上，不要让子块去"分剩余空间"，让它自然流动。
    // **必须给它 weight(1f)**：否则内容一长，下面的署名与定位来源会被顶出屏幕——
    // 真机上就是这样：卡片区一多，那行小字直接看不见了。
    // 让滚动区吃掉"剩余空间"，固定行才永远在屏幕里。
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState()),
    ) {
    // 当前状况：一行大字，抬手就能看清
    Row(verticalAlignment = Alignment.CenterVertically) {
        WeatherIcon(code = snapshot.conditionCode, tint = Color(0xFF9FD8EE), fontSize = 26.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            snapshot.conditionText.ifBlank { "—" },
            color = Color(0xFFE8E8E8),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.weight(1f))
        Text(
            snapshot.tempC.toDoubleOrNull()
                ?.let { String.format(java.util.Locale.US, "%.1f℃", it) }
                ?: "—",
            color = Color(0xFFF2C14E),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }

    if (days.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            days.forEach { DayCell(it) }
        }
    }

    Spacer(Modifier.height(8.dp))
    Text("未来几小时", color = Color(0xFF6EE7A8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        snapshot.hours.forEach { hour -> HourRow(hour) }
    }

    // ── 卡片区：把"读数"从标题下那行挤成一团的字里解放出来 ──────────────
    // 原来四五个字段塞一行还要折行，手表上读起来很吃力；做成卡片之后
    // 每项有自己的位置，**新数据（空气质量、以后的预警）也有地方放**。
    Spacer(Modifier.height(10.dp))
    Text("此刻", color = Color(0xFF6EE7A8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(5.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        MetricCard(
            label = "体感",
            value = snapshot.feelsLikeC.toDoubleOrNull()
                ?.let { String.format(java.util.Locale.US, "%.1f", it) } ?: "—",
            unit = "℃",
            valueColor = Color(0xFFF2C14E),
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "湿度",
            value = snapshot.humidity.toDoubleOrNull()?.let { "${(it * 100).toInt()}" } ?: "—",
            unit = "%",
            valueColor = Color(0xFF7FD1E8),
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(6.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        MetricCard(
            label = "气压",
            value = snapshot.pressureHpa.ifBlank { "—" },
            unit = "hPa",
            valueColor = Color(0xFFB79CE8),
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            // 紫外线值得单独一格：它是整份数据里**唯一没法从别处推出来**的一项。
            // 显示"当前 / 今日峰值"而不是只显示当前：**每天有半天当前值是 0**，
            // 傍晚看到"0"虽然正确却没信息量；而峰值才是"要不要防晒"的依据。
            label = "紫外线 当前/峰值",
            value = snapshot.uvIndex.ifBlank { "—" } + " / " + (days.firstOrNull()?.uvMax?.ifBlank { "—" } ?: "—"),
            unit = "",
            valueColor = Color(0xFFE8C36A),
            modifier = Modifier.weight(1f),
        )
    }

    if (air != null && air.aqi.isNotBlank()) {
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                label = "空气 ${air.category}",
                value = air.aqi,
                unit = "AQI",
                valueColor = aqiColor(air.aqi),
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                label = "首要污染物",
                value = air.primaryPollutant.ifBlank { "—" },
                unit = "",
                valueColor = Color(0xFFB8B8B8),
                modifier = Modifier.weight(1f),
            )
        }
    }


    Spacer(Modifier.height(10.dp))
    } // 竖向滚动容器到此结束

    Spacer(Modifier.height(6.dp))
    Text(
        // 定位来源要写出来：它决定这份预报的可信范围（高德 Wi-Fi 是 30 米级、IP 推断是城市级）。
        // 但要写**人话**：`amap:4` 是开发者黑话，使用者看不懂，看不懂就等于没写。
        "天气数据由和风天气提供 · 定位：${locationSourceLabel(snapshot.locationSource)}",
        color = Color(0xFF5A5A5A),
        fontSize = 7.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

/**
 * 横条上的一格：周几（今天标"今天"）+ 图标 + 低温/高温。
 *
 * 温度按主人说的**低在前**。日期来自 UTC 的 forecastStartTime，必须换算成本地再取周几——
 * 直接用 UTC 会在跨日时把今天标成明天。
 */
@Composable
private fun DayCell(day: QweatherLogger.DayLine) {
    val localDate = runCatching {
        java.time.OffsetDateTime.parse(day.dateUtc)
            .atZoneSameInstant(java.time.ZoneId.systemDefault())
            .toLocalDate()
    }.getOrNull()
    val today = java.time.LocalDate.now(java.time.ZoneId.systemDefault())
    val label = when {
        localDate == null -> "—"
        localDate == today -> "今天"
        else -> listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")[localDate.dayOfWeek.value - 1]
    }
    fun one(v: String) = v.toDoubleOrNull()?.let { String.format(java.util.Locale.US, "%.0f", it) } ?: "—"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = if (localDate == today) Color(0xFF6EE7A8) else Color(0xFF9A9A9A), fontSize = 8.sp)
        Spacer(Modifier.height(3.dp))
        WeatherIcon(code = day.conditionCode, tint = Color(0xFFB8D8E8), fontSize = 15.sp)
        Spacer(Modifier.height(3.dp))
        Text("${one(day.minC)}°/${one(day.maxC)}°", color = Color(0xFFD0D0D0), fontSize = 8.sp)
    }
}

@Composable
private fun HourRow(hour: QweatherLogger.HourLine) {
    // forecastTime 是 **ISO UTC**（形如 2026-09-12T08:00Z）——**必须换算成本地时间**再显示。
    // 第一版我直接截了字符串，于是 16:30 取的快照里未来第一格写着"09:00"，
    // 一眼就能看出不对（手表上没有任何地方显示 UTC）。
    val label = runCatching {
        java.time.OffsetDateTime.parse(hour.clock)
            .atZoneSameInstant(java.time.ZoneId.systemDefault())
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrNull() ?: hour.clock.substringAfter('T').take(5).ifBlank { "—" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = Color(0xFF9A9A9A), fontSize = 9.sp, modifier = Modifier.weight(0.9f))
        WeatherIcon(code = hour.conditionCode, tint = Color(0xFFB8D8E8), fontSize = 12.sp)
        Spacer(Modifier.width(4.dp))
        Text(
            hour.conditionText.ifBlank { "—" },
            color = Color(0xFFD0D0D0),
            fontSize = 9.sp,
            modifier = Modifier.weight(1.4f),
        )
        Text(
            hour.precipProbability.toDoubleOrNull()
                ?.takeIf { it > 0.0 }
                ?.let { "💧${(it * 100).toInt()}%" }
                ?: "",
            color = Color(0xFF7FD1E8),
            fontSize = 8.sp,
            modifier = Modifier.weight(0.9f),
        )
        Text(
            // 上游给两位小数（28.14），界面上一位就够——手表屏幕小，多一位只是噪声
            // 单位要写出来：只有数字读者得靠上下文猜是温度还是降水概率，
            // 而这一行里两样都有
            hour.tempC.toDoubleOrNull()
                ?.let { String.format(java.util.Locale.US, "%.1f℃", it) }
                ?: "—",
            color = Color(0xFFE8E8E8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.EmptyState(
    text: String,
    buttonLabel: String,
    onButton: () -> Unit,
) {
    Spacer(Modifier.height(10.dp))
    Text(text, color = Color(0xFFB8B8B8), fontSize = 9.sp, lineHeight = 12.sp)
    Spacer(Modifier.weight(1f))
    Button(
        onClick = onButton,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2A6C86),
            contentColor = Color(0xFFD8F2FA),
        ),
    ) { Text(buttonLabel, fontSize = 11.sp) }
    Spacer(Modifier.height(6.dp))
    Text(
        "天气数据由和风天气提供",
        color = Color(0xFF5A5A5A),
        fontSize = 7.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

/**
 * 读数卡片。
 *
 * 数值用大字号、单位用小字号——手表屏幕上"一眼看到数"比"完整写出来"重要，
 * 单位跟着数字反而会把数字挤小。
 */
@Composable
private fun MetricCard(
    label: String,
    value: String,
    unit: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color(0xFF161616), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 6.dp),
    ) {
        // 单位跟着**标签**走，不跟数值挤一行：格子小，让数值独占一行才能一眼看清，
        // 单位属于"知道就行"的信息。第一版把单位放在数值旁边，结果 1018.18 一长，
        // "hPa" 就被逐字挤成竖排三行——Row 里没设 maxLines 就是这个下场。
        Text(
            if (unit.isBlank()) label else "$label · $unit",
            color = Color(0xFF7A7A7A),
            fontSize = 7.sp,
            maxLines = 1,
        )
        Spacer(Modifier.height(2.dp))
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

/**
 * AQI 配色按国标的分档来（优/良/轻度/中度/重度/严重），而不是自己发挥。
 * 这个颜色用户在其他地方见过，复用它的语义比自创配色更省解释。
 */
private fun aqiColor(aqi: String): Color = when (aqi.toIntOrNull() ?: -1) {
    in 0..50 -> Color(0xFF6EE7A8)
    in 51..100 -> Color(0xFFE8D96A)
    in 101..150 -> Color(0xFFF2A24E)
    in 151..200 -> Color(0xFFE2574C)
    in 201..300 -> Color(0xFFB04ED8)
    else -> Color(0xFF9A5A5A)
}

/**
 * 把落盘用的定位来源代号翻成人话。
 *
 * 代号是给 CSV 用的（要紧凑、要能 grep），界面是给人看的——**两者必须分开**：
 * `amap:4` 这种字符串写在手表上，使用者只会觉得莫名其妙，等于没说。
 *
 * 括号里的半句是**为什么它重要**：同一个"位置"可能是 30 米的 Wi-Fi 定位，
 * 也可能是几十公里外的网络推断。天气页面既然把位置当依据，就该让人知道依据有多硬。
 */
private fun locationSourceLabel(source: String): String = when {
    source.isBlank() -> "未知"
    source.startsWith("amap:5") -> "高德 Wi-Fi（约 30 米）"
    source.startsWith("amap:1") -> "高德卫星"
    source.startsWith("amap:4") -> "高德缓存"
    source.startsWith("amap:8") -> "高德离线"
    source.startsWith("amap:") -> "高德"
    source.startsWith("network-ip") -> {
        val city = source.substringAfter(':', "").takeIf { it.isNotBlank() && it != "?" }
        if (city == null) "网络推断（城市级）" else "网络推断 · $city（城市级）"
    }
    source.startsWith("system:") -> "系统卫星"
    else -> source
}
