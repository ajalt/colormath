@file:Suppress("UNUSED_VARIABLE", "PropertyName")

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
                implementation("io.kotest:kotest-assertions-core:4.6.2")
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
