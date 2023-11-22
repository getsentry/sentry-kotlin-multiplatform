package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.applyCocoaBaseOptions
import io.sentry.kotlin.multiplatform.extensions.toKmpBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

actual class BreadcrumbConfigurator {
    private val cocoaBreadcrumb = CocoaBreadcrumb()
    actual val originalBreadcrumb: Breadcrumb = cocoaBreadcrumb.toKmpBreadcrumb()

    actual fun applyOptions(optionsConfiguration: OptionsConfiguration): Breadcrumb? {
        val kmpOptions = SentryOptions()
        optionsConfiguration.invoke(kmpOptions)
        return applyOptions(kmpOptions)
    }

    actual fun applyOptions(options: SentryOptions): Breadcrumb? {
        val cocoaOptions = CocoaSentryOptions()
        cocoaOptions.applyCocoaBaseOptions(options)
        val cocoaModifiedBreadcrumb = cocoaOptions.beforeBreadcrumb?.invoke(cocoaBreadcrumb)
        return cocoaModifiedBreadcrumb?.toKmpBreadcrumb()
    }
}
