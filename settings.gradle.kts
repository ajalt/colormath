include("colormath")
include("extensions:colormath-ext-android-color")
include("extensions:colormath-ext-android-colorint")
include("extensions:colormath-ext-jetpack-compose")
include("website:converter")
include("website:gradient")
include("scripts:benchmarks")

rootProject.name = "colormath-root"

// For compose web
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.8.20")
            version("jbCompose", "1.4.0")

            plugin("dokka", "org.jetbrains.dokka").version("1.8.10")

            // used in tests
            library("kotest", "io.kotest:kotest-assertions-core:5.6.1")

            // used in extensions
            plugin("android-library", "com.android.library").version("7.4.0")

            library("compose-ui-graphics", "org.jetbrains.compose.ui", "ui-graphics").versionRef("jbCompose")
            library("androidx-annotation", "androidx.annotation:annotation:1.6.0")
            library("junit", "junit:junit:4.13.2")
            library("robolectric", "org.robolectric:robolectric:4.10")

            // used in samples
            plugin("compose", "org.jetbrains.compose").versionRef("jbCompose")
        }
    }
}
