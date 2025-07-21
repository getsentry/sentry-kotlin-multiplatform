import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.distribution.DistributionContainer
import java.io.File

private val sep: String = File.separator

fun DistributionContainer.configureForMultiplatform(project: Project, buildPublishDir: String) {
    val version = project.property("versionName").toString()
    if (version.isEmpty()) {
        throw GradleException("DistZip: version name is empty")
    }
    val projectName = project.name
    val platforms = mapOf(
        "main" to projectName,
        "android" to "$projectName-android",
        "jvm" to "$projectName-jvm",
        "iosarm64" to "$projectName-iosarm64",
        "iossimulatorarm64" to "$projectName-iossimulatorarm64",
        "iosx64" to "$projectName-iosx64",
        "macosarm64" to "$projectName-macosarm64",
        "macosx64" to "$projectName-macosx64",
        "tvosarm64" to "$projectName-tvosarm64",
        "tvossimulatorarm64" to "$projectName-tvossimulatorarm64",
        "tvosx64" to "$projectName-tvosx64",
        "watchosarm32" to "$projectName-watchosarm32",
        "watchosarm64" to "$projectName-watchosarm64",
        "watchossimulatorarm64" to "$projectName-watchossimulatorarm64",
        "watchosx64" to "$projectName-watchosx64",
        "js" to "$projectName-js",
        "wasm-js" to "$projectName-wasm-js",
        "mingwx64" to "$projectName-mingwx64",
        "linuxarm64" to "$projectName-linuxarm64",
        "linuxx64" to "$projectName-linuxx64"
    )

    platforms.forEach { (distName, projectName) ->
        val distribution = maybeCreate(distName)
        distribution.contents {
            val basePath = "${buildPublishDir}io${sep}sentry${sep}$projectName$sep$version"

            // Rename the POM since craft looks for pom-default.xml
            from("$basePath$sep$projectName-$version.pom") {
                rename { "pom-default.xml" }
            }

            // Rename the main jar to ...-all.jar due to craft
            if (distribution.name == "main") {
                from("$basePath$sep$projectName-$version.jar") {
                    rename { "$projectName-$version-all.jar" }
                }
            }

            // Rename the android.aar to android-release.aar due to craft
            if (distribution.name == "android") {
                from("$basePath$sep$projectName-$version.aar") {
                    rename { "$projectName-release.aar" }
                }
            }

            from(basePath) {
                exclude("*.pom")
                exclude("$projectName-$version.aar")
            }
        }
    }
}
