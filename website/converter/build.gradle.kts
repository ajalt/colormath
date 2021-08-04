plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "0.5.0-build270"
}


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(rootProject)
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}
