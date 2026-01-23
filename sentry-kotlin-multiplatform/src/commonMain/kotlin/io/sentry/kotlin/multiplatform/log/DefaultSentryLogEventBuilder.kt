package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * Default implementation of [SentryLogEventBuilder] used by platform logger implementations.
 *
 * This builder only captures the message template, args, and attributes.
 * Formatting is delegated to the platform implementations (JVM/Cocoa).
 */
internal class DefaultSentryLogEventBuilder : SentryLogEventBuilder {
    internal var message: String? = null
        private set
    internal var args: Array<out Any?> = emptyArray()
        private set
    internal val attrs: SentryAttributes = SentryAttributes()

    override fun message(body: String) {
        this.message = body
        this.args = emptyArray()
    }

    override fun message(template: String, vararg args: Any?) {
        this.message = template
        this.args = args
    }

    override fun attributes(prebuilt: SentryAttributes) {
        prebuilt.forEach { attribute ->
            attrs.setAttribute(attribute)
        }
    }

    override fun attributes(block: SentryAttributes.() -> Unit) {
        attrs.block()
    }
}
