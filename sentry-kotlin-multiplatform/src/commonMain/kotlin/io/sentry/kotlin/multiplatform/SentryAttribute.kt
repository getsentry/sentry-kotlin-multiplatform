package io.sentry.kotlin.multiplatform

/**
 * Represents a typed attribute with a key-value pair.
 */
public sealed class SentryAttribute(
    public val key: String,
    public val value: Any
) {
    public class StringAttribute(key: String, value: String) : SentryAttribute(key, value)
    public class IntAttribute(key: String, value: Int) : SentryAttribute(key, value)
    public class DoubleAttribute(key: String, value: Double) : SentryAttribute(key, value)
    public class BooleanAttribute(key: String, value: Boolean) : SentryAttribute(key, value)

    /** Convenience factory methods for creating attributes. */
    public companion object {
        /** Creates a string attribute. */
        public fun string(key: String, value: String): StringAttribute = StringAttribute(key, value)

        /** Creates an integer attribute. */
        public fun int(key: String, value: Int): IntAttribute = IntAttribute(key, value)

        /** Creates a double attribute. */
        public fun double(key: String, value: Double): DoubleAttribute = DoubleAttribute(key, value)

        /** Creates a boolean attribute. */
        public fun boolean(key: String, value: Boolean): BooleanAttribute = BooleanAttribute(key, value)
    }
}
