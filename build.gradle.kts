import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("multiplatform").version(libs.versions.kotlin).apply(false)
    alias(libs.plugins.android.kmp.library).apply(false)
    alias(libs.plugins.dokka).apply(false)
    alias(libs.plugins.publish).apply(false)
    alias(libs.plugins.jetbrainsCompose).apply(false)
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

apiValidation {
    // https://github.com/Kotlin/binary-compatibility-validator/issues/3
    project("scripts").subprojects.mapTo(ignoredProjects) { it.name }
    project("test").subprojects.mapTo(ignoredProjects) { it.name }
    ignoredProjects.add("website")
    ignoredProjects.add("test")
}


fun getPublishVersion(): String {
    val versionName = project.property("VERSION_NAME") as String
    // Call gradle with -PsnapshotVersion to set the version as a snapshot.
    // Otherwise, we skip it to save time.
    if (!project.hasProperty("snapshotVersion")) return versionName
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

    pluginManager.withPlugin("com.vanniktech.maven.publish") {
        apply(plugin = "org.jetbrains.dokka")
        extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
            @Suppress("UnstableApiUsage")
            configure(KotlinMultiplatform(JavadocJar.Empty()))
        }
        extensions.configure<DokkaExtension> {
            val dir = if (project.name == "colormath") "" else "/${project.name}"
            dokkaPublications.named("html") {
                outputDirectory.set(rootProject.rootDir.resolve("docs/api$dir"))
            }
            val rootPath = rootProject.rootDir.toPath()
            val logoCss = rootPath.resolve("docs/css/logo-styles.css").toString().replace('\\', '/')
            val paletteSvg = rootPath.resolve("docs/img/palette_black_36dp.svg").toString()
                .replace('\\', '/')
            pluginsConfiguration.named<DokkaHtmlPluginParameters>("html") {
                customStyleSheets.from(logoCss)
                customAssets.from(paletteSvg)
                footerMessage.set("Copyright &copy; 2021 AJ Alt")
            }
            dokkaSourceSets.configureEach {
                skipDeprecated.set(true)
            }
        }
    }
}
