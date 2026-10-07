import io.sentry.kotlin.multiplatform.Sentry

/** Initialize Sentry from shared code. Called by the iOS app on launch. */
fun initializeSentry() {
    Sentry.init {
        it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        it.debug = true
    }
}

/** Capture an event after the hosting application initializes Sentry. */
fun captureExample() {
    Sentry.captureMessage("Official SwiftPM sample")
}

/** Capture an exception after the hosting application initializes Sentry. */
fun captureExampleException() {
    Sentry.captureException(IllegalStateException("Official SwiftPM sample exception"))
}
