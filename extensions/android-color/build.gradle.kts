@file:Suppress("UNUSED_VARIABLE", "PropertyName")

plugins {
    id("com.android.library")
    kotlin("android")
}

repositories {
    mavenCentral()
    google()
}


android {
    compileSdk = 30
    defaultConfig {
        minSdk = 26
        targetSdk = 30
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    useLibrary("android.test.base")
}

dependencies {
    api(project(":colormath"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.4")
}

apply(from = "../../gradle/publish.gradle.kts")
