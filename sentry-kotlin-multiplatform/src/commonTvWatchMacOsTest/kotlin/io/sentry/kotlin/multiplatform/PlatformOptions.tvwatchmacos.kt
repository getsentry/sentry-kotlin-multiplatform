package io.sentry.kotlin.multiplatform

actual interface ApplePlatformOptions : PlatformOptions

class SentryTvWatchMacOsOptionsWrapper(cocoaOptions: CocoaSentryOptions) : SentryAppleOptionsWrapper(cocoaOptions)

actual fun createApplePlatformOptions(): PlatformOptions =
    SentryTvWatchMacOsOptionsWrapper(CocoaSentryOptions())

actual fun ApplePlatformOptions.assertApplePlatformSpecificOptions(options: SentryOptions) {
    // no-op
}