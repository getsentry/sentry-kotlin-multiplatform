package sample.kmp.app

import io.sentry.kotlin.multiplatform.Attachment
import io.sentry.kotlin.multiplatform.Context
import io.sentry.kotlin.multiplatform.HttpStatusCodeRange
import io.sentry.kotlin.multiplatform.OptionsConfiguration
import io.sentry.kotlin.multiplatform.Sentry

/** Configure scope applicable to all platforms */
fun configureSentryScope() {
    Sentry.configureScope {
        it.setContext("Custom Context", "Shared Context")
        it.setTag("custom-tag", "from shared code")
        it.addAttachment(
            Attachment(
                "This is a shared text attachment".encodeToByteArray(),
                "shared.log"
            )
        )
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
private fun optionsConfiguration(): OptionsConfiguration {
    return {
        it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        it.attachStackTrace = true
        it.attachThreads = true
        it.attachScreenshot = true
        it.attachViewHierarchy = true
        it.release = "kmp-release@0.0.1"
        it.debug = true
        it.failedRequestStatusCodes = listOf(HttpStatusCodeRange(400, 599))
        it.failedRequestTargets = listOf("httpbin.org")
        it.beforeBreadcrumb = { breadcrumb ->
            breadcrumb.message = "Add message before every breadcrumb"
            breadcrumb
        }
        it.beforeSend = { event ->
            if (event.environment == "test") {
                null
            } else {
                event
            }
        }
    }
}
