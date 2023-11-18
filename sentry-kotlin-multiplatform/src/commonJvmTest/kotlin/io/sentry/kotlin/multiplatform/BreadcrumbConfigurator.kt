package io.sentry.kotlin.multiplatform

import io.sentry.Hint
import io.sentry.kotlin.multiplatform.extensions.applyJvmBaseOptions
import io.sentry.kotlin.multiplatform.extensions.toKmpBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

actual class BreadcrumbConfigurator {
  private val jvmBreadcrumb = JvmBreadcrumb()
  actual val originalBreadcrumb: Breadcrumb = jvmBreadcrumb.toKmpBreadcrumb()

  actual fun applyOptions(optionsConfiguration: OptionsConfiguration): Breadcrumb? {
    val kmpOptions = SentryOptions()
    optionsConfiguration.invoke(kmpOptions)
    return applyOptions(kmpOptions)
  }

  actual fun applyOptions(options: SentryOptions): Breadcrumb? {
    val jvmOptions = JvmSentryOptions()
    jvmOptions.applyJvmBaseOptions(options)
    val jvmHint = Hint()
    val jvmModifiedBreadcrumb = jvmOptions.beforeBreadcrumb?.execute(jvmBreadcrumb, jvmHint)
    return jvmModifiedBreadcrumb?.toKmpBreadcrumb()
  }
}
