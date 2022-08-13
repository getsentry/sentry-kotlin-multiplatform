import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.MavenPublishPluginExtension

plugins {
    `maven-publish`
    id("org.jlleitschuh.gradle.ktlint")
    id("com.vanniktech.maven.publish") version "0.18.0"
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
        classpath("com.android.tools.build:gradle:7.2.2")
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }
    group = "io.sentry"
    version = properties["versionName"].toString()
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint") // Version should be inherited from parent

    if (!this.name.contains("samples") && !this.name.contains("shared")) {

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
