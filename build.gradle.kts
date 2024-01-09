buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("com.android.tools.build:gradle:8.1.3")
    }
}

tasks.register("Delete", Delete::class) {
    delete(rootProject.buildDir)
}