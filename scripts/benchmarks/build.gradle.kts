plugins {
    kotlin("jvm")
    id( "me.champeau.jmh") version "0.7.3"
}

dependencies {
    api(kotlin("stdlib"))
    implementation(rootProject)
}
