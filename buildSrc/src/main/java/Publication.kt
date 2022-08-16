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
        from("build${sep}libs$sep") {
            include("*jvm*")
        }
    }
    this.maybeCreate("iosArm64").contents {
        from("build${sep}publications${sep}iosArm64")
        from("build${sep}libs${sep}iosArm64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-iosarm64*")
        }
    }
    this.maybeCreate("iosX64").contents {
        from("build${sep}publications${sep}iosX64")
        from("build${sep}libs${sep}iosX64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-iosx64*")
        }
    }
    this.maybeCreate("iosSimulatorArm64").contents {
        from("build${sep}publications${sep}iosSimulatorArm64")
        from("build${sep}libs${sep}iosSimulatorArm64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-iossimulatorarm64*")
        }
    }
    this.maybeCreate("macosArm64").contents {
        from("build${sep}publications${sep}macosArm64")
        from("build${sep}libs${sep}macosArm64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-macosarm64*")
        }
    }
    this.maybeCreate("macosX64").contents {
        from("build${sep}publications${sep}macosX64")
        from("build${sep}libs${sep}macosX64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-macosx64*")
        }
    }
    this.maybeCreate("watchosX64").contents {
        from("build${sep}publications${sep}watchosX64")
        from("build${sep}libs${sep}watchosX64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-watchosx64*")
        }
    }
    this.maybeCreate("watchosArm32").contents {
        from("build${sep}publications${sep}watchosArm32")
        from("build${sep}libs${sep}watchosArm32")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-watchosarm32*")
        }
    }
    this.maybeCreate("watchosArm64").contents {
        from("build${sep}publications${sep}watchosArm64")
        from("build${sep}libs${sep}watchosArm64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-watchosarm64*")
        }
    }
    this.maybeCreate("watchosSimulatorArm64").contents {
        from("build${sep}publications${sep}watchosSimulatorArm64")
        from("build${sep}libs${sep}watchosArm64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-watchossimulatorarm64*")
        }
    }
    this.maybeCreate("tvosArm64").contents {
        from("build${sep}publications${sep}tvosArm64")
        from("build${sep}libs${sep}tvosArm64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-tvosarm64*")
        }
    }
    this.maybeCreate("tvosX64").contents {
        from("build${sep}publications${sep}tvosX64")
        from("build${sep}libs${sep}tvosX64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-tvosx64*")
        }
    }
    this.maybeCreate("tvosSimulatorArm64").contents {
        from("build${sep}publications${sep}tvosSimulatorArm64")
        from("build${sep}libs${sep}tvosArm64")
        from("build${sep}libs${sep}") {
            include("sentry-kotlin-multiplatform-tvossimulatorarm64*")
        }
    }

    // make other distZip tasks run together with the main distZip
    val platformDists = project.tasks.filter { task ->
        task.name.matches(Consts.taskRegex)
    }.toTypedArray()
    project.tasks.getByName("distZip").finalizedBy(*platformDists)
}
