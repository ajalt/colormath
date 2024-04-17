import com.vanniktech.maven.publish.tasks.JavadocJar
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

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
    androidTarget {
        publishLibraryVariants("release")
    }

    jvm()
    js { nodejs() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { nodejs() }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":colormath"))
            api(libs.compose.ui.graphics)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

@Suppress("UnstableApiUsage")
android {
    namespace = "com.github.ajalt.colormath.extensions.android.composecolor"
    compileSdk = 33
    defaultConfig.minSdk = 21
    buildFeatures.buildConfig = false
}

// workaround for https://github.com/Kotlin/dokka/issues/1833
tasks.withType<JavadocJar>().configureEach {
    dependsOn(project.tasks.getByPath(":colormath:dokkaHtml"))
}
