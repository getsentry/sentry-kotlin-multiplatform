package com.example.common

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.init

actual fun sentryInit() {
    Sentry.init {
        it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        it.attachStackTrace = true
        it.attachThreads = true
    }
}
