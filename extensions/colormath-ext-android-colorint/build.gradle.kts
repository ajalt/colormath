plugins {
    id("com.android.library")
    kotlin("multiplatform")
    alias(libs.plugins.publish)
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        val androidMain by getting  {
            dependencies {
                api(project(":colormath"))
                api(libs.androidx.annotation)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.robolectric)
            }
        }
    }
}

android {
    namespace = "com.github.ajalt.colormath.extensions.android.colorint"
    compileSdk = 33
    defaultConfig.minSdk = 21
}
