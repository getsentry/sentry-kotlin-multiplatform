import com.diffplug.spotless.LineEnding
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.MavenPublishPluginExtension

plugins {
    id(Config.gradleMavenPublishPlugin).version(Config.gradleMavenPublishPluginVersion)
    id(Config.QualityPlugins.spotless).version(Config.QualityPlugins.spotlessVersion)
    kotlin(Config.multiplatform).version(Config.kotlinVersion).apply(false)
    kotlin(Config.cocoapods).version(Config.kotlinVersion).apply(false)
    id(Config.jetpackCompose).version(Config.composeVersion).apply(false)
    id(Config.androidGradle).version(Config.agpVersion).apply(false)
    id(Config.BuildPlugins.buildConfig).version(Config.BuildPlugins.buildConfigVersion).apply(false)
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


tasks.register("updateReadme") {
    val filename = "README.md"
    val kmpRegex = Regex("io\\.sentry:sentry-kotlin-multiplatform:[^\"]*")
    val cocoaRegex = Regex("pod\\(\"Sentry\", \"~>[^\"]*\"\\)")

    doLast {
        try {
            val readmeFile = File(filename)

            val newContents = readmeFile.readText()
                .replace(kmpRegex, "io.sentry:sentry-kotlin-multiplatform:${project.version}")
                .replace(cocoaRegex, "pod(\"Sentry\", \"~> ${Config.Libs.sentryCocoaVersion}\")")

            readmeFile.writeText(newContents)
        } catch (e: Exception) {
            throw GradleException("Failed to update README file: ${e.message}")
        }
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
