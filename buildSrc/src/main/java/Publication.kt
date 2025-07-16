import org.gradle.api.Project
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.register
import java.io.File

/**
 * Helpers to assemble our platform‑specific distribution zips.
 *
 * For Kotlin/Native cinterop outputs we want **real** `.klib` archives whose file names
 * encode the native target and version (e.g. `sentry-kotlin-multiplatform-cinterop-Sentry-iosarm64-6.36.0.klib`).
 *
 * On recent Kotlin versions the compiler already emits such files; on older ones the
 * compiler leaves an *unpacked* klib layout on disk (`default/`, `linkdata/`, …).
 * The `fromKlib(…)` helper below registers a Zip task per (target, cinterop) that:
 *   1. Detects whether the compiler produced a ready‑made `.klib` **file** *or* an
 *      unpacked **directory**.
 *   2. Zips the directory (or re‑zips the file) so we can rename it deterministically.
 *   3. Declares an explicit `dependsOn` on the corresponding `cinterop*` task so Gradle
 *      knows the inputs are produced by another task (avoiding configuration errors).
 *
 * The produced Zip tasks are wired into the distribution `CopySpec` via `from(zipTask)`;
 * passing a task provider automatically adds the dependency edge to the distZip task.
 */

val sep: String = File.separator

// ---------------------------------------------------------------------------
// Top‑level entry point called from the module's `publishing.gradle.kts`.
// ---------------------------------------------------------------------------

fun DistributionContainer.configureForMultiplatform(project: Project) {
    val version = project.properties["versionName"].toString()

    // --------------------------------------------------
    // MAIN (platform‑agnostic) DISTRIBUTION
    // --------------------------------------------------
    this.getByName("main").contents {
        from("build${sep}publications${sep}kotlinMultiplatform") {
            renameModule(project.name, version = version)
        }
        // Compatibility shim: Kotlin ≥1.9 stopped writing *-all.jar.
        from("build${sep}libs") {
            include("${project.name}-metadata-$version*")
            rename { it.replace("metadata-$version", "$version-all") }
        }
        from("build${sep}kotlinToolingMetadata") {
            rename {
                it.replace(
                    "kotlin-tooling-metadata.json",
                    "${project.name}-$version-kotlin-tooling-metadata.json"
                )
            }
        }
        from("build${sep}libs") {
            include("${project.name}-kotlin*")
            include("${project.name}-metadata*")
            withJavadoc(project.name)
            rename {
                it.replace("multiplatform-kotlin", "multiplatform")
                    .replace("-metadata", "")
            }
        }
    }

    // --------------------------------------------------
    // JVM & ANDROID ‑ no native klibs needed
    // --------------------------------------------------
    this.maybeCreate("android").contents {
        from("build${sep}publications${sep}androidRelease") {
            renameModule(project.name, "android", version)
        }
        from("build${sep}outputs${sep}aar${sep}${project.name}-release.aar") {
            rename { it.replace("-release", "-android-release") }
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

    // --------------------------------------------------
    // All targets that need klib packaging
    // --------------------------------------------------
    fun CopySpec.native(targetGradleName: String, targetFileSuffix: String) {
        from("build${sep}publications${sep}$targetGradleName") {
            renameModule(project.name, targetFileSuffix, version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-$targetFileSuffix*")
            withJavadoc(project.name, targetFileSuffix)
        }
        fromKlib(project, targetGradleName, version)
    }

    this.maybeCreate("iosarm64").contents { native("iosArm64", "iosarm64") }
    this.maybeCreate("iosx64").contents { native("iosX64", "iosx64") }
    this.maybeCreate("iossimulatorarm64")
        .contents { native("iosSimulatorArm64", "iossimulatorarm64") }
    this.maybeCreate("macosarm64").contents { native("macosArm64", "macosarm64") }
    this.maybeCreate("macosx64").contents { native("macosX64", "macosx64") }
    this.maybeCreate("watchosx64").contents { native("watchosX64", "watchosx64") }
    this.maybeCreate("watchosarm32").contents { native("watchosArm32", "watchosarm32") }
    this.maybeCreate("watchosarm64").contents { native("watchosArm64", "watchosarm64") }
    this.maybeCreate("watchossimulatorarm64")
        .contents { native("watchosSimulatorArm64", "watchossimulatorarm64") }
    this.maybeCreate("tvosarm64").contents { native("tvosArm64", "tvosarm64") }
    this.maybeCreate("tvosx64").contents { native("tvosX64", "tvosx64") }
    this.maybeCreate("tvossimulatorarm64")
        .contents { native("tvosSimulatorArm64", "tvossimulatorarm64") }

    // JS usually has no klibs but run defensively.
    this.maybeCreate("js").contents {
        from("build${sep}publications${sep}js") {
            renameModule(project.name, "js", version)
        }
        from("build${sep}libs$sep") {
            include("${project.name}-js-*")
            withJavadoc(project.name, "js")
        }
        fromKlib(project, "js", version)
    }
}

// ---------------------------------------------------------------------------
// Helper to package klibs per target.
// ---------------------------------------------------------------------------

private fun CopySpec.fromKlib(
    project: Project,
    target: String,
    version: String
) {
    val libs = listOf("Sentry", "Sentry.Internal")
    val cinteropRelPath = "classes${sep}kotlin${sep}$target${sep}main${sep}cinterop"
    val projectName = project.name
    val targetSlug = target.lowercase()

    libs.forEach { lib ->
        val baseName = "$projectName-cinterop-$lib"

        // Desired shipped artifact name:
        //   <project>-<targetSlug>-<version>-cinterop-<lib>.klib
        // e.g. sentry-kotlin-multiplatform-iossimulatorarm64-0.110.0-cinterop-Sentry.klib
        val finalFileName = "$projectName-$targetSlug-$version-cinterop-$lib.klib"

        // Gradle's cinterop task keeps raw lib (including '.'), so we do the same
        // when locating upstream tasks; we only sanitize for our packaging task name.
        val tgtTaskPartCap = target.replaceFirstChar { it.uppercase() } // iosSimulatorArm64 -> IosSimulatorArm64
        val cinteropTaskName = "cinterop${lib}$tgtTaskPartCap" // dot in lib preserved

        val libTaskSafe = lib.replace(".", "") // for our task name
        val pkgTaskName = "package${tgtTaskPartCap}${libTaskSafe}CinteropKlib"

        val sourceKlibProvider = project.layout.buildDirectory.file(
            "$cinteropRelPath${sep}$baseName.klib"
        )
        val sourceDirProvider = project.layout.buildDirectory.dir(
            "$cinteropRelPath${sep}$baseName"
        )

        val zipTask = project.tasks.register(pkgTaskName, Zip::class.java) {
            group = "packaging"
            description = "Package $baseName for $target into a .klib"

            archiveFileName.set(finalFileName)
            destinationDirectory.set(project.layout.buildDirectory.dir("tmp${sep}klibs${sep}$targetSlug"))

            // Prefer compiler-produced klib; else zip unpacked dir.
            from(
                project.provider {
                    val klibFile = sourceKlibProvider.get().asFile
                    if (klibFile.isFile) project.zipTree(klibFile) else sourceDirProvider.get().asFile
                }
            )

            // Skip if neither form exists (no such cinterop for this target).
            onlyIf {
                sourceKlibProvider.get().asFile.exists() || sourceDirProvider.get().asFile.exists()
            }
        }

        project.afterEvaluate {
            val upstream = tasks.findByName(cinteropTaskName)
            if (upstream != null) {
                zipTask.configure { dependsOn(upstream) }
            }
        }

        from(zipTask)
    }
}

private fun CopySpec.renameModule(projectName: String, renameTo: String = "", version: String) {
    val suffix = if (renameTo.isNotEmpty()) "-$renameTo" else ""
    rename { it.replace("module.json", "$projectName$suffix-$version.module") }
}

private fun CopySpec.withJavadoc(projectName: String, renameTo: String = "") {
    include("*javadoc*")
    rename { fileName ->
        if ("javadoc" in fileName) {
            buildString {
                append(fileName.substring(0, projectName.length))
                if (renameTo.isNotEmpty()) append('-').append(renameTo)
                append(fileName.substring(projectName.length))
            }
        } else {
            fileName
        }
    }
}
