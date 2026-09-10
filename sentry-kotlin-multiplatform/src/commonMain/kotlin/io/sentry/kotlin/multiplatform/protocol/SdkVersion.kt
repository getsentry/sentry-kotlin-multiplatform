package io.sentry.kotlin.multiplatform.protocol

/** The SDK Interface describes the Sentry SDK and its configuration used to capture and transmit an event. */
public data class SdkVersion(
    /** The name of the SDK. */
    val name: String,

    /** The version of the SDK. */
    val version: String
) {
    /** Packages used by the SDK. */
    var packages: List<Package>? = mutableListOf()
        private set

    public fun addPackage(name: String, version: String) {
        val mutableList = packages?.toMutableList()
        mutableList?.add(Package(name, version))
        packages = mutableList
    }
}

/** Describes which native SDK packages are used. */
public data class Package(
    /** The name of the package. */
    val name: String,
    /** The version of the package. */
    val version: String
)
