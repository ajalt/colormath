import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform").version("1.4.10")
    id("org.jetbrains.dokka").version("0.10.1")
}

group = "com.github.ajalt.clikt"
version = "2.0.0"

repositories {
    mavenCentral()
    jcenter() // for dokka
}

kotlin {
    jvm()
    js {
        nodejs()
        browser()
    }

    linuxX64()
    mingwX64()
    macosX64()

    sourceSets {
        val commonTest by getting {
            dependencies {
                api(kotlin("test-common"))
                api(kotlin("test-annotations-common"))
                api("io.kotest:kotest-assertions-core:4.2.4")
            }
        }

        val jvmTest by getting {
            dependencies {
                api(kotlin("test-junit"))
            }
        }

        val jsTest by getting {
            dependencies {
                api(kotlin("test-js"))
            }
        }
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}
