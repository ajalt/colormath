plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.github.ajalt.colormath.limitfinder.MainKt")
}

dependencies {
    api(kotlin("stdlib"))
    implementation(rootProject)
}

