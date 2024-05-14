package io.sentry.kotlin.multiplatform

public actual typealias SentryPlatformOptions = io.sentry.SentryOptions

internal actual fun SentryPlatformOptions.prepareForInit() {
    sdkVersion?.name = BuildKonfig.SENTRY_KMP_JAVA_SDK_NAME
    sdkVersion?.version = BuildKonfig.VERSION_NAME
}
