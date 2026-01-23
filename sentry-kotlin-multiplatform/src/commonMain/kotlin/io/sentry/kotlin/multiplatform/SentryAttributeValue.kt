package io.sentry.kotlin.multiplatform



/**
 * Represents a typed attribute with a key-value pair.
 */
public sealed class SentryAttributeValue(
    public val key: String,
    public val value: Any
) {
    public class StringAttributeValue(key: String, value: String) : SentryAttributeValue(key, value)
    public class IntAttributeValue(key: String, value: Int) : SentryAttributeValue(key, value)
    public class DoubleAttributeValue(key: String, value: Double) : SentryAttributeValue(key, value)
    public class BooleanAttributeValue(key: String, value: Boolean) : SentryAttributeValue(key, value)

    /** Convenience factory methods for creating attributes. */
    public companion object Companion {
        /** Creates a string attribute. */
        public fun string(key: String, value: String): StringAttributeValue = StringAttributeValue(key, value)

        /** Creates an integer attribute. */
        public fun int(key: String, value: Int): IntAttributeValue = IntAttributeValue(key, value)

        /** Creates a double attribute. */
        public fun double(key: String, value: Double): DoubleAttributeValue = DoubleAttributeValue(key, value)

        /** Creates a boolean attribute. */
        public fun boolean(key: String, value: Boolean): BooleanAttributeValue = BooleanAttributeValue(key, value)
    }
}
