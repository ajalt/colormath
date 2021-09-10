include("colormath")
include("website:converter")
include("website:gradient")
include("scripts:benchmarks")

// For compose web and dokka dev
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
    }
}
