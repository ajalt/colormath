import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("multiplatform")
}

// kotest 6 requires JVM 11+
tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js { nodejs() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { nodejs() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi { nodejs() }

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
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()

    sourceSets {
        commonTest.dependencies {
            implementation(project(":colormath"))
            implementation(libs.kotest.assertions)
            implementation(libs.kotest.tables)
            implementation(kotlin("test"))
        }
    }
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes("Automatic-Module-Name" to "com.github.ajalt.colormath")
    }
}
