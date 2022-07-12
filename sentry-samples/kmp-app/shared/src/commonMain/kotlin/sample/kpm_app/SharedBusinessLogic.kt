package sample.kpm_app

import io.sentry.kotlin.multiplatform.SentryKMP

object SharedBusinessLogic {
    fun hardCrash() {
        SentryKMP.crash()
    }

    fun captureMessage(message: String) {
        SentryKMP.captureMessage(message)
    }

    fun captureException(exception: Throwable) {
        SentryKMP.captureException(exception)
    }

    fun doException() {
        try {
            val arr = arrayOf(1)
            arr[2]
        } catch (exception: Exception) {
            captureException(exception)
        }
    }
}