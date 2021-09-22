include("colormath")
include("extensions:colormath-ext-android-color")
include("extensions:colormath-ext-android-colorint")
include("website:converter")
include("website:gradient")
include("scripts:benchmarks")

rootProject.name = "colormath-root"

// For compose web
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
