package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.prepareForInit
import kotlin.collections.forEach as kForEach

internal fun SentryOptions.toAndroidSentryOptionsCallback(): (SentryAndroidOptions) -> Unit = {
    // Apply base options available to all JVM targets
    it.applyJvmBaseOptions(this)

    // Apply Android specific options
    it.isAttachScreenshot = this.attachScreenshot
    it.isAttachViewHierarchy = this.attachViewHierarchy
    it.isAnrEnabled = this.isAnrEnabled
    it.anrTimeoutIntervalMillis = this.anrTimeoutIntervalMillis

    // kForEach solves an issue with linter where it thinks forEach is the Java version
    // see here: https://stackoverflow.com/questions/44751469/kotlin-extension-functions-suddenly-require-api-level-24/68897591#68897591
    this.sdk?.packages?.kForEach { sdkPackage ->
        it.sdkVersion?.addPackage(sdkPackage.name, sdkPackage.version)
    }

    if (it.sdkVersion?.packages?.none { it.name == BuildKonfig.SENTRY_ANDROID_PACKAGE_NAME } == true) {
        it.sdkVersion?.addPackage(BuildKonfig.SENTRY_ANDROID_PACKAGE_NAME, BuildKonfig.SENTRY_ANDROID_VERSION)
    }
}
