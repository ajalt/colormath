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
    android {
        publishLibraryVariants("release")
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                api(project(":colormath"))
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
    namespace = "com.github.ajalt.colormath"
    compileSdk = 33
    defaultConfig.minSdk = 26 // Color instances were added in 26
}
