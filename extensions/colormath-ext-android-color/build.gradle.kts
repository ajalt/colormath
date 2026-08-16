plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.publish)
}


repositories {
    mavenCentral()
    google()
}

kotlin {
    android {
        namespace = "com.github.ajalt.colormath.extensions.android.color"
        compileSdk = 36
        minSdk = 26 // Color instances were added in 26
        withHostTestBuilder {}
    }

    sourceSets {
        androidMain.dependencies {
            api(project(":colormath"))
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.junit)
            implementation(libs.robolectric)
        }
    }
}
