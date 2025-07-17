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
        "watchosx64" to "$projectName-watchosx64"
    )

    platforms.forEach { (distName, projectName) ->
        val distribution = if (distName == "main") getByName("main") else maybeCreate(distName)
        distribution.contents {
            val basePath = "${buildPublishDir}io${sep}sentry${sep}$projectName$sep$version"

            // Rename the POM since craft looks for pom-default.xml
            from("$basePath$sep$projectName-$version.pom") {
                rename { "pom-default.xml" }
            }

            from(basePath) {
                exclude("*.pom")
            }
        }
    }
}
