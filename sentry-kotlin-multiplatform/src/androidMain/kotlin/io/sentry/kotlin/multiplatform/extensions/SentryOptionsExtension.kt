package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toSentryAndroidOptions(): (SentryAndroidOptions) -> Unit = {
    it.applyBaseOptions(this)

    it.isAnrEnabled = this.anrEnabled
    it.anrTimeoutIntervalMillis = this.anrTimeoutIntervalMillis
    it.isAnrReportInDebug = this.anrReportInDebug
    it.isEnableUserInteractionBreadcrumbs = this.enableUserInteractionBreadcrumbs
    it.isEnableAppLifecycleBreadcrumbs = this.enableAppLifecycleBreadcrumbs
    it.isEnableActivityLifecycleBreadcrumbs = this.enableActivityLifecycleBreadcrumbs
    it.isEnableSystemEventBreadcrumbs = this.enableSystemEventBreadcrumbs
    it.isEnableAppComponentBreadcrumbs = this.enableAppComponentBreadcrumbs
    it.isCollectAdditionalContext = this.collectAdditionalContext
    it.isAttachScreenshot = this.attachScreenshot
}
