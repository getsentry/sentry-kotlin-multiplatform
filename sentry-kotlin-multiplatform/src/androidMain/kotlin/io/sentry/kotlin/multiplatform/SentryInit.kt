package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.android.core.SentryAndroid
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptionsCallback

actual fun initJvmTarget(context: Context?, configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)
    context?.let {
        SentryAndroid.init(it, options.toAndroidSentryOptionsCallback())
    }
}

actual typealias Context = Context
