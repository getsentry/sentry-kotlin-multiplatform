package io.sentry.kotlin.multiplatform.protocol

import io.sentry.sentry_kotlin_multiplatform.BuildConfig

expect class SdkVersion(
    name: String = BuildConfig.SENTRY_KOTLIN_MULTIPLATFORM_SDK_NAME,
    version: String = BuildConfig.VERSION_NAME
) {
    val name: String
    val version: String
    val packages: MutableList<Package>
}

fun SdkVersion.setPackages(packages: List<Package>) {
    this.packages.clear()
    this.packages.addAll(packages)
}

fun SdkVersion.addPackage(name: String, version: String) {
    this.packages.add(Package(name, version))
}

data class Package(
    val name: String,
    val version: String
)
