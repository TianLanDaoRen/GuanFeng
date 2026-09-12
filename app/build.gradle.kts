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
        versionCode = 1
        versionName = "1.0"

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