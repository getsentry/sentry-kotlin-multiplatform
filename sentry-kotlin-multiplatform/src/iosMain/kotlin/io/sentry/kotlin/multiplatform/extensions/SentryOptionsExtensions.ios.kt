package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toIosOptionsConfiguration(): (CocoaSentryOptions?) -> Unit = {
    // Apply base options available to all Cocoa/Apple targets
    it?.applyCocoaBaseOptions(this)

    // Apply iOS specific options
    it?.attachScreenshot = this.attachScreenshot
    it?.attachViewHierarchy = this.attachViewHierarchy
}
