package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.protocol.SdkVersion

fun SdkVersion.toCocoaSdkVersion(): Map<Any?, *> {
    return mapOf(
        "name" to this.name,
        "version" to this.version,
        "packages" to this.packages?.map { pkg ->
            mapOf(
                "name" to pkg.name,
                "version" to pkg.version
            )
        }
    )
}
