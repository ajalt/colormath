@file:Suppress("UNUSED_VARIABLE", "PropertyName")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    kotlin("multiplatform") version "1.4.31"
    id("org.jetbrains.dokka") version "0.10.1"
    id("maven-publish")
    id("signing")
}

val VERSION_NAME: String by project

group = "com.github.ajalt.colormath"
version = getPublishVersion()

repositories {
    mavenCentral()
    jcenter() // for dokka
}

kotlin {
    jvm()
    js(BOTH) {
        nodejs()
        browser()
    }

    linuxX64()
    mingwX64()
    macosX64()

    ios()
    watchos()
    tvos()

    sourceSets {
        val commonTest by getting {
            dependencies {
                api(kotlin("test-common"))
                api(kotlin("test-annotations-common"))
                api("io.kotest:kotest-assertions-core:4.4.3")
            }
        }

        val jvmTest by getting {
            dependencies {
                api(kotlin("test-junit"))
            }
        }

        val jsTest by getting {
            dependencies {
                api(kotlin("test-js"))
            }
        }
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}


fun getPublishVersion(): String {
    // Call gradle with -PinferVersion to set the dynamic version name. Otherwise we skip it to save time.
    if (!project.hasProperty("inferVersion")) return VERSION_NAME

    val stdout = ByteArrayOutputStream()
    project.exec {
        commandLine = listOf("git", "tag", "--points-at", "master")
        standardOutput = stdout
    }
    val tag = String(stdout.toByteArray()).trim()
    if (tag.isNotEmpty()) return tag

    val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "0"
    return "$VERSION_NAME.$buildNumber-SNAPSHOT"
}


val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

val isSnapshot = version.toString().endsWith("SNAPSHOT")
val signingKey: String? by project
val SONATYPE_USERNAME: String? by project
val SONATYPE_PASSWORD: String? by project

publishing {
    publications.withType<MavenPublication>().all {
        pom {
            description.set("Multiplatform color space conversions for Kotlin")
            name.set("Colormath")
            url.set("https://github.com/ajalt/colormath")
            scm {
                url.set("https://github.com/ajalt/colormath")
                connection.set("scm:git:git://github.com/ajalt/colormath.git")
                developerConnection.set("scm:git:ssh://git@github.com/ajalt/colormath.git")
            }
            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("ajalt")
                    name.set("AJ Alt")
                    url.set("https://github.com/ajalt")
                }
            }
        }
    }

    repositories {
        val releaseUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        val snapshotUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
        maven {
            url = if (isSnapshot) snapshotUrl else releaseUrl
            credentials {
                username = SONATYPE_USERNAME ?: ""
                password = SONATYPE_PASSWORD ?: ""
            }
        }
    }

    publications.withType<MavenPublication>().all {
        artifact(emptyJavadocJar.get())
    }
}

signing {
    isRequired = !isSnapshot

    if (signingKey != null && !isSnapshot) {
        useInMemoryPgpKeys(signingKey, "")
        sign(publishing.publications)
    }
}
