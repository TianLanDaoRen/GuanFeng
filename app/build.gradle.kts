plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

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
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
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