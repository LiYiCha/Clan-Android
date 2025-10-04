pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url =uri("https://maven.aliyun.com/repository/releases") }
        maven { url =uri("https://maven.aliyun.com/repository/google") }
        maven { url =uri("https://maven.aliyun.com/repository/central") }
        maven { url =uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url =uri("https://maven.aliyun.com/repository/public") }
        maven { url =uri("https://jitpack.io") }
        google()
        mavenCentral()
    }
}
rootProject.name = "clanApp"
include(":app")
include(":module-auth")
include(":library-base")
include(":library-mvvmlazy")
include(":library-captcha")
