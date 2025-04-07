@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
}

val localProperties = org.jetbrains.kotlin.konan.properties.Properties()
if (rootProject.file("local.properties").canRead()) localProperties.load(rootProject.file("local.properties").inputStream())

android {
    namespace = "statusbar.lyric"
    compileSdk = 36
    val buildTime = System.currentTimeMillis()
    defaultConfig {
        applicationId = "statusbar.lyric"
        minSdk = 26
        targetSdk = 36
        versionCode = 713
        versionName = "7.1.3"
        aaptOptions.cruncherEnabled = false
        dependenciesInfo.includeInApk = false
        buildConfigField("long", "BUILD_TIME", "$buildTime")
        buildConfigField("int", "API_VERSION", "6")
        buildConfigField("int", "COMPOSE_CONFIG_VERSION", "1")
    }
    val config = localProperties.getProperty("androidStoreFile")?.let {
        signingConfigs.create("config") {
            storeFile = file(it)
            storePassword = localProperties.getProperty("androidStorePassword")
            keyAlias = localProperties.getProperty("androidKeyAlias")
            keyPassword = localProperties.getProperty("androidKeyPassword")
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    buildTypes {
        all {
            signingConfig = config ?: signingConfigs["debug"]
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    packaging {
        resources {
            excludes += "**"
        }
    }
    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName = "StatusBarLyric-$versionName($versionCode)-$name-$buildTime.apk"
        }
    }
    buildFeatures.buildConfig = true
    kotlin.jvmToolchain(21)
}

dependencies {
    compileOnly(libs.xposed)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.ezXHelper)
    implementation(libs.haze)
    implementation(libs.lyric.getter.api)
    implementation(libs.miuix)

    debugImplementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
}
