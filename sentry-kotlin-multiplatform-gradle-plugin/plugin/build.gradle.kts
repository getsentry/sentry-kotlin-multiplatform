import com.vanniktech.maven.publish.MavenPublishPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    alias(libs.plugins.vanniktech.publish)
    id("distribution")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(gradleApi())
    compileOnly(kotlin("gradle-plugin"))

    testImplementation(libs.junit)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
        }
    }
}

val publish = extensions.getByType(MavenPublishPluginExtension::class.java)
// signing is done when uploading files to MC
// via gpg:sign-and-deploy-file (release.kts)
publish.releaseSigningEnabled = false

tasks.named("distZip").configure {
    dependsOn("publishToMavenLocal")
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
