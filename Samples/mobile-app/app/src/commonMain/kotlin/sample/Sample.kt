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
        Sentry.init("https://c3e288e338f346a99f0ac65956c2e24e@o19635.ingest.sentry.io/5381417")
    }

    fun proxyHello() = hello()
}
