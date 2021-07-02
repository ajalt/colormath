include("website:converter")
include("website:gradient")
include("scripts:limitfinder")
include("scripts:benchmarks")

// For compose web
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
