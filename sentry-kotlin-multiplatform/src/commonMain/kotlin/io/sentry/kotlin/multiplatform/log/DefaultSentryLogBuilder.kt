package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * Default factory that creates [DefaultSentryLogBuilder] instances.
 */
internal val DefaultSentryLogBuilderFactory: SentryLogBuilderFactory = ::DefaultSentryLogBuilder

/**
 * Default implementation of [SentryLogBuilder].
 *
 * This builder captures the message template, args, and attributes, and provides
 * both raw access (for JVM) and formatted output (for Apple/Web).
 *
 * Formatting logic for %s substitution and template attribute generation lives here,
 * so it's shared across all platforms that need it.
 */
internal class DefaultSentryLogBuilder : SentryLogBuilder {
    private val formatRegex = Regex("%%|%s")

    override var template: String? = null
        private set

    override var args: Array<out Any?> = emptyArray()
        private set

    override val customAttributes: SentryAttributes = SentryAttributes.empty()

    override fun message(body: String) {
        template = body
        args = emptyArray()
    }

    override fun message(template: String, vararg args: Any?) {
        this.template = template
        this.args = args
    }

    override fun attributes(prebuilt: SentryAttributes) {
        prebuilt.forEach { (key, value) ->
            customAttributes[key] = value
        }
    }

    override fun attributes(block: @SentryLogDsl SentryAttributes.() -> Unit) {
        customAttributes.block()
    }

    override fun buildFormatted(): FormattedLog? {
        val tmpl = template ?: return null

        val formattedBody = formatMessage(tmpl, args)
        val allAttributes = buildAllAttributes(tmpl, args, customAttributes)

        return FormattedLog(
            body = formattedBody,
            attributes = allAttributes
        )
    }

    /**
     * Formats the message by substituting %s placeholders with argument values.
     * Use %% to produce a literal percent sign.
     */
    private fun formatMessage(template: String, args: Array<out Any?>): String {
        if (!template.contains('%')) return template

        var argIndex = 0
        return formatRegex.replace(template) { match ->
            when {
                match.value == "%%" -> "%"
                argIndex < args.size -> args[argIndex++]?.toString() ?: "null"
                else -> "%s"
            }
        }
    }

    /**
     * Builds the complete attributes map including template info and custom attributes.
     */
    private fun buildAllAttributes(
        template: String,
        args: Array<out Any?>,
        customAttributes: SentryAttributes
    ): SentryAttributes {
        val result = SentryAttributes.empty()

        if (args.isNotEmpty()) {
            result["sentry.message.template"] = template
            args.forEachIndexed { index, arg ->
                result["sentry.message.parameter.$index"] = arg?.toString() ?: "null"
            }
        }

        customAttributes.forEach { (key, value) ->
            result[key] = value
        }

        return result
    }
}
