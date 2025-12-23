package sample.kmp.app

import io.sentry.kotlin.multiplatform.Attachment
import io.sentry.kotlin.multiplatform.HttpStatusCodeRange
import io.sentry.kotlin.multiplatform.OptionsConfiguration
import io.sentry.kotlin.multiplatform.PlatformOptionsConfiguration
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
fun initializeSentry(useNativeOptions: Boolean = false) {
    if (useNativeOptions) {
        Sentry.initWithPlatformOptions(createPlatformOptionsConfiguration())
    } else {
        Sentry.init(optionsConfiguration())
    }
}

expect fun createPlatformOptionsConfiguration(): PlatformOptionsConfiguration

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
        it.logs.enabled = true
        it.failedRequestStatusCodes = listOf(HttpStatusCodeRange(400, 599))
        it.failedRequestTargets = listOf("httpbin.org")
        it.sessionReplay.onErrorSampleRate = 1.0
        it.sessionReplay.sessionSampleRate = 1.0
        it.logs.enabled = true
        it.logs.beforeSend = { log ->
            if (log.attributes["dont send log"]?.value == true) {
                null
            } else {
                log
            }
        }
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
