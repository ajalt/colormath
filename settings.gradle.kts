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

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.7.10")

            plugin("dokka", "org.jetbrains.dokka").version("1.7.10")

            // used in tests
            library("kotest", "io.kotest:kotest-assertions-core:5.4.2")

            // used in extensions
            plugin("android-library", "com.android.library").version("7.2.1")

            library("compose-ui-graphics", "androidx.compose.ui:ui-graphics:1.2.1")
            library("androidx-annotation", "androidx.annotation:annotation:1.4.0")
            library("junit", "junit:junit:4.13.2")
            library("robolectric", "org.robolectric:robolectric:4.4")

            // used in samples
            plugin("compose", "org.jetbrains.compose").version("1.1.1")

        }
    }
}
