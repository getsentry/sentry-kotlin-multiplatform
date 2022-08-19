import org.gradle.api.Project
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.file.CopySpec
import java.io.File

private object Consts {
    val taskRegex = Regex("(.*)DistZip")
}

val sep = File.separator

// configure distZip tasks for multiplatform
fun DistributionContainer.configureForMultiplatform(project: Project) {
    val version = project.properties["versionName"].toString()

    this.maybeCreate("android").contents {
        from("build${sep}publications${sep}androidRelease")
        from("build${sep}outputs${sep}aar${sep}sentry-kotlin-multiplatform-release.aar") {
            rename {
                it.replace("-release", "-android-release")
            }
        }
        from("build${sep}libs") {
            include("*android*")
            withJavadoc("android")
        }
    }
    this.getByName("main").contents {
        from("build${sep}publications${sep}kotlinMultiplatform")
        from("build${sep}kotlinToolingMetadata")
        from("build${sep}libs") {
            include("sentry-kotlin-multiplatform-?.?.*")
            include("sentry-kotlin-multiplatform-kotlin*")
            include("sentry-kotlin-multiplatform-metadata*")
            rename {
                it.replace("-kotlin", "").replace("-metadata", "")
            }
        }
    }
    this.maybeCreate("jvm").contents {
        from("build${sep}publications${sep}jvm")
        from("build${sep}libs$sep") {
            include("*jvm*")
            withJavadoc("jvm")
        }
    }
    this.maybeCreate("iosArm64").contents {
        from("build${sep}publications${sep}iosArm64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-iosarm64*")
            withJavadoc("iosarm64")
        }
        fromKlib("iosArm64", version)
    }
    this.maybeCreate("iosX64").contents {
        from("build${sep}publications${sep}iosX64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-iosx64*")
            withJavadoc("iosx64")
        }
        fromKlib("iosX64", version)
    }
    this.maybeCreate("iosSimulatorArm64").contents {
        from("build${sep}publications${sep}iosSimulatorArm64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-iossimulatorarm64*")
            withJavadoc("iossimulatorarm64")
        }
        fromKlib("iosSimulatorArm64", version)
    }
    this.maybeCreate("macosArm64").contents {
        from("build${sep}publications${sep}macosArm64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-macosarm64*")
            withJavadoc("macosarm64")
        }
        fromKlib("macosArm64", version)
    }
    this.maybeCreate("macosX64").contents {
        from("build${sep}publications${sep}macosX64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-macosx64*")
            withJavadoc("macosx64")
        }
        fromKlib("macosX64", version)
    }
    this.maybeCreate("watchosX64").contents {
        from("build${sep}publications${sep}watchosX64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-watchosx64*")
            withJavadoc("watchosx64")
        }
        fromKlib("watchosX64", version)
    }
    this.maybeCreate("watchosArm32").contents {
        from("build${sep}publications${sep}watchosArm32")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-watchosarm32*")
            withJavadoc("watchosarm32")
        }
        fromKlib("watchosArm32", version)
    }
    this.maybeCreate("watchosArm64").contents {
        from("build${sep}publications${sep}watchosArm64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-watchosarm64*")
            withJavadoc("watchosarm64")
        }
        fromKlib("watchosArm64", version)
    }
    this.maybeCreate("watchosSimulatorArm64").contents {
        from("build${sep}publications${sep}watchosSimulatorArm64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-watchossimulatorarm64*")
            withJavadoc("watchossimulatorarm64")
        }
        fromKlib("watchosSimulatorArm64", version)
    }
    this.maybeCreate("tvosArm64").contents {
        from("build${sep}publications${sep}tvosArm64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-tvosarm64*")
            withJavadoc("tvosarm64")
        }
        fromKlib("tvosArm64", version)
    }
    this.maybeCreate("tvosX64").contents {
        from("build${sep}publications${sep}tvosX64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-tvosx64*")
            withJavadoc("tvosx64")
        }
        fromKlib("tvosX64", version)
    }
    this.maybeCreate("tvosSimulatorArm64").contents {
        from("build${sep}publications${sep}tvosSimulatorArm64")
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-tvossimulatorarm64*")
            withJavadoc("tvossimulatorarm64")
        }
        fromKlib("tvosSimulatorArm64", version)
    }

    // make other distZip tasks run together with the main distZip
    val platformDists = project.tasks.filter { task ->
        task.name.matches(Consts.taskRegex)
    }.toTypedArray()
    project.tasks.getByName("distZip").finalizedBy(*platformDists)
}

private fun CopySpec.fromKlib(target: String, version: String) {
    val pos = "sentry-kotlin-multiplatform".length
    from("build${sep}libs${sep}${target}${sep}main") {
        rename {
            it.replaceRange(pos, pos, "-${target.toLowerCase()}-$version")
        }
    }
    from("build${sep}classes${sep}kotlin${sep}${target}${sep}main${sep}klib") {
        rename {
            "sentry-kotlin-multiplatform-${target.toLowerCase()}-$version.klib"
        }
    }
}

private fun CopySpec.withJavadoc(renameTo: String) {
    include("*javadoc*")
    rename {
        if (it.contains("javadoc")) {
            val pos = "sentry-kotlin-multiplatform".length
            it.replaceRange(pos, pos, "-$renameTo")
        } else {
            it
        }
    }
}
