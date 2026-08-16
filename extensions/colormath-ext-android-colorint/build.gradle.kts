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
        namespace = "com.github.ajalt.colormath.extensions.android.colorint"
        compileSdk = 36
        minSdk = 21
        withHostTestBuilder {}
    }

    sourceSets {
        androidMain.dependencies {
            api(project(":colormath"))
            api(libs.androidx.annotation)
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.junit)
            implementation(libs.robolectric)
        }
    }
}
