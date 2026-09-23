package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toIosOptionsConfiguration

public actual typealias SentryPlatformOptions = cocoapods.Sentry.SentryOptions

internal actual fun SentryOptions.toPlatformOptionsConfiguration(): PlatformOptionsConfiguration = toIosOptionsConfiguration()
