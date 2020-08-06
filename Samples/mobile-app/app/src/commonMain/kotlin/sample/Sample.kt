package sample

import io.sentry.Sentry

expect class Sample() {
    fun checkMe(): Int
}

expect object Platform {
    val name: String
}

fun hello(): String {
    Sentry.start("https://8ee5199a90354faf995292b15c196d48@o19635.ingest.sentry.io/4394")
    Sentry.captureMessage("Hello from ${Platform.name}")
    return "Hello from ${Platform.name}"
}

class Proxy {
    fun proxyHello() = hello()
}

fun main() {
    println(hello())
}