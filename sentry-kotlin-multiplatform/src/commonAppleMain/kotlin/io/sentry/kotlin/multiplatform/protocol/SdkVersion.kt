package io.sentry.kotlin.multiplatform.protocol

actual data class SdkVersion actual constructor(
    actual val name: String,
    actual val version: String,
    actual val packages: MutableList<Package>
) {
    fun toCocoaSdkVersion(): Map<Any?, *> {
        return mapOf(
            "name" to this.name,
            "version" to this.version,
            "packages" to this.packages.map { pkg ->
                mapOf(
                    "name" to pkg.name,
                    "version" to pkg.version
                )
            }
        )
    }
}
