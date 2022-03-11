import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "statusbar.lyric"
        minSdk = 26
        targetSdk = 32
        versionCode = 111
        versionName = "4.4.3"
        aaptOptions.cruncherEnabled = false
        aaptOptions.useNewCruncher = false
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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

fun getGitHeadRefsSuffix(project: Project): String {
    // .git/HEAD描述当前目录所指向的分支信息，内容示例："ref: refs/heads/master\n"
    val headFile = File(project.rootProject.projectDir, ".git${File.separator}HEAD")
    if (headFile.exists() && headFile.canRead()) {
        val string: String = headFile.readText(Charsets.UTF_8)
        if (string.contains("Dev")) {
            val heads = File(
                project.rootProject.projectDir,
                ".git${File.separator}refs${File.separator}heads${File.separator}Dev"
            )
            if (heads.exists() && heads.canRead())
                println(heads.readText(Charsets.UTF_8).substring(0, 7))
                return heads.readText(Charsets.UTF_8).substring(0, 7)
        }
    }
    return ""
}

dependencies {
    //API
    compileOnly("de.robv.android.xposed:api:82")
    //带源码Api
    compileOnly("de.robv.android.xposed:api:82:sources")
    // Use Hide Api
    compileOnly(project(":hidden-api"))
    //MIUI 通知栏
    implementation(files("libs/miui_sdk.jar"))
    // microsoft app center
    val appCenterSdkVersion = "4.4.2"
    implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
    implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")
    implementation(project(":blockmiui"))
    // Google Ad
    implementation(project(":ads"))
    implementation("com.google.android.gms:play-services-ads:20.6.0")
}
