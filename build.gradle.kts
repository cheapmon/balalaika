buildscript {
    val kotlinVersion by extra { "1.3.61" }
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath("com.android.tools.build:gradle:4.1.0-alpha02")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.1.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

apply("tasks.gradle.kts")
