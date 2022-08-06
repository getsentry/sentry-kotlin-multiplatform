package sample.kpm_app

import io.sentry.kotlin.multiplatform.Sentry

fun configureSharedScope() {
    Sentry.configureScope {
        it.setContext("Custom Context", "Shared Context")
        it.setTag("custom-tag", "from shared code")
    }
}
