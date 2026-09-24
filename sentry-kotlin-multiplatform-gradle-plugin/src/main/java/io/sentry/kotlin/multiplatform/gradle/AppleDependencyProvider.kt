package io.sentry.kotlin.multiplatform.gradle

/** Integration used to install the native Sentry Cocoa dependency. */
enum class AppleDependencyProvider {
    /** Prefer integrations in use: official SwiftPM, then spm4Kmp. */
    AUTO,

    /** Use Kotlin 2.4 or newer's official SwiftPM import. */
    SWIFT_PM,

    /** Use the applied spm4Kmp plugin. */
    SPM4KMP,

    /** Leave Apple dependency installation to the application. Linking remains enabled. */
    NONE,
}
