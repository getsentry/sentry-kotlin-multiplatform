package io.sentry.kotlin.multiplatform.gradle

/** Integration used to install the native Sentry Cocoa dependency. */
enum class AppleDependencyProvider {
    /**
     * Use spm4Kmp when applied, nothing when the Kotlin CocoaPods plugin is applied, and otherwise
     * official SwiftPM on Kotlin 2.4 or newer.
     */
    AUTO,

    /** Use Kotlin 2.4 or newer's official SwiftPM import. */
    SWIFT_PM,

    /** Use the applied spm4Kmp plugin. */
    SPM4KMP,

    /** Leave Apple dependency installation to the application. Manual linking remains enabled. */
    NONE,
}
