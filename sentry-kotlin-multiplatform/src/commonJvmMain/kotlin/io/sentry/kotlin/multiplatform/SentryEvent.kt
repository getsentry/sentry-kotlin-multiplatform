package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpMessage
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpUser
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.User

public actual class SentryEvent(jvmSentryEvent: JvmSentryEvent? = null) : SentryBaseEvent() {
    public actual var level: SentryLevel? = jvmSentryEvent?.level?.toKmpSentryLevel()
    public actual var message: Message? = jvmSentryEvent?.message?.toKmpMessage()
    public actual var logger: String? = jvmSentryEvent?.logger
    public actual var fingerprint: List<String>? = jvmSentryEvent?.fingerprints?.toList()
    public override var release: String? = jvmSentryEvent?.release
    public override var environment: String? = jvmSentryEvent?.environment
    public override var platform: String? = jvmSentryEvent?.platform
    public override var user: User? = jvmSentryEvent?.user?.toKmpUser()
    public override var serverName: String? = jvmSentryEvent?.serverName
    public override var dist: String? = jvmSentryEvent?.dist

    init {
        jvmSentryEvent?.breadcrumbs?.forEach {
            addBreadcrumb(it.toKmpBreadcrumb())
        }
        jvmSentryEvent?.tags?.forEach { (key, value) ->
            setTag(key, value)
        }
    }
}
