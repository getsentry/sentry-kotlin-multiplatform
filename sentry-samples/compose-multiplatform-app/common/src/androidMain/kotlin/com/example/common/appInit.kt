package com.example.common

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.init
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun sentryInit() {
    Sentry.init(LocalContext.current) {
        it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        it.attachStackTrace = true
        it.attachThreads = true
    }
}
