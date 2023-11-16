package sentry.kmp.demo.sentry

import io.sentry.kotlin.multiplatform.Attachment
import io.sentry.kotlin.multiplatform.OptionsConfiguration
import io.sentry.kotlin.multiplatform.Sentry

/** Shared options configuration */
private val optionsConfiguration: OptionsConfiguration = {
    it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
    it.attachStackTrace = true
    it.attachThreads = true
    it.attachScreenshot = true
    it.release = "kmp-release@0.0.1"
    it.beforeSend = { event ->
        if (event.environment == "test") {
            null
        } else {
            event
        }
    }
    it.beforeBreadcrumb = { breadcrumb ->
        breadcrumb.message = "Add message before every breadcrumb"
        breadcrumb
    }
    it.tracesSampleRate = 1.0
}

/**
 * Initializes Sentry with given options.
 * Make sure to hook this into your native platforms as early as possible
 */
fun initializeSentry() {
    Sentry.init(optionsConfiguration)
    configureSentryScope()
}

/** Configure scope applicable to all platforms */
private fun configureSentryScope() {
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
