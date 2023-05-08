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
