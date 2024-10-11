import com.diffplug.spotless.LineEnding
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.MavenPublishPluginExtension
import io.gitlab.arturbosch.detekt.Detekt
import java.util.zip.ZipFile

plugins {
    id(Config.gradleMavenPublishPlugin).version(Config.gradleMavenPublishPluginVersion)
    id(Config.QualityPlugins.spotless).version(Config.QualityPlugins.spotlessVersion)
    id(Config.QualityPlugins.detekt).version(Config.QualityPlugins.detektVersion)
    id(Config.dokka).version(Config.dokkaVersion)
    kotlin(Config.multiplatform).version(Config.kotlinVersion).apply(false)
    kotlin(Config.cocoapods).version(Config.kotlinVersion).apply(false)
    id(Config.jetpackCompose).version(Config.composeVersion).apply(false)
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

        configure<DistributionContainer> {
            this.configureForMultiplatform(this@subprojects)
        }

        tasks.named("distZip").configure {
            this.dependsOn("publishToMavenLocal")
            this.doLast {
                val distributionFilePath =
                    "${this.project.buildDir}${sep}distributions${sep}${this.project.name}-${this.project.version}.zip"
                val file = File(distributionFilePath)
                if (!file.exists()) throw IllegalStateException("Distribution file: $distributionFilePath does not exist")
                if (file.length() == 0L) throw IllegalStateException("Distribution file: $distributionFilePath is empty")
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
            validateKotlinMultiplatformCoreArtifacts(subproject)
        }
    }
}

private fun validateKotlinMultiplatformCoreArtifacts(project: Project) {
    val rootDistributionFilePath =
        "${project.layout.buildDirectory.asFile.get().path}${sep}distributions"
    val expectedNumOfFiles = 15
    val filesList = File(rootDistributionFilePath).listFiles()
    val actualNumberOfFiles = filesList?.size ?: 0

    if (actualNumberOfFiles == expectedNumOfFiles) {
        println("✅ Found $actualNumberOfFiles distribution files as expected.")
    } else {
        throw IllegalStateException("❌ Expected $expectedNumOfFiles distribution files, but found $actualNumberOfFiles")
    }

    val baseFileName = "sentry-kotlin-multiplatform"
    val platforms = listOf(
        "watchosx64", "watchossimulatorarm64", "watchosarm64", "watchosarm32",
        "tvosx64", "tvossimulatorarm64", "tvosarm64",
        "macosx64", "macosarm64",
        "jvm",
        "iosx64", "iossimulatorarm64", "iosarm64",
        "android"
    )
    val listOfArtifactPaths = buildList {
        add("$rootDistributionFilePath$sep$baseFileName-$version.zip")
        addAll(
            platforms.map { platform ->
                "$rootDistributionFilePath$sep$baseFileName-$platform-$version.zip"
            }
        )
    }

    listOfArtifactPaths.forEach { artifactPath ->
        val artifactFile = File(artifactPath)
        if (!artifactFile.exists()) {
            throw IllegalStateException("❌ Artifact file: $artifactPath does not exist")
        }
        if (artifactFile.length() == 0L) {
            throw IllegalStateException("❌ Artifact file: $artifactPath is empty")
        }

        val file = File(artifactPath)
        ZipFile(file).use { zip ->
            val entries = zip.entries().asSequence().map { it.name }.toList()
            val javadocFile = entries.firstOrNull { it.contains("javadoc") }
            if (javadocFile == null) {
                throw IllegalStateException("❌ javadoc file not found in ${file.name}")
            }
            val sourcesFile = entries.firstOrNull { it.contains("sources") }
            if (sourcesFile == null) {
                throw IllegalStateException("❌ sources file not found in ${file.name}")
            }
            val moduleFile = entries.firstOrNull { it.contains("module") }
            if (moduleFile == null) {
                throw IllegalStateException("❌ module file not found in ${file.name}")
            }
            val pomFile = entries.firstOrNull { it.contains("pom-default.xml") }
            if (pomFile == null) {
                throw IllegalStateException("❌ pom file not found in ${file.name}")
            }

            val isAppleArtifact =
                file.name.contains("ios") || file.name.contains("macos") || file.name.contains("watchos") || file.name.contains(
                    "tvos"
                )
            if (isAppleArtifact) {
                // this is hardcoded but will probably not change unless we add another cinterop library or remove one
                val expectedNumOfKlibFiles = 3
                val actualKlibFiles = entries.filter { it.contains("klib") }
                if (actualKlibFiles.size != expectedNumOfKlibFiles) {
                    throw IllegalStateException("❌ $expectedNumOfKlibFiles klib files not found in ${file.name}")
                } else {
                    println("✅ Found $expectedNumOfKlibFiles klib files in ${file.name}")
                }
            }

            val isAndroidArtifact = file.name.contains("android")
            if (isAndroidArtifact) {
                val aarFile = entries.firstOrNull() { it.contains("aar") }
                if (aarFile == null) {
                    throw IllegalStateException("❌ aar file not found in ${file.name}")
                } else {
                    println("✅ Found aar file in ${file.name}")
                }
            }
        }
    }
}

subprojects {
    if (project.name.contains("sentry-kotlin-multiplatform")) {
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

// Configure tasks so it is also run for the plugin
tasks.getByName("detekt") {
    gradle.includedBuild("sentry-kotlin-multiplatform-gradle-plugin").task(":detekt")
}
