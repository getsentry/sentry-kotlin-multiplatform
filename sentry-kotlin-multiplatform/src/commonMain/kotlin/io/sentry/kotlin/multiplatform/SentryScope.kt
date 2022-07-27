package io.sentry.kotlin.multiplatform

class SentryScope {

    /** Scope's SentryLevel */
    var sentryLevel: SentryLevel? = null

    /** Scope's SentryKMPOptions */
    var options: SentryKMPOptions? = null

    /** Scope's tags  */
    val tags: Map<String, String> = HashMap()

    /** Scope's extras  */
    val extra: Map<String, Any> = HashMap()

    /** Scope's fingerprint  */
    val fingerprint: List<String> = ArrayList()
}
