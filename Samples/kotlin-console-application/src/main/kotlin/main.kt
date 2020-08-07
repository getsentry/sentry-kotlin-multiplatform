import io.sentry.Sentry

fun main(args: Array<String>) {
    Sentry.init("https://c3e288e338f346a99f0ac65956c2e24e@o19635.ingest.sentry.io/5381417")
    Sentry.captureMessage("Hello from Kotlin Console Application")
    println("Hello World!")
}