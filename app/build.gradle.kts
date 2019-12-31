plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("androidx.navigation.safeargs")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.2"

    defaultConfig {
        applicationId = "com.github.cheapmon.balalaika"
        minSdkVersion(15)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        incremental = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(
            group = "org.jetbrains.kotlin",
            name = "kotlin-stdlib-jdk7",
            version = "${rootProject.extra["kotlinVersion"]}"
    )
    implementation(
            group = "androidx.core",
            name = "core-ktx",
            version = "1.1.0"
    )
    implementation(
            group = "androidx.legacy",
            name = "legacy-support-v4",
            version = "1.0.0"
    )
    implementation(
            group = "com.google.android.material",
            name = "material",
            version = "1.0.0"
    )
    implementation(
            group = "androidx.constraintlayout",
            name = "constraintlayout",
            version = "1.1.3"
    )
    implementation(
            group = "androidx.navigation",
            name = "navigation-fragment",
            version = "2.1.0"
    )
    implementation(
            group = "androidx.navigation",
            name = "navigation-ui",
            version = "2.1.0"
    )
    implementation(
            group = "androidx.lifecycle",
            name = "lifecycle-extensions",
            version = "2.1.0"
    )
    implementation(
            group = "androidx.navigation",
            name = "navigation-fragment-ktx",
            version = "2.1.0"
    )
    implementation(
            group = "androidx.navigation",
            name = "navigation-ui-ktx",
            version = "2.1.0"
    )
    testImplementation(
            group = "junit",
            name = "junit",
            version = "4.12"
    )
    androidTestImplementation(
            group = "androidx.test.ext",
            name = "junit",
            version = "1.1.1"
    )
    androidTestImplementation(
            group = "androidx.test.espresso",
            name = "espresso-core",
            version = "3.2.0"
    )
    implementation(
            group = "org.apache.commons",
            name = "commons-csv",
            version = "1.7"
    )
    val roomVersion by extra { "2.2.2" }
    implementation(group = "androidx.room", name = "room-runtime", version = roomVersion)
    kapt(group = "androidx.room", name = "room-compiler", version = roomVersion)
    implementation("androidx.room:room-ktx:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0-rc03")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-rc03")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0-rc03")
}
