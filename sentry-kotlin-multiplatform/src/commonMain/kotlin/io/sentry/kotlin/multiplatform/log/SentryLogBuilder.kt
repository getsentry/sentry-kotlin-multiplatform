package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * Factory that creates [SentryLogBuilder] instances.
 */
internal typealias SentryLogBuilderFactory = () -> SentryLogBuilder

/**
 * DSL builder for structured log messages with attributes.
 *
 * Use this builder inside `logger.<level> { }` blocks to set the message and optional attributes.
 *
 * ### Message
 * - [message] with a single string sets a plain message body
 * - [message] with template and args enables structured logging (template is captured separately)
 * - If called multiple times, the last call wins
 *
 * ### Attributes
 * - [attributes] with a block allows inline attribute definition using `this[key] = value`
 * - [attributes] with a prebuilt [SentryAttributes] merges those attributes
 * - Multiple calls merge into the same set; duplicate keys use the last value
 */
public interface SentryLogBuilder {
    /**
     * The message template, or null if not set.
     */
    public val template: String?

    /**
     * The message arguments for substitution.
     */
    public val args: Array<out Any?>

    /**
     * The custom attributes set by the user.
     */
    public val customAttributes: SentryAttributes

    /**
     * Sets a plain message body without template parameters.
     */
    public fun message(body: String)

    /**
     * Sets a structured message with template and parameters.
     *
     * @param template The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the template via toString()
     */
    public fun message(template: String, vararg args: Any?)

    /**
     * Merges prebuilt attributes into this log entry.
     */
    public fun attributes(prebuilt: SentryAttributes)

    /**
     * Defines attributes inline using a DSL block.
     *
     * Example:
     * ```
     * attributes {
     *     this["user.id"] = userId
     *     this["request.duration"] = durationMs
     * }
     * ```
     */
    public fun attributes(block: SentryAttributes.() -> Unit)


    /**
     * Builds a formatted log entry with message substitution done.
     *
     * This method:
     * - Substitutes %s placeholders with argument values (%% becomes literal %)
     * - Adds template info to attributes (sentry.message.template, sentry.message.parameter.N)
     * - Merges custom attributes
     *
     * Use this for platforms that need pre-formatted messages (Apple, Web, etc.)
     * The timestamp is provided by the native SDK, so it's not included here.
     *
     * @return A formatted log entry, or null if no message was set
     */
    public fun buildFormatted(): FormattedLog?
}

/**
 * A formatted log entry ready to be sent to native SDKs.
 *
 * This contains the pre-formatted message body and complete attributes
 * (including template info). The timestamp and level are provided by
 * the native SDK when the log is actually sent.
 */
public class FormattedLog(
    /** The formatted message body with placeholders substituted. */
    public val body: String,
    /** Complete attributes including template info and custom attributes. */
    public val attributes: SentryAttributes
)
