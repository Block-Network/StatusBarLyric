dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
    }
}
rootProject.name = "Statusbar Lyric"
include(":app")
include(":blockmiui")
include(":hidden-api")
include(":xtoast")
include(":LyricGetterApi")
