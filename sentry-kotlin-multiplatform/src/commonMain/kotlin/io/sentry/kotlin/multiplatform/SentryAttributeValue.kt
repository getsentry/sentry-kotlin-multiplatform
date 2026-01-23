package io.sentry.kotlin.multiplatform

/**
 * Represents a typed attribute value.
 * 
 * Used as the value type in [SentryAttributes] map.
 * 
 * Supported types per the Sentry logs spec:
 * - String
 * - Boolean
 * - Long (64-bit signed integer)
 * - Double (64-bit floating point)
 */
public sealed class SentryAttributeValue(
    public val value: Any
) {
    public class StringValue(value: String) : SentryAttributeValue(value)
    public class LongValue(value: Long) : SentryAttributeValue(value)
    public class DoubleValue(value: Double) : SentryAttributeValue(value)
    public class BooleanValue(value: Boolean) : SentryAttributeValue(value)

    /** Returns the String value, or null if this is not a [StringValue]. */
    public val stringOrNull: String? get() = (this as? StringValue)?.value as? String

    /** Returns the Long value, or null if this is not a [LongValue]. */
    public val longOrNull: Long? get() = (this as? LongValue)?.value as? Long

    /** Returns the Double value, or null if this is not a [DoubleValue]. */
    public val doubleOrNull: Double? get() = (this as? DoubleValue)?.value as? Double

    /** Returns the Boolean value, or null if this is not a [BooleanValue]. */
    public val booleanOrNull: Boolean? get() = (this as? BooleanValue)?.value as? Boolean

    /** Convenience factory methods for creating attribute values. */
    public companion object {
        /** Creates a string value. */
        public fun string(value: String): StringValue = StringValue(value)

        /** Creates a 64-bit integer value. */
        public fun long(value: Long): LongValue = LongValue(value)

        /** Creates a double value. */
        public fun double(value: Double): DoubleValue = DoubleValue(value)

        /** Creates a boolean value. */
        public fun boolean(value: Boolean): BooleanValue = BooleanValue(value)
    }
}
