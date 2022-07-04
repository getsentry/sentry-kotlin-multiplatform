package sample.kpm_app

import android.content.Context
import io.sentry.kotlin.multiplatform.Sentry

class Sentry {
    companion object {
        /**
         * For manual initialization
         */
        fun init(dsn: String, context: Context) {
            Sentry.init(dsn, context)
        }

        fun captureMessage(message: String) {
            Sentry.captureMessage(message)
        }

        fun captureException(throwable: Throwable) {
            Sentry.captureException(throwable)
        }
    }
}