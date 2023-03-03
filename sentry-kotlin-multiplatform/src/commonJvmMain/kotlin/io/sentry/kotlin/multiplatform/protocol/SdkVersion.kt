package io.sentry.kotlin.multiplatform.protocol

import io.sentry.protocol.SdkVersion as JvmSdkVersion

actual class SdkVersion actual constructor(
    actual val name: String,
    actual val version: String
) {
    actual val packages: MutableList<Package> = mutableListOf()

    fun toJvmSdkVersion(): JvmSdkVersion {
        val sdk = JvmSdkVersion(name, version)
        packages.forEach { pkg ->
            sdk.addPackage(pkg.name, pkg.version)
        }
        return sdk
    }
}
