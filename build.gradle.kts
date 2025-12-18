buildscript {
    // 应用config.gradle
    apply(from = "config.gradle")
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// 清理任务
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

subprojects {
    configurations.all {
        resolutionStrategy {
            force(
                "org.jetbrains.kotlin:kotlin-stdlib:2.0.21",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.21",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21"
            )
        }
    }
}