import com.vanniktech.maven.publish.tasks.JavadocJar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    alias(libs.plugins.publish)
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
        val androidMain by getting {
            dependencies {
                api(project(":colormath"))
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
        minSdk = 26 // Color instances were added in 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
