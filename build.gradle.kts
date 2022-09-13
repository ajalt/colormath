import java.io.ByteArrayOutputStream
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask

val VERSION_NAME: String by project

buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.2.1")
    }
}

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("multiplatform").version(libs.versions.kotlin).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.dokka).apply(false)
}

allprojects {
    group = "com.github.ajalt.colormath"
    version = getPublishVersion()

    repositories {
        mavenCentral()
    }

    // Disable npm install scripts
    tasks.withType<KotlinNpmInstallTask> {
        args += "--ignore-scripts"
    }
}

fun getPublishVersion(): String {
    // Call gradle with -PinferVersion to set the dynamic version name. Otherwise we skip it to save time.
    if (!project.hasProperty("inferVersion")) return VERSION_NAME

    val stdout = ByteArrayOutputStream()
    project.exec {
        commandLine = listOf("git", "tag", "--points-at", "master")
        standardOutput = stdout
    }
    val tag = String(stdout.toByteArray()).trim()
    if (tag.isNotEmpty()) return tag

    val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "0"
    return "$VERSION_NAME.$buildNumber-SNAPSHOT"
}
