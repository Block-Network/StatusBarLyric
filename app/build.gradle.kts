import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "cn.fkj233.xposed.statusbarlyric"
        minSdk = 26
        targetSdk = 32
        versionCode = 83
        versionName = "3.56.25"
        aaptOptions.cruncherEnabled = false
        aaptOptions.useNewCruncher = false
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isZipAlignEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                    "proguard-log.pro"
                )
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.majorVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/**"
            excludes += "/kotlin/**"
            excludes += "/*.txt"
            excludes += "/*.bin"
        }
        dex {
            useLegacyPackaging = true
        }
    }
    buildFeatures {
        viewBinding = true
    }
    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName =
                "StatusBarLyric-$versionName($versionCode)-$name.apk"
        }
    }
}

dependencies {
    //API
    compileOnly("de.robv.android.xposed:api:82")
    //带源码Api
    compileOnly("de.robv.android.xposed:api:82:sources")
    //MIUI 通知栏
    implementation(files("libs/miui_sdk.jar"))
    // microsoft app center
    val appCenterSdkVersion = "4.4.2"
    implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
    implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
}