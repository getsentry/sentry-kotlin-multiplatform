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

    this.getByName("main").contents {
        from("build${sep}publications${sep}kotlinMultiplatform") {
            renameModule(version = version)
        }
        from("build${sep}kotlinToolingMetadata")
        from("build${sep}libs") {
            include("sentry-kotlin-multiplatform-?.?.*")
            include("sentry-kotlin-multiplatform-kotlin*")
            include("sentry-kotlin-multiplatform-metadata*")
            rename {
                it.replace("multiplatform-kotlin", "multiplatform").replace("-metadata", "")
            }
        }
    }
    this.maybeCreate("android").contents {
        from("build${sep}publications${sep}androidRelease") {
            renameModule("android", version)
        }
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
    this.maybeCreate("jvm").contents {
        from("build${sep}publications${sep}jvm") {
            renameModule("jvm", version)
        }
        from("build${sep}libs$sep") {
            include("*jvm*")
            withJavadoc("jvm")
        }
    }
    this.maybeCreate("iosarm64").contents {
        from("build${sep}publications${sep}iosArm64") {
            renameModule("iosarm64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-iosarm64*")
            withJavadoc("iosarm64")
        }
        fromKlib("iosArm64", version)
    }
    this.maybeCreate("iosx64").contents {
        from("build${sep}publications${sep}iosX64") {
            renameModule("iosx64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-iosx64*")
            withJavadoc("iosx64")
        }
        fromKlib("iosX64", version)
    }
    this.maybeCreate("iossimulatorarm64").contents {
        from("build${sep}publications${sep}iosSimulatorArm64") {
            renameModule("iossimulatorarm64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-iossimulatorarm64*")
            withJavadoc("iossimulatorarm64")
        }
        fromKlib("iosSimulatorArm64", version)
    }
    this.maybeCreate("macosarm64").contents {
        from("build${sep}publications${sep}macosArm64") {
            renameModule("macosarm64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-macosarm64*")
            withJavadoc("macosarm64")
        }
        fromKlib("macosArm64", version)
    }
    this.maybeCreate("macosx64").contents {
        from("build${sep}publications${sep}macosX64") {
            renameModule("macosx64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-macosx64*")
            withJavadoc("macosx64")
        }
        fromKlib("macosX64", version)
    }
    this.maybeCreate("watchosx64").contents {
        from("build${sep}publications${sep}watchosX64") {
            renameModule("watchosx64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-watchosx64*")
            withJavadoc("watchosx64")
        }
        fromKlib("watchosX64", version)
    }
    this.maybeCreate("watchosarm32").contents {
        from("build${sep}publications${sep}watchosArm32") {
            renameModule("watchosarm32", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-watchosarm32*")
            withJavadoc("watchosarm32")
        }
        fromKlib("watchosArm32", version)
    }
    this.maybeCreate("watchosarm64").contents {
        from("build${sep}publications${sep}watchosArm64") {
            renameModule("watchosarm64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-watchosarm64*")
            withJavadoc("watchosarm64")
        }
        fromKlib("watchosArm64", version)
    }
    this.maybeCreate("watchossimulatorarm64").contents {
        from("build${sep}publications${sep}watchosSimulatorArm64") {
            renameModule("watchossimulatorarm64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-watchossimulatorarm64*")
            withJavadoc("watchossimulatorarm64")
        }
        fromKlib("watchosSimulatorArm64", version)
    }
    this.maybeCreate("tvosarm64").contents {
        from("build${sep}publications${sep}tvosArm64") {
            renameModule("tvosarm64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-tvosarm64*")
            withJavadoc("tvosarm64")
        }
        fromKlib("tvosArm64", version)
    }
    this.maybeCreate("tvosx64").contents {
        from("build${sep}publications${sep}tvosX64") {
            renameModule("tvosx64", version)
        }
        from("build${sep}libs$sep") {
            include("sentry-kotlin-multiplatform-tvosx64*")
            withJavadoc("tvosx64")
        }
        fromKlib("tvosX64", version)
    }
    this.maybeCreate("tvossimulatorarm64").contents {
        from("build${sep}publications${sep}tvosSimulatorArm64") {
            renameModule("tvossimulatorarm64", version)
        }
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

private fun CopySpec.renameModule(renameTo: String = "", version: String) {
    var target = ""
    if (!renameTo.isEmpty()) {
        target = "-$renameTo"
    }
    rename {
        it.replace("module.json", "sentry-kotlin-multiplatform$target-$version.module")
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
