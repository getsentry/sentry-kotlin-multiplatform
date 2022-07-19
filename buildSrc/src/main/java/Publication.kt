import org.gradle.api.Project
import org.gradle.api.distribution.DistributionContainer
import java.io.File

private object Consts {
    val taskRegex = Regex("(.*)DistZip")
}

// configure distZip tasks for multiplatform
fun DistributionContainer.configureForMultiplatform(project: Project) {
    val sep = File.separator

    this.maybeCreate("android").contents {
        from("build${sep}publications${sep}androidRelease")
        from("build${sep}outputs${sep}aar${sep}sentry-kotlin-multiplatform-release.aar") {
            rename {
                it.replace("-release", "-android-release")
            }
        }
        from("build${sep}libs") {
            include("*android*")
        }
    }
    this.getByName("main").contents {
        from("build${sep}publications${sep}kotlinMultiplatform")
        from("build${sep}kotlinToolingMetadata")
        from("build${sep}libs") {
            include("*kotlin*")
            include("*metadata*")
        }
    }
    this.maybeCreate("jvm").contents {
        from("build${sep}publications${sep}jvm")
        from("build${sep}libs${sep}") {
            include("*jvm*")
        }
    }
    this.maybeCreate("iosArm64").contents {
        from("build${sep}publications${sep}iosArm64")
        from("build${sep}libs${sep}iosArm64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-iosarm64*")
        }
    }
    this.maybeCreate("iosX64").contents {
        from("build${sep}publications${sep}iosX64")
        from("build${sep}libs${sep}iosX64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-iosx64*")
        }
    }

    // make other distZip tasks run together with the main distZip
    val platformDists = project.tasks.filter { task ->
        task.name.matches(Consts.taskRegex)
    }.toTypedArray()
    project.tasks.getByName("distZip").finalizedBy(*platformDists)
}
