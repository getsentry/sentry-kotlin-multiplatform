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
            renameModule(project.name, version = version)
        }
        from("build${sep}kotlinToolingMetadata") {
            rename {
                it.replace("kotlin-tooling-metadata.json", "${project.name}-$version-kotlin-tooling-metadata.json")
            }
        }
        from("build${sep}libs") {
            include("${project.name}-?.?.*")
            include("${project.name}-kotlin*")
            include("${project.name}-metadata*")
            rename {
                it.replace("multiplatform-kotlin", "multiplatform").replace("-metadata", "")
            }
        }
    }
    this.maybeCreate("android").contents {
        from("build${sep}publications${sep}androidRelease") {
            renameModule(project.name, "android", version)
        }
        from("build${sep}outputs${sep}aar${sep}${project.name}-release.aar") {
            rename {
                it.replace("-release", "-android-release")
            }
        }
        from("build${sep}libs") {
            include("*android*")
            withJavadoc(project.name, "android")
        }
    }
    this.maybeCreate("jvm").contents {
        from("build${sep}publications${sep}jvm") {
            renameModule(project.name, "jvm", version)
        }
        from("build${sep}libs$sep") {
            include("*jvm*")
            withJavadoc(project.name, "jvm")
        }
    }
    this.maybeCreate("iosarm64").contents {
        from("build${sep}publications${sep}iosArm64") {
            renameModule(project.name, "iosarm64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-iosarm64*")
            withJavadoc(project.name, "iosarm64")
        }
        fromKlib(project.name, "iosArm64", version)
    }
    this.maybeCreate("iosx64").contents {
        from("build${sep}publications${sep}iosX64") {
            renameModule(project.name, "iosx64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-iosx64*")
            withJavadoc(project.name, "iosx64")
        }
        fromKlib(project.name, "iosX64", version)
    }
    this.maybeCreate("iossimulatorarm64").contents {
        from("build${sep}publications${sep}iosSimulatorArm64") {
            renameModule(project.name, "iossimulatorarm64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-iossimulatorarm64*")
            withJavadoc(project.name, "iossimulatorarm64")
        }
        fromKlib(project.name, "iosSimulatorArm64", version)
    }
    this.maybeCreate("macosarm64").contents {
        from("build${sep}publications${sep}macosArm64") {
            renameModule(project.name, "macosarm64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-macosarm64*")
            withJavadoc(project.name, "macosarm64")
        }
        fromKlib(project.name, "macosArm64", version)
    }
    this.maybeCreate("macosx64").contents {
        from("build${sep}publications${sep}macosX64") {
            renameModule(project.name, "macosx64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-macosx64*")
            withJavadoc(project.name, "macosx64")
        }
        fromKlib(project.name, "macosX64", version)
    }
    this.maybeCreate("watchosx64").contents {
        from("build${sep}publications${sep}watchosX64") {
            renameModule(project.name, "watchosx64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-watchosx64*")
            withJavadoc(project.name, "watchosx64")
        }
        fromKlib(project.name, "watchosX64", version)
    }
    this.maybeCreate("watchosarm32").contents {
        from("build${sep}publications${sep}watchosArm32") {
            renameModule(project.name, "watchosarm32", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-watchosarm32*")
            withJavadoc(project.name, "watchosarm32")
        }
        fromKlib(project.name, "watchosArm32", version)
    }
    this.maybeCreate("watchosarm64").contents {
        from("build${sep}publications${sep}watchosArm64") {
            renameModule(project.name, "watchosarm64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-watchosarm64*")
            withJavadoc(project.name, "watchosarm64")
        }
        fromKlib(project.name, "watchosArm64", version)
    }
    this.maybeCreate("watchossimulatorarm64").contents {
        from("build${sep}publications${sep}watchosSimulatorArm64") {
            renameModule(project.name, "watchossimulatorarm64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-watchossimulatorarm64*")
            withJavadoc(project.name, "watchossimulatorarm64")
        }
        fromKlib(project.name, "watchosSimulatorArm64", version)
    }
    this.maybeCreate("tvosarm64").contents {
        from("build${sep}publications${sep}tvosArm64") {
            renameModule(project.name, "tvosarm64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-tvosarm64*")
            withJavadoc(project.name, "tvosarm64")
        }
        fromKlib(project.name, "tvosArm64", version)
    }
    this.maybeCreate("tvosx64").contents {
        from("build${sep}publications${sep}tvosX64") {
            renameModule(project.name, "tvosx64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-tvosx64*")
            withJavadoc(project.name, "tvosx64")
        }
        fromKlib(project.name, "tvosX64", version)
    }
    this.maybeCreate("tvossimulatorarm64").contents {
        from("build${sep}publications${sep}tvosSimulatorArm64") {
            renameModule(project.name, "tvossimulatorarm64", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-tvossimulatorarm64*")
            withJavadoc(project.name, "tvossimulatorarm64")
        }
        fromKlib(project.name, "tvosSimulatorArm64", version)
    }

    // make other distZip tasks run together with the main distZip
    val platformDists = project.tasks.filter { task ->
        task.name.matches(Consts.taskRegex)
    }.toTypedArray()
    project.tasks.getByName("distZip").finalizedBy(*platformDists)
}

private fun CopySpec.fromKlib(projectName: String, target: String, version: String) {
    val pos = projectName.length
    from("build${sep}libs${sep}${target}${sep}main") {
        rename {
            it.replaceRange(pos, pos, "-${target.toLowerCase()}-$version")
        }
    }
    from("build${sep}classes${sep}kotlin${sep}${target}${sep}main${sep}klib") {
        rename {
            "$projectName-${target.toLowerCase()}-$version.klib"
        }
    }
}

private fun CopySpec.renameModule(projectName: String, renameTo: String = "", version: String) {
    var target = ""
    if (!renameTo.isEmpty()) {
        target = "-$renameTo"
    }
    rename {
        it.replace("module.json", "$projectName$target-$version.module")
    }
}

private fun CopySpec.withJavadoc(projectName: String, renameTo: String) {
    include("*javadoc*")
    rename {
        if (it.contains("javadoc")) {
            val pos = projectName.length
            it.replaceRange(pos, pos, "-$renameTo")
        } else {
            it
        }
    }
}
