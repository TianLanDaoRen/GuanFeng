import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

/**
 * 和风代理的 gate key 从 local.properties 读——**不写在仓库里**。
 *
 * 本仓库是公开的，而这个 key 是"挡住别人蹭免费额度"的唯一一道门：
 * 写进源码等于门不上锁。所以它走 local.properties（已 gitignore），
 * 构建时注入 BuildConfig。克隆仓库的人没有这个值，天气采集功能会自动关闭
 * （见 QweatherClient.isConfigured），不会误打我们的代理。
 *
 * 私钥则根本不在这里——它在 Vercel 的环境变量里，连这个仓库都碰不到。
 */
val props: Properties = run {
    val f = rootProject.file("local.properties")
    if (f.exists()) Properties().apply { f.inputStream().use { load(it) } } else Properties()
}
val qweatherGateKey: String = props.getProperty("qweather.gateKey").orEmpty()

android {
    namespace = "com.yisiyun.guanfeng"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.yisiyun.guanfeng"
        minSdk = 27
        targetSdk = 37
        // 版本号方案（主人 2026-09-12 定）：B{年}{月}.{日}.{当日序号}
        //   B        = Beta
        //   202609   = 2026 年 9 月
        //   .12      = 12 日
        //   .01      = 当天第一版
        //
        // versionCode 必须是与它同源的**单调递增整数**（Android 靠它判断能否覆盖安装），
        // 所以直接把 B 后面的数字连起来：2026091201。
        // 上限提醒：int 上限 2147483647，这个 10 位方案到 2147 年才会撞上，不必担心；
        // 但**不要**改成 11 位（例如把当日序号补到三位），那会溢出。
        versionCode = 2026091201
        versionName = "B202609.12.01"

        // 仪器测试已移除（见 dependencies 段的说明）。这一行留着是**有意的**：
        // 将来若重新加仪器测试，除它之外还需要那四条 androidTestImplementation。
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 空值也可构建（功能自动关闭），这样克隆者不配也能编译通过
        buildConfigField("String", "QWEATHER_GATE_KEY", "\"$qweatherGateKey\"")


        // 坐标**不设兜底值**：取不到就由设置流程明确问使用者，不替他猜（见 SiteLocation）。
    }

    /**
     * release 签名：全部从 local.properties 读（密钥库路径、别名、两个口令）。
     *
     * 为什么不在仓库里写死：本仓库是公开的，而密钥库是**应用身份**——
     * 泄露它等于任何人都能签出"看起来是你发的"更新包。
     *
     * 为什么四项缺一就不签：这样克隆仓库的人（没有密钥库）依然能 `assembleRelease`
     * 得到一个未签名产物，不会因为签名配置缺失而构建失败。
     */
    signingConfigs {
        create("release") {
            val storePath = props.getProperty("guanfeng.storeFile")
            val storePw = props.getProperty("guanfeng.storePassword")
            val alias = props.getProperty("guanfeng.keyAlias")
            val keyPw = props.getProperty("guanfeng.keyPassword")
            // 注意要判空白而不是判 null：local.properties 里留空得到的是空串，
            // 空串非 null，会被当成已配置→ 拿空口令去开密钥库 → 构建失败。
            // （这不是假想，是第一次就踩到的。）
            if (!storePath.isNullOrBlank() && !storePw.isNullOrBlank() &&
                !alias.isNullOrBlank() && !keyPw.isNullOrBlank()
            ) {
                storeFile = rootProject.file(storePath)
                storePassword = storePw
                keyAlias = alias
                keyPassword = keyPw
            }
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            // 只在四项齐备时才挂上签名配置
            if (!props.getProperty("guanfeng.storePassword").isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        // QweatherClient 要读 BuildConfig.QWEATHER_GATE_KEY
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // 只有 JVM 单测（100 个），没有仪器测试。
    // 模板自带的 androidTest 桩与它的依赖（espresso / androidx.test.ext:junit /
    // compose ui-test）已删除——它们从未被任何 CI 跑过，却持续引来 Dependabot 的升级 PR。
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
}