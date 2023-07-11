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
        versionCode = 167
        versionName = "5.9.9beta2"
        aaptOptions.cruncherEnabled = false
        aaptOptions.useNewCruncher = false
        buildConfigField("long", "BUILD_TIME", "$buildTime")
        buildConfigField("int", "API_VERSION", "2")
        buildConfigField("int", "CONFIG_VERSION", "3")
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
    implementation(project(":blockmiui"))
    implementation(project(":xtoast"))
    implementation(project(":LyricGetterApi"))
    implementation("com.github.kyuubiran:EzXHelper:2.0.6")
}
