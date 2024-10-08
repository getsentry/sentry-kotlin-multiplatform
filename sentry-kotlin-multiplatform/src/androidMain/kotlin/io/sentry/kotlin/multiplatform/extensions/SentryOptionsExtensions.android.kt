package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.JvmSentryReplayQuality
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.SentryReplayOptions
import kotlin.collections.forEach as kForEach

internal fun SentryOptions.toAndroidSentryOptionsCallback(): (SentryAndroidOptions) -> Unit = { androidOptions ->
    val kmpOptions = this

    // Apply base options available to all JVM targets
    androidOptions.applyJvmBaseOptions(kmpOptions)

    // Apply Android specific options
    androidOptions.isAttachScreenshot = kmpOptions.attachScreenshot
    androidOptions.isAttachViewHierarchy = kmpOptions.attachViewHierarchy
    androidOptions.isAnrEnabled = kmpOptions.isAnrEnabled
    androidOptions.anrTimeoutIntervalMillis = kmpOptions.anrTimeoutIntervalMillis

    // Replay options
    androidOptions.experimental.sessionReplay.redactAllText =
        kmpOptions.experimental.sessionReplay.redactAllText
    androidOptions.experimental.sessionReplay.redactAllImages =
        kmpOptions.experimental.sessionReplay.redactAllImages
    androidOptions.experimental.sessionReplay.sessionSampleRate =
        kmpOptions.experimental.sessionReplay.sessionSampleRate
    androidOptions.experimental.sessionReplay.errorSampleRate =
        kmpOptions.experimental.sessionReplay.onErrorSampleRate
    androidOptions.experimental.sessionReplay.quality =
        kmpOptions.experimental.sessionReplay.quality.toAndroidSentryQuality()

    // kForEach solves an issue with linter where it thinks forEach is the Java version
    // see here: https://stackoverflow.com/questions/44751469/kotlin-extension-functions-suddenly-require-api-level-24/68897591#68897591
    this.sdk?.packages?.kForEach { sdkPackage ->
        androidOptions.sdkVersion?.addPackage(sdkPackage.name, sdkPackage.version)
    }
}

internal fun SentryReplayOptions.Quality.toAndroidSentryQuality(): JvmSentryReplayQuality {
    val kmpQuality = this
    return when (kmpQuality) {
        SentryReplayOptions.Quality.LOW -> JvmSentryReplayQuality.LOW
        SentryReplayOptions.Quality.MEDIUM -> JvmSentryReplayQuality.MEDIUM
        SentryReplayOptions.Quality.HIGH -> JvmSentryReplayQuality.HIGH
    }
}
