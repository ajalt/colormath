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
        minSdk = 26
        targetSdk = 31
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
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.7.2")
}

apply(from = "../../gradle/dokka.gradle")
apply(from = "../../gradle/publish-android.gradle")
apply(from = "../../gradle/publish.gradle.kts")
