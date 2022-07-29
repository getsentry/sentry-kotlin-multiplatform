package io.sentry.kotlin.multiplatform

fun interface OptionsConfiguration<T: SentryKMPOptions> {
    fun configure(options: T)
}
