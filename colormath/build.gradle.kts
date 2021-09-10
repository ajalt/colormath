@file:Suppress("UNUSED_VARIABLE", "PropertyName")

import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.io.ByteArrayOutputStream

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "1.5.30-dev-115"
    id("maven-publish")
    id("signing")
}

repositories {
    maven("https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.5.30-dev-115")
    }
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
    tvos()
    watchos()

    sourceSets {
        val commonMain by getting

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        listOf("macosX64", "linuxX64", "mingwX64", "ios", "tvos", "watchos").forEach { target ->
            getByName(target + "Main").dependsOn(nativeMain)
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-assertions-core:4.6.2")
            }
        }
    }
}

val jvmJar by tasks.getting(Jar::class) {
    manifest {
        attributes("Automatic-Module-Name" to "com.github.ajalt.colormath")
    }
}

tasks.dokkaHtml.configure {
    outputDirectory.set(rootDir.resolve("docs/api"))
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customStyleSheets = listOf(rootDir.resolve("docs/css/logo-styles.css"))
        customAssets = listOf(rootDir.resolve("docs/img/palette_black_36dp.svg"))
        footerMessage = "Copyright &copy; 2021 AJ Alt"
    }
    dokkaSourceSets {
        configureEach {
            reportUndocumented.set(false)
            skipDeprecated.set(true)
        }
    }
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
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
