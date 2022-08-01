package io.sentry.kotlin.multiplatform

fun interface OptionsConfiguration<T: SentryOptions> {
    fun configure(options: T)
}
