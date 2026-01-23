package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes

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
public interface SentryLogEventBuilder {
    /**
     * Sets a plain message body without template parameters.
     */
    public fun message(body: String)

    /**
     * Sets a structured message with template and parameters.
     *
     * @param template The message template with format specifiers %s
     * @param args Arguments to substitute into the template
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
}