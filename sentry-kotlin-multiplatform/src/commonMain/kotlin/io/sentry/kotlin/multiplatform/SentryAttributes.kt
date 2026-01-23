package io.sentry.kotlin.multiplatform

import kotlin.collections.set

/**
 * A type-safe collection of [SentryAttributeValue]s.
 */
public open class SentryAttributes {
    private val entries: MutableMap<String, SentryAttributeValue> = mutableMapOf()

    /** Sets an attribute, replacing any existing attribute with the same key. */
    public open fun setAttribute(attribute: SentryAttributeValue) {
        entries[attribute.key] = attribute
    }

    /** Sets a list of attributes, replacing any existing attributes with the same key. */
    public open fun setAttributes(attributes: List<SentryAttributeValue>) {
        attributes.forEach { entries[it.key] = it }
    }

    /** Sets a map of attributes, replacing any existing attributes with the same key. */
    public open fun setAttributes(attributes: Map<String, SentryAttributeValue>) {
        attributes.forEach { entries[it.key] = it.value }
    }

    /** Removes an attribute by key. */
    public open fun removeAttribute(key: String) {
        entries.remove(key)
    }

    /** Gets an attribute by key, or null if not found. */
    public operator fun get(key: String): SentryAttributeValue? = entries[key]

    /** Returns true if an attribute with the given key exists. */
    public operator fun contains(key: String): Boolean = entries.containsKey(key)

    public operator fun set(key: String, value: String) {
        setAttribute(SentryAttributeValue.StringAttributeValue(key, value))
    }

    public operator fun set(key: String, value: Int) {
        setAttribute(SentryAttributeValue.IntAttributeValue(key, value))
    }

    public operator fun set(key: String, value: Double) {
        setAttribute(SentryAttributeValue.DoubleAttributeValue(key, value))
    }

    public operator fun set(key: String, value: Boolean) {
        setAttribute(SentryAttributeValue.BooleanAttributeValue(key, value))
    }

    /** Gets a String value by key, or null if not found or wrong type. */
    public fun getString(key: String): String? = getTyped<SentryAttributeValue.StringAttributeValue>(key)?.value as? String

    /** Gets an Int value by key, or null if not found or wrong type. */
    public fun getInt(key: String): Int? = getTyped<SentryAttributeValue.IntAttributeValue>(key)?.value as? Int

    /** Gets a Double value by key, or null if not found or wrong type. */
    public fun getDouble(key: String): Double? = getTyped<SentryAttributeValue.DoubleAttributeValue>(key)?.value as? Double

    /** Gets a Boolean value by key, or null if not found or wrong type. */
    public fun getBoolean(key: String): Boolean? = getTyped<SentryAttributeValue.BooleanAttributeValue>(key)?.value as? Boolean

    /** Returns true if there are no attributes. */
    public fun isEmpty(): Boolean = entries.isEmpty()

    /** Returns true if there are attributes. */
    public fun isNotEmpty(): Boolean = entries.isNotEmpty()

    /** Iterates over all attributes. */
    public fun forEach(action: (SentryAttributeValue) -> Unit) {
        entries.values.forEach(action)
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : SentryAttributeValue> getTyped(key: String): T? {
        val attr = entries[key] ?: return null
        return attr as? T
    }

    public companion object {
        /** Creates a [SentryAttributes] collection from the given list of attributes. */
        public fun of(attributes: List<SentryAttributeValue>): SentryAttributes {
            return SentryAttributes().apply {
                setAttributes(attributes)
            }
        }

        /** Creates a [SentryAttributes] collection from the given map of attributes. */
        public fun of(attributes: Map<String, SentryAttributeValue>): SentryAttributes {
            return SentryAttributes().apply {
                setAttributes(attributes)
            }
        }
    }
}
