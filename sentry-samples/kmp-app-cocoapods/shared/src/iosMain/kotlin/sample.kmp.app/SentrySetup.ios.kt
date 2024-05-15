package sample.kmp.app

import io.sentry.kotlin.multiplatform.PlatformOptionsConfiguration

actual fun createPlatformOptionsConfiguration(): PlatformOptionsConfiguration = {
    it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
}
