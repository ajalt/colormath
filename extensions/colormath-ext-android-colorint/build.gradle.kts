plugins {
    id("com.android.library")
    kotlin("android")
}

repositories {
    mavenCentral()
    google()
}


android {
    compileSdk = 31
    defaultConfig {
        minSdk = 16
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
    api(libs.androidx.annotation)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
}

apply(from = "../../gradle/dokka.gradle")
apply(from = "../../gradle/publish-android.gradle")
apply(from = "../../gradle/publish.gradle.kts")
