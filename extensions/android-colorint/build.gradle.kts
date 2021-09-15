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
        minSdk = 16
        targetSdk = 30
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    api(project(":colormath"))
    api("androidx.annotation:annotation:1.2.0")
}
