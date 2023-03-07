package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.protocol.SdkVersion
import io.sentry.protocol.SdkVersion as JvmSdkVersion

fun SdkVersion.toJvmSdkVersion(): JvmSdkVersion {
    val sdk = JvmSdkVersion(this.name, this.version)
    packages?.forEach { pkg ->
        sdk.addPackage(pkg.name, pkg.version)
    }
    return sdk
}
