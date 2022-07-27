package io.sentry.kotlin.multiplatform.extensions

import android.content.Context
import io.sentry.android.core.SentryAndroid
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryBridge
import io.sentry.kotlin.multiplatform.SentryKMPOptions

/**
 * Sentry initialization with a context and option configuration handler.
 *
 * @param context Application context.
 * @param configuration Options configuration handler.
 */
fun Sentry.init(context: Context, configuration: (SentryKMPOptions) -> Unit) {
    val options = SentryKMPOptions()
    configuration.invoke(options)
    SentryAndroid.init(context, SentryBridge.convertToSentryAndroidOptions(options))
}