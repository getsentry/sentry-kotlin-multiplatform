package io.sentry.kotlin.multiplatform.gradle

import io.github.frankois944.spmForKmp.definition.PackageRootDefinitionExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.RelativePath
import org.gradle.api.tasks.Copy

private const val COPY_TASK_NAME = "copySentrySpmWatchosSimulatorFramework"
private const val SPM_TASK_PREFIX = "SwiftPackageConfigAppleSentryCocoa"

/**
 * spm4Kmp 1.9.5 uses aarch64 for the watchOS simulator triple, which SwiftPM does not match to
 * the arm64 binary slice. Link-only consumers still need that framework beside the bridge archive.
 * Called only for packages owned by Sentry auto-installation.
 */
internal fun Project.registerWatchosSimulatorFrameworkCopy() {
    if (COPY_TASK_NAME in tasks.names) {
        return
    }
    // Resolve lazily after all target blocks. spm4Kmp uses the global entry, or the first entry
    // in container order, for every target sharing this cinterop name (not the watchOS entry).
    val configuration =
        provider {
            val entries =
                extensions.getByName(SPM4KMP_SWIFT_PACKAGE_CONFIG_EXTENSION_NAME) as NamedDomainObjectContainer<*>
            val entry =
                entries.findByName(SENTRY_COCOA_CINTEROP_NAME)
                    ?: entries.first {
                        it is PackageRootDefinitionExtension && it.name.startsWith("${SENTRY_COCOA_CINTEROP_NAME}_")
                    }
            entry as PackageRootDefinitionExtension
        }
    val scratchDirectory =
        configuration.map { file(it.spmWorkingPath).resolve("spmKmpPlugin/$SENTRY_COCOA_CINTEROP_NAME/scratch") }
    val copyTask =
        tasks.register(COPY_TASK_NAME, Copy::class.java) { task ->
            task.dependsOn("${SPM_TASK_PREFIX}CompileSwiftPackageWatchosSimulatorArm64")
            task.from(scratchDirectory.map { it.resolve("artifacts/sentry-cocoa/Sentry/Sentry.xcframework") }) { spec ->
                spec.include("watchos-*-simulator/Sentry.framework/**")
                spec.eachFile { details ->
                    details.relativePath =
                        RelativePath.parse(true, details.relativePath.pathString.substringAfter('/'))
                }
                spec.includeEmptyDirs = false
            }
            task.into(
                scratchDirectory.zip(configuration) { scratch, entry ->
                    scratch.resolve("aarch64-apple-watchos-simulator/${if (entry.debug) "debug" else "release"}")
                },
            )
        }
    tasks.matching { it.name == "${SPM_TASK_PREFIX}GenerateCInteropDefinitionWatchosSimulatorArm64" }.configureEach {
        it.dependsOn(copyTask)
    }
}
