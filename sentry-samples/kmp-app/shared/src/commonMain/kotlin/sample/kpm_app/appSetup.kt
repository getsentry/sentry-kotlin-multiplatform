package sample.kpm_app

import io.sentry.kotlin.multiplatform.Context
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryOptions

/** Configure scope applicable to all platforms */
fun configureSentryScope() {
    Sentry.configureScope {
        it.setContext("Custom Context", "Shared Context")
        it.setTag("custom-tag", "from shared code")
    }
}

/**
 * Initializes Sentry with given options.
 * Make sure to hook this into your native platforms as early as possible
 */
fun initializeSentry(context: Context) {
    Sentry.init(context, optionsConfiguration())
}

/**
 * Convenience initializer for Cocoa targets.
 * Kotlin -> ObjC doesn't support default parameters (yet).
 * Otherwise, you would need to do this: AppSetupKt.initializeSentry(context: nil) in Swift.
 */
fun initializeSentry() {
    Sentry.init(optionsConfiguration())
}

/** Returns a shared options configuration */
private fun optionsConfiguration(): (SentryOptions) -> Unit {
    return {
        it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        it.attachStackTrace = true
        it.attachThreads = true
        it.attachScreenshot = true
    }
}
