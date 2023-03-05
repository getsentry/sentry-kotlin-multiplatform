package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.BuildKonfig

/** The SDK Interface describes the Sentry SDK and its configuration used to capture and transmit an event. */
expect class SdkVersion(
    name: String = BuildKonfig.SENTRY_KOTLIN_MULTIPLATFORM_SDK_NAME,
    version: String = BuildKonfig.VERSION_NAME,
    packages: MutableList<Package> = mutableListOf()
) {
    /** The name of the SDK. */
    val name: String

    /** The version of the SDK. */
    val version: String

    /** A list of packages used by the SDK. */
    val packages: MutableList<Package>
}

data class Package(
    val name: String,
    val version: String
)
