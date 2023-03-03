package io.sentry.kotlin.multiplatform.protocol

actual class SdkVersion actual constructor(
    actual val name: String,
    actual val version: String
) {
    actual val packages: MutableList<Package> = mutableListOf()

    fun toCocoaSdkVersion(): Map<Any?, *> {
        return mapOf(
            "name" to name,
            "version" to version,
            "packages" to packages.map { pkg ->
                mapOf(
                    "name" to pkg.name,
                    "version" to pkg.version
                )
            }
        )
    }
}
