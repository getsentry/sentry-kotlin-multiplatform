package io.sentry.kotlin.multiplatform

/**
 * A type-safe mutable map of attribute key-value pairs.
 * * Supports type-safe setters for primitives. Use properties on [SentryAttributeValue]
 * (e.g., `stringOrNull`, `longOrNull`) to extract typed values.
 */
public class SentryAttributes private constructor(
    private val attributes: MutableMap<String, SentryAttributeValue>
) : MutableMap<String, SentryAttributeValue> by attributes {
    /** Sets a [String] value by key. */
    public operator fun set(key: String, value: String) {
        attributes[key] = SentryAttributeValue.string(value)
    }

    /** Sets an [Int] value by key. Stored as 64-bit Long value. */
    public operator fun set(key: String, value: Int) {
        attributes[key] = SentryAttributeValue.long(value.toLong())
    }

    /** Sets a [Long] value by key. */
    public operator fun set(key: String, value: Long) {
        attributes[key] = SentryAttributeValue.long(value)
    }

    /** Sets an [Float] value by key. Stored as 64-bit Double value. */
    public operator fun set(key: String, value: Float) {
        attributes[key] = SentryAttributeValue.double(value.toDouble())
    }

    /** Sets a [Double] value by key. */
    public operator fun set(key: String, value: Double) {
        attributes[key] = SentryAttributeValue.double(value)
    }

    /** Sets a [Boolean] value by key. */
    public operator fun set(key: String, value: Boolean) {
        attributes[key] = SentryAttributeValue.boolean(value)
    }

    /** Creates a copy of this [SentryAttributes] instance. */
    public fun copy(): SentryAttributes = SentryAttributes(attributes.toMutableMap())

    public companion object {
        /** Creates an empty [SentryAttributes] collection. */
        public fun empty(): SentryAttributes = SentryAttributes(mutableMapOf())

        /** Creates a [SentryAttributes] collection from the given map. */
        public fun of(attributes: Map<String, SentryAttributeValue>): SentryAttributes {
            return SentryAttributes(attributes.toMutableMap())
        }
    }
}
