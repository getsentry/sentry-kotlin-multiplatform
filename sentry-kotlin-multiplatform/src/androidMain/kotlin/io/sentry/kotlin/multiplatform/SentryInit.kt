package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.android.core.SentryAndroid
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptionsCallback

internal actual fun initSentry(context: Context, configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)
    SentryAndroid.init(context, options.toAndroidSentryOptionsCallback())
}

actual typealias Context = Context
