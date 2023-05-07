@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    android {
        publishLibraryVariants("release")
    }

    sourceSets {
        val androidMain by getting  {
            dependencies {
                api(project(":colormath"))
                api(libs.androidx.annotation)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.robolectric)
            }
        }
    }
}

android {
    namespace = "com.github.ajalt.colormath"
    compileSdk = 33

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}


apply(from = "../../gradle/dokka.gradle")
apply(from = "../../gradle/publish.gradle.kts")
