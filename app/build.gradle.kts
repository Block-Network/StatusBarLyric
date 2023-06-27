import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "statusbar.lyric"
    compileSdk = 34
    val buildTime = System.currentTimeMillis()
    defaultConfig {
        applicationId = "statusbar.lyric"
        minSdk = 26
        targetSdk = 34
        versionCode = 165
        versionName = "5.4.2$buildTime"
        aaptOptions.cruncherEnabled = false
        aaptOptions.useNewCruncher = false
        buildConfigField("long", "BUILD_TIME", "$buildTime")
        buildConfigField("int", "apiVersion", "1")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro", "proguard-log.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
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
            (this as BaseVariantOutputImpl).outputFileName = "StatusBarLyric-$versionName($versionCode)-$name-$buildTime.apk"
        }
    }
}


dependencies {
    compileOnly("de.robv.android.xposed:api:82")
    compileOnly(project(":hidden-api"))
    implementation(project(":blockmiui"))
    implementation(project(":xtoast"))
    implementation(project(":LyricGetterApi"))
    implementation("com.github.kyuubiran:EzXHelper:2.0.5")
}
