package io.sentry.kotlin.multiplatform.protocol

/** The SDK Interface describes the Sentry SDK and its configuration used to capture and transmit an event. */
data class SdkVersion(
    /** The name of the SDK. */
    val name: String,

    /** The version of the SDK. */
    val version: String
) {
    /** Packages used by the SDK. */
    var packages: List<Package>? = mutableListOf()
        private set

    fun addPackage(name: String, version: String) {
        val mutableList = packages?.toMutableList()
        mutableList?.add(Package(name, version))
        packages = mutableList
    }
}

data class Package(
    val name: String,
    val version: String
)
