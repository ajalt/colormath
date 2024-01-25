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
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
    watchosX64()
    watchosArm64()
    watchosSimulatorArm64()

    sourceSets {
        val commonTest by getting {
            dependencies {
                api(project(":colormath"))
                implementation(libs.kotest)
                implementation(kotlin("test"))
            }
        }
    }
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes("Automatic-Module-Name" to "com.github.ajalt.colormath")
    }
}
