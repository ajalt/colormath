plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose)
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
                implementation(project(":colormath"))
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}
