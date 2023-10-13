import com.vanniktech.maven.publish.tasks.JavadocJar

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

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":colormath"))
                api(libs.compose.ui.graphics)
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
    namespace = "com.github.ajalt.colormath.extensions.android.composecolor"
    compileSdk = 33
    defaultConfig.minSdk = 21
}

// workaround for https://github.com/Kotlin/dokka/issues/1833
tasks.withType<JavadocJar>().configureEach {
    dependsOn(project.tasks.getByPath(":colormath:dokkaHtml"))
}
