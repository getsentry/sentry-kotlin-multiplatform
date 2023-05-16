import com.diffplug.spotless.LineEnding
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.MavenPublishPluginExtension
import io.gitlab.arturbosch.detekt.Detekt

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
    id(Config.QualityPlugins.binaryCompatibility).version(Config.QualityPlugins.binaryCompatibilityVersion).apply(false)
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
            apply<MavenPublishPlugin>()

            configure<MavenPublishPluginExtension> {
                // signing is done when uploading files to MC
                // via gpg:sign-and-deploy-file (release.kts)
                releaseSigningEnabled = false
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
        ktlint()
    }
    kotlinGradle {
        target("**/*.kts")
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
