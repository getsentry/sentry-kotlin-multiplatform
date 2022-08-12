package sample.kpm_app

import io.sentry.kotlin.multiplatform.Attachment
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryOptions

/** Configure scope applicable to all platforms */
fun configureSharedScope() {
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

/** Returns options configuration applicable to all platforms */
fun optionsConfiguration(): (SentryOptions) -> Unit {
    return {
        it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        it.attachStackTrace = true
        it.attachThreads = true
    }
}
