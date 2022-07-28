package sample.kpm_app

import io.sentry.kotlin.multiplatform.Sentry

object SharedBusinessLogic {
    fun hardCrash() {
        Sentry.crash()
    }

    fun captureMessage(message: String) {
        Sentry.captureMessage(message)
    }

    fun captureException(exception: Throwable) {
        Sentry.captureException(exception)
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