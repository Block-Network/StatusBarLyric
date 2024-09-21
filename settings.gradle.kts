@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}
rootProject.name = "Statusbar Lyric"
include(":app", ":blockmiui")
