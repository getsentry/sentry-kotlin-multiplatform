import io.sentry.kotlin.multiplatform.Sentry

/** Capture an event after the hosting application initializes Sentry. */
fun captureExample() {
    Sentry.captureMessage("Official SwiftPM sample")
}
