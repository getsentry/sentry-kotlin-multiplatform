package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptionsCallback

public actual typealias SentryPlatformOptions = io.sentry.SentryOptions

internal actual fun SentryPlatformOptions.prepareForInit() {
    sdkVersion?.name = BuildKonfig.SENTRY_KMP_JAVA_SDK_NAME
    sdkVersion?.version = BuildKonfig.VERSION_NAME
    if (sdkVersion?.packageSet?.none { it.name == BuildKonfig.SENTRY_JAVA_PACKAGE_NAME } == true) {
        sdkVersion?.addPackage(BuildKonfig.SENTRY_JAVA_PACKAGE_NAME, BuildKonfig.SENTRY_JAVA_VERSION)
    }
}

internal actual fun SentryOptions.toPlatformOptionsConfiguration(): PlatformOptionsConfiguration =
    toJvmSentryOptionsCallback()
