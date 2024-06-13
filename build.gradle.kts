buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
        classpath("com.android.tools.build:gradle:8.5.0")
    }
}

tasks.register("Delete", Delete::class) {
    delete(rootProject.buildDir)
}