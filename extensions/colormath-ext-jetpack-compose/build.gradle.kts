import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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
        namespace = "com.github.ajalt.colormath.extensions.android.composecolor"
        compileSdk = 36
        minSdk = 21
    }

    jvm()
    js { nodejs() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { nodejs() }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":colormath"))
            api(libs.compose.ui.graphics)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
