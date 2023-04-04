package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpMessage
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpUser
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.User

public actual class SentryEvent(cocoaSentryEvent: CocoaSentryEvent? = null) : SentryBaseEvent() {
    public actual var level: SentryLevel? = cocoaSentryEvent?.level?.toKmpSentryLevel()
    public actual var message: Message? = cocoaSentryEvent?.message?.toKmpMessage()
    public actual var logger: String? = cocoaSentryEvent?.logger
    public actual var fingerprint: List<String>? =
        cocoaSentryEvent?.fingerprint()?.toList() as? List<String>
    public override var release: String? = cocoaSentryEvent?.releaseName
    public override var environment: String? = cocoaSentryEvent?.environment
    public override var platform: String? = cocoaSentryEvent?.platform
    public override var user: User? = cocoaSentryEvent?.user?.toKmpUser()
    public override var serverName: String? = cocoaSentryEvent?.serverName
    public override var dist: String? = cocoaSentryEvent?.dist

    init {
        cocoaSentryEvent?.breadcrumbs?.mapNotNull { it as? CocoaBreadcrumb }?.forEach {
            addBreadcrumb(it.toKmpBreadcrumb())
        }
        cocoaSentryEvent?.tags?.filterValues { it is String }?.forEach { (key, value) ->
            setTag(key as String, value as String)
        }
    }
}
