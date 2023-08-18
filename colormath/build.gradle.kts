import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.publish)
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js { nodejs() }

    linuxX64()
    linuxArm64()
    mingwX64()
    macosX64()
    macosArm64()

    ios()
    iosSimulatorArm64()
    tvos()
    tvosSimulatorArm64()
    watchos()
    watchosSimulatorArm64()

    sourceSets {
        val commonMain by getting

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        listOf(
            "macosX64",
            "macosArm64",
            "linuxX64",
            "mingwX64",
            "ios",
            "iosSimulatorArm64",
            "tvos",
            "tvosSimulatorArm64",
            "watchos",
            "watchosSimulatorArm64"
        ).forEach { target ->
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

tasks.withType<Jar>().configureEach {
    manifest {
        attributes("Automatic-Module-Name" to "com.github.ajalt.colormath")
    }
}
