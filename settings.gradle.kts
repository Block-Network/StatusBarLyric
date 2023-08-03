dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://api.xposed.info")
        maven("https://jitpack.io")
    }
}
rootProject.name = "Statusbar Lyric"
include(":app")
include(":blockmiui")
include(":xtoast")
