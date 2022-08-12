package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.android.core.SentryAndroid
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptionsCallback

/**
 * Sentry initialization with a context and option configuration handler.
 *
 * @param context Application context.
 * @param configuration Options configuration handler.
 */
actual fun initJvmTarget(context: Context?, configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    context?.let {
        SentryAndroid.init(it, options.toAndroidSentryOptionsCallback())
    }
}

actual typealias Context = Context
