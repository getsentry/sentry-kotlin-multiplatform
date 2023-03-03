package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.BuildKonfig

expect class SdkVersion(
    name: String = BuildKonfig.SENTRY_KOTLIN_MULTIPLATFORM_SDK_NAME,
    version: String = BuildKonfig.VERSION_NAME
) {
    val name: String
    val version: String
    val packages: MutableList<Package>
}

fun SdkVersion.addPackage(name: String, version: String) {
    this.packages.add(Package(name, version))
}

data class Package(
    val name: String,
    val version: String
)
