plugins {
    kotlin("jvm")
    id( "me.champeau.jmh") version "0.6.5"
}

dependencies {
    api(kotlin("stdlib"))
    implementation(rootProject)
}
