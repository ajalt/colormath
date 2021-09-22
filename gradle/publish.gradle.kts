apply(plugin = "maven-publish")
apply(plugin = "signing")
apply(plugin = "org.jetbrains.dokka")

repositories {
    mavenCentral()
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.5.30")
    }
}

// Hacks for kts
fun Project.publishing(action: PublishingExtension.() -> Unit) = configure(action)
fun Project.signing(configure: SigningExtension.() -> Unit): Unit = configure(configure)
val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

//
//tasks.named("dokkaHtml").configure {
//    outputDirectory.set(rootDir.resolve("docs/api"))
//    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
//        customStyleSheets = listOf(rootDir.resolve("docs/css/logo-styles.css"))
//        customAssets = listOf(rootDir.resolve("docs/img/palette_black_36dp.svg"))
//        footerMessage = "Copyright &copy; 2021 AJ Alt"
//    }
//    dokkaSourceSets {
//        configureEach {
//            reportUndocumented.set(false)
//            skipDeprecated.set(true)
//        }
//    }
//}

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

val isSnapshot = version.toString().endsWith("SNAPSHOT")
val signingKey: String? by project
val SONATYPE_USERNAME: String? by project
val SONATYPE_PASSWORD: String? by project

afterEvaluate {
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
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
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
            sign(publications)
        }
    }
}
