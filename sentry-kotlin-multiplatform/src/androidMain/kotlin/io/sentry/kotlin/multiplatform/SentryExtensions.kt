package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.android.core.SentryAndroid

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