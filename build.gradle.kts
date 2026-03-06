import com.diffplug.spotless.LineEnding
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.MavenPublishPluginExtension
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaTask
import java.util.zip.ZipFile

plugins {
    id(Config.gradleMavenPublishPlugin).version(Config.gradleMavenPublishPluginVersion)
    id(Config.QualityPlugins.spotless).version(Config.QualityPlugins.spotlessVersion)
    id(Config.QualityPlugins.detekt).version(Config.QualityPlugins.detektVersion)
    id(Config.dokka).version(Config.dokkaVersion)
    kotlin(Config.multiplatform).version(Config.kotlinVersion).apply(false)
    kotlin(Config.cocoapods).version(Config.kotlinVersion).apply(false)
    id(Config.jetpackCompose).version(Config.composePluginVersion).apply(false)
    id(Config.kotlinCompose).version(Config.kotlinVersion).apply(false)
    id(Config.androidGradle).version(Config.agpVersion).apply(false)
    id(Config.BuildPlugins.buildConfig).version(Config.BuildPlugins.buildConfigVersion).apply(false)
    kotlin(Config.kotlinSerializationPlugin).version(Config.kotlinVersion).apply(false)
    id(Config.QualityPlugins.kover).version(Config.QualityPlugins.koverVersion).apply(false)
    id(Config.QualityPlugins.binaryCompatibility).version(Config.QualityPlugins.binaryCompatibilityVersion)
        .apply(false)
}

allprojects {
    group = Config.Sentry.group
    version = properties["versionName"].toString()
}

subprojects {
    if (this.name.contains("sentry-kotlin-multiplatform")) {
        apply<DistributionPlugin>()

        val sep = File.separator
        // The path where we want publishToMavenLocal to output the artifacts to
        val buildPublishDir = "${project.layout.buildDirectory.get().asFile.path}${sep}sentry-local-publish$sep"

        configure<DistributionContainer> {
            configureForMultiplatform(this@subprojects, buildPublishDir)
        }

        tasks.named("distZip").configure {
            System.setProperty("maven.repo.local", buildPublishDir)
            dependsOn("publishToMavenLocal")
            doLast {
                val distributionFilePath =
                    "${project.layout.buildDirectory.get().asFile.path}${sep}distributions${sep}${project.name}-${project.version}.zip"
                val file = File(distributionFilePath)
                if (!file.exists()) throw GradleException("Distribution file: $distributionFilePath does not exist")
                if (file.length() == 0L) throw GradleException("Distribution file: $distributionFilePath is empty")
            }
        }

        afterEvaluate {
            val platformDists = project.tasks.filter { task ->
                task.name.matches(Regex("(.*)DistZip"))
            }.toTypedArray()
            project.tasks.getByName("distZip").finalizedBy(*platformDists)

            apply<MavenPublishPlugin>()

            configure<MavenPublishPluginExtension> {
                // signing is done when uploading files to MC
                // via gpg:sign-and-deploy-file (release.kts)
                releaseSigningEnabled = false
            }
        }
    }
}

tasks.register("validateDistributions") {
    subprojects {
        val subproject = this@subprojects
        if (subproject.name == "sentry-kotlin-multiplatform") {
            subproject.validateKotlinMultiplatformCoreArtifacts()
        }
    }
}

private fun Project.validateKotlinMultiplatformCoreArtifacts() {
    val distributionDir = project.layout.buildDirectory.dir("distributions").get().asFile
    val expectedNumOfFiles = 20
    val filesList = distributionDir.listFiles()
    val actualNumOfFiles = filesList?.size ?: 0

    if (actualNumOfFiles == expectedNumOfFiles) {
        println("✅ Found $actualNumOfFiles distribution files as expected.")
    } else {
        throw GradleException("❌ Expected $expectedNumOfFiles distribution files, but found $actualNumOfFiles")
    }

    val baseFileName = "sentry-kotlin-multiplatform"
    val platforms = listOf(
        "watchosx64", "watchossimulatorarm64", "watchosarm64", "watchosarm32",
        "tvosx64", "tvossimulatorarm64", "tvosarm64",
        "macosx64", "macosarm64",
        "jvm",
        "iosx64", "iossimulatorarm64", "iosarm64",
        "android",
        "js",
        "wasm-js",
        "linuxx64", "linuxarm64",
        "mingwx64"
    )

    val artifactPaths = buildList {
        add(distributionDir.resolve("$baseFileName-$version.zip"))
        addAll(
            platforms.map { platform ->
                distributionDir.resolve("$baseFileName-$platform-$version.zip")
            }
        )
    }

    val commonRequiredEntries = listOf(
        "javadoc",
        "sources",
        "module",
        "pom-default.xml"
    )

    artifactPaths.forEach { artifactFile ->
        if (!artifactFile.exists()) {
            throw GradleException("❌ Artifact file: ${artifactFile.path} does not exist")
        }
        if (artifactFile.length() == 0L) {
            throw GradleException("❌ Artifact file: ${artifactFile.path} is empty")
        }

        ZipFile(artifactFile).use { zip ->
            val entries = zip.entries().asSequence().map { it.name }.toList()

            commonRequiredEntries.forEach { requiredEntry ->
                if (entries.none { it.contains(requiredEntry) }) {
                    throw GradleException("❌ $requiredEntry not found in ${artifactFile.name}")
                } else {
                    println("✅ Found $requiredEntry in ${artifactFile.name}")
                }
            }

            when {
                artifactFile.name.contains("ios", ignoreCase = true) ||
                    artifactFile.name.contains("macos", ignoreCase = true) ||
                    artifactFile.name.contains("watchos", ignoreCase = true) ||
                    artifactFile.name.contains("tvos", ignoreCase = true) -> {
                    val expectedNumOfKlibFiles = 3
                    val actualKlibFiles = entries.count { it.contains("klib") }
                    if (actualKlibFiles != expectedNumOfKlibFiles) {
                        throw GradleException("❌ Expected $expectedNumOfKlibFiles klib files in ${artifactFile.name}, but found $actualKlibFiles")
                    } else {
                        println("✅ Found $expectedNumOfKlibFiles klib files in ${artifactFile.name}")
                    }
                }

                artifactFile.name.contains("android", ignoreCase = true) -> {
                    if (entries.none { it.contains("aar") }) {
                        throw GradleException("❌ aar file not found in ${artifactFile.name}")
                    } else {
                        println("✅ Found aar file in ${artifactFile.name}")
                    }
                }
            }
        }
    }
}

subprojects {
    if (project.name.contains("sentry-kotlin-multiplatform")) {
        tasks.withType<DokkaTask>().configureEach {
            dokkaSourceSets.configureEach {
                if (name.endsWith("Test")) {
                    suppress.set(true)
                }
            }
        }
        apply(plugin = Config.dokka)
    }
}

spotless {
    lineEndings = LineEnding.UNIX

    kotlin {
        target("**/*.kt")
        targetExclude("**/generated/**/*.kt")
        ktlint()
    }
    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/generated/**/*.kts")
        ktlint()
    }
}

val detektConfigFilePath = "$rootDir/config/detekt/detekt.yml"
val detektBaselineFilePath = "$rootDir/config/detekt/baseline.xml"

detekt {
    buildUponDefaultConfig = true
    config = files(detektConfigFilePath)
    baseline = file(detektBaselineFilePath)
}

fun SourceTask.detektExcludes() {
    exclude("**/build/**")
    exclude("**/*.kts")
    exclude("**/buildSrc/**")
    exclude("**/*Test*/**")
    exclude("**/resources/**")
    exclude("**/sentry-samples/**")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
    }
    setSource(files(project.projectDir))
    detektExcludes()
}

/** Task for generating a Detekt baseline.xml */
val detektProjectBaseline by tasks.registering(io.gitlab.arturbosch.detekt.DetektCreateBaselineTask::class) {
    buildUponDefaultConfig.set(true)
    setSource(files(rootDir))
    config.setFrom(files(detektConfigFilePath))
    baseline.set(file(detektBaselineFilePath))
    include("**/*.kt")
    detektExcludes()
}

subprojects {
    tasks.withType<AbstractTestTask>().configureEach {
        if (System.getenv("CI") != "true") {
            filter.excludeTestsMatching("*E2E*")
        }
    }
}

// Configure tasks so it is also run for the plugin
tasks.getByName("detekt") {
    gradle.includedBuild("sentry-kotlin-multiplatform-gradle-plugin").task(":detekt")
}
