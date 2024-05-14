package io.sentry.kotlin.multiplatform

/**
 * Platform-specific configuration class for Sentry.
 *
 * This class is expected to be implemented on each platform.
 * It is directly type aliased to the platform-specific configuration options provided by the
 * native Sentry SDKs, allowing developers to utilize native platform capabilities and
 * override settings with configurations that are specific to each platform.
 *
 * This is useful if you need to configure options that might not be supported through the
 * KMP SDK in the common code.
 *
 * Using this class in common code will do nothing, as its implementation
 * is platform-dependent and expected to be empty in shared code.
 *
 * @see Sentry.initWithPlatformOptions
 */
public expect class SentryPlatformOptions()

/**
 * Prepare the platform-specific options for initialization.
 *
 * This is not used through normal initialization, only in [Sentry.initWithPlatformOptions].
 */
internal expect fun SentryPlatformOptions.prepareForInit()