include("colormath")
include("test")
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
}
