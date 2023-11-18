package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

/**
 * This class deals with configuring and modifying a Breadcrumb. It is used to test any code that
 * can alter a Breadcrumb.
 */
expect class BreadcrumbConfigurator() {
  val originalBreadcrumb: Breadcrumb

  fun applyOptions(optionsConfiguration: OptionsConfiguration): Breadcrumb?

  fun applyOptions(options: SentryOptions = SentryOptions()): Breadcrumb?
}
