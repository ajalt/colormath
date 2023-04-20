@file:Suppress("UNUSED_VARIABLE", "PropertyName")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR) {
        nodejs()
        browser()
    }

    linuxX64()
    mingwX64()
    macosX64()
    macosArm64()

    ios()
    iosSimulatorArm64()
    tvos()
    watchos()

    sourceSets {
        val commonMain by getting

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        listOf("macosX64", "macosArm64", "linuxX64", "mingwX64", "ios", "iosSimulatorArm64", "tvos", "watchos")
            .forEach { target ->
                getByName(target + "Main").dependsOn(nativeMain)
            }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotest)
            }
        }
    }
}

val jvmJar by tasks.getting(Jar::class) {
    manifest {
        attributes("Automatic-Module-Name" to "com.github.ajalt.colormath")
    }
}

apply(from = "../gradle/dokka.gradle")
apply(from = "../gradle/publish.gradle.kts")
