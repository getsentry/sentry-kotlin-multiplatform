import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.attributes.java.TargetJvmVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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
    // Consumers supply spm4Kmp when they apply it; keep it optional at runtime.
    compileOnly(libs.spmForKmp)

    testImplementation(kotlin("gradle-plugin"))
    testImplementation(libs.spmForKmp)
    testImplementation(libs.junit)
    testImplementation(libs.junit.params)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// Resolve spm4Kmp's Java 17 artifact for compilation and tests, but emit Java 11 bytecode
// for consumers that do not use spm4Kmp.
listOf("compileClasspath", "testCompileClasspath", "testRuntimeClasspath").forEach { configurationName ->
    configurations.named(configurationName).configure {
        attributes {
            attribute(
                TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                JavaVersion.VERSION_17.majorVersion.toInt()
            )
        }
    }
}

tasks.withType<KotlinCompile>().configureEach { compilerOptions { jvmTarget.set(JvmTarget.JVM_11) } }

gradlePlugin {
    plugins {
        register("sentryPlugin") {
            id = property("id").toString()
            implementationClass = property("implementationClass").toString()
        }
    }
}

tasks.named("distZip") {
    dependsOn("publishToMavenLocal")
    onlyIf {
        inputs.sourceFiles.isEmpty.not().also {
            require(it) { "No distribution to zip." }
        }
    }
}

val sep: String = File.separator

distributions {
    main {
        contents {
            from("build${sep}libs")
            from("build${sep}publications${sep}pluginMaven")
        }
    }
    create("sentryPluginMarker") {
        contents {
            from("build${sep}publications${sep}sentryPluginPluginMarkerMaven")
        }
    }
}

tasks.named("sentryPluginMarkerDistTar") {
    mustRunAfter("generatePomFileForSentryPluginPluginMarkerMavenPublication")
}

tasks.named("sentryPluginMarkerDistZip") {
    mustRunAfter("generatePomFileForSentryPluginPluginMarkerMavenPublication")
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
    buildConfigField(
        "String",
        "SentryKmpVersion",
        provider { "\"${project.property("versionName")}\"" }
    )
}

detekt { config.setFrom(rootProject.files("../config/detekt/detekt.yml")) }

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
}
