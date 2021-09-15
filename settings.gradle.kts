include("colormath")
include("extensions:android-color")
include("extensions:android-colorint")
include("website:converter")
include("website:gradient")
include("scripts:benchmarks")

rootProject.name = "colormath-root"

// For compose web and dokka dev
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
