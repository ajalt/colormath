include("colormath")
include("test")
include("extensions:colormath-ext-android-color")
include("extensions:colormath-ext-android-colorint")
include("extensions:colormath-ext-jetpack-compose")
include("website")
include("scripts:benchmarks")

rootProject.name = "colormath-root"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
