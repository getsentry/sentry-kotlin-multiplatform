package sample

import io.sentry.Sentry

expect class Sample() {
    fun checkMe(): Int
}

expect object Platform {
    val name: String
}

fun hello(): String {
    val message = "Hello from ${Platform.name}"
    Sentry.captureMessage(message)
    return message
}

class Proxy {
    init {
        Sentry.start("https://8ee5199a90354faf995292b15c196d48@o19635.ingest.sentry.io/4394")
    }

    fun proxyHello() = hello()
}
