import io.sentry.Sentry

fun main(args: Array<String>) {
    Sentry.init("https://8ee5199a90354faf995292b15c196d48@o19635.ingest.sentry.io/4394")
    Sentry.captureMessage("Hello from Kotlin Console Application")
    println("Hello World!")
}