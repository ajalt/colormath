import com.android.build.gradle.BaseExtension
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.ByteArrayOutputStream

plugins {
    kotlin("multiplatform").version(libs.versions.kotlin).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.dokka).apply(false)
    alias(libs.plugins.publish).apply(false)
    alias(libs.plugins.compose).apply(false)
}

fun getPublishVersion(): String {
    val versionName = project.property("VERSION_NAME") as String

    // Call gradle with -PinferVersion to set the dynamic version name.
    // Otherwise, we skip it to save time.
    if (!project.hasProperty("inferVersion")) return versionName

    val stdout = ByteArrayOutputStream()
    project.exec {
        commandLine = listOf("git", "tag", "--points-at", "master")
        standardOutput = stdout
    }
    val tag = String(stdout.toByteArray()).trim()
    if (tag.isNotEmpty()) return tag

    val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "0"
    return "$versionName.$buildNumber-SNAPSHOT"
}


subprojects {
    project.setProperty("VERSION_NAME", getPublishVersion())

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    tasks.withType<JavaCompile>().configureEach {
        options.release.set(8)
    }

    plugins.withType<com.android.build.gradle.BasePlugin>().configureEach {
        configure<BaseExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        }
    }

    pluginManager.withPlugin("com.vanniktech.maven.publish") {
        apply(plugin = "org.jetbrains.dokka")
        tasks.named<DokkaTask>("dokkaHtml") {
            val dir = if (project.name == "colormath") "" else "/${project.name}"
            outputDirectory.set(rootProject.rootDir.resolve("docs/api$dir"))
            val rootPath = rootProject.rootDir.toPath()
            val logoCss = rootPath.resolve("docs/css/logo-styles.css").toString().replace('\\', '/')
            val paletteSvg = rootPath.resolve("docs/img/palette_black_36dp.svg").toString()
                .replace('\\', '/')
            pluginsMapConfiguration.set(
                mapOf(
                    "org.jetbrains.dokka.base.DokkaBase" to """{
                    "customStyleSheets": ["$logoCss"],
                    "customAssets": ["$paletteSvg"],
                    "footerMessage": "Copyright &copy; 2021 AJ Alt"
                }"""
                )
            )
            dokkaSourceSets.configureEach {
                skipDeprecated.set(true)
            }
        }
    }
}
