import com.vanniktech.maven.publish.MavenPublishPluginExtension
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
    `java-gradle-plugin`
    alias(libs.plugins.vanniktech.publish)
    id("distribution")
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kover)
}

version = property("versionName").toString()

group = property("group").toString()

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(gradleApi())
    compileOnly(kotlin("gradle-plugin"))

    testImplementation(kotlin("gradle-plugin"))
    testImplementation(libs.junit)
    testImplementation(libs.junit.params)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> { kotlinOptions { jvmTarget = JavaVersion.VERSION_11.toString() } }

gradlePlugin {
    plugins {
        create(property("id").toString()) {
            id = property("id").toString()
            implementationClass = property("implementationClass").toString()
        }
    }
}

val publish = extensions.getByType(MavenPublishPluginExtension::class.java)
// signing is done when uploading files to MC
// via gpg:sign-and-deploy-file (release.kts)
publish.releaseSigningEnabled = false

tasks.named("distZip").configure {
    dependsOn("publishToMavenLocal")
    this.doLast {
        val distributionFilePath =
            "${project.layout.buildDirectory.asFile.get().path}${sep}distributions${sep}${project.name}-${project.version}.zip"
        val file = File(distributionFilePath)
        if (!file.exists()) {
            throw IllegalStateException("Distribution file: $distributionFilePath does not exist")
        }
        if (file.length() == 0L) {
            throw IllegalStateException("Distribution file: $distributionFilePath is empty")
        }
    }
}

val sep = File.separator

distributions {
    main {
        contents {
            from("build${sep}libs")
            from("build${sep}publications${sep}maven")
        }
    }
}

buildConfig {
    useKotlinOutput()
    packageName("io.sentry")
    className("BuildConfig")

    buildConfigField(
        "String",
        "SentryCocoaVersion",
        provider { "\"${project.property("sentryCocoaVersion")}\"" }
    )
}

detekt { config.setFrom(rootProject.files("../config/detekt/detekt.yml")) }

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
}
