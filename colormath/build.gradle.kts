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
    js(BOTH) {
        nodejs()
        browser()
    }

    linuxX64()
    mingwX64()
    macosX64()
    macosArm64()

    ios()
    tvos()
    watchos()

    sourceSets {
        val commonMain by getting

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        listOf("macosX64", "macosArm64", "linuxX64", "mingwX64", "ios", "tvos", "watchos").forEach { target ->
            getByName(target + "Main").dependsOn(nativeMain)
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-assertions-core:5.0.2")
            }
        }
    }

    targets.withType<KotlinNativeTargetWithTests<*>> {
        binaries {
            // Configure a separate test where code runs in background
            test("background", setOf(NativeBuildType.DEBUG)) {
                freeCompilerArgs = freeCompilerArgs + "-trw"
            }
        }
        testRuns {
            val background by creating {
                setExecutionSourceFrom(binaries.getByName("backgroundDebugTest") as TestExecutable)
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
