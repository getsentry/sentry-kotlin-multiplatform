package io.sentry.kotlin.multiplatform

import kotlin.collections.set

/**
 * A type-safe collection of [SentryAttribute]s.
 */
public open class SentryAttributes {
    private val entries: MutableMap<String, SentryAttribute> = mutableMapOf()

    /** Sets an attribute, replacing any existing attribute with the same key. */
    public open fun setAttribute(attribute: SentryAttribute) {
        entries[attribute.key] = attribute
    }

    /** Sets a list of attributes, replacing any existing attributes with the same key. */
    public open fun setAttributes(attributes: List<SentryAttribute>) {
        attributes.forEach { entries[it.key] = it }
    }

    /** Sets a map of attributes, replacing any existing attributes with the same key. */
    public open fun setAttributes(attributes: Map<String, SentryAttribute>) {
        attributes.forEach { entries[it.key] = it.value }
    }

    /** Removes an attribute by key. */
    public open fun removeAttribute(key: String) {
        entries.remove(key)
    }

    /** Gets an attribute by key, or null if not found. */
    public operator fun get(key: String): SentryAttribute? = entries[key]

    /** Returns true if an attribute with the given key exists. */
    public operator fun contains(key: String): Boolean = entries.containsKey(key)

    /** Gets a String value by key, or null if not found or wrong type. */
    public fun getString(key: String): String? = getTyped<SentryAttribute.StringAttribute>(key)?.value as? String

    /** Gets an Int value by key, or null if not found or wrong type. */
    public fun getInt(key: String): Int? = getTyped<SentryAttribute.IntAttribute>(key)?.value as? Int

    /** Gets a Double value by key, or null if not found or wrong type. */
    public fun getDouble(key: String): Double? = getTyped<SentryAttribute.DoubleAttribute>(key)?.value as? Double

    /** Gets a Boolean value by key, or null if not found or wrong type. */
    public fun getBoolean(key: String): Boolean? = getTyped<SentryAttribute.BooleanAttribute>(key)?.value as? Boolean

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : SentryAttribute> getTyped(key: String): T? {
        val attr = entries[key] ?: return null
        return attr as? T
    }

    public companion object {
        /** Creates a [SentryAttributes] collection from the given list of attributes. */
        public fun of(attributes: List<SentryAttribute>): SentryAttributes {
            return SentryAttributes().apply {
                setAttributes(attributes)
            }
        }

        /** Creates a [SentryAttributes] collection from the given map of attributes. */
        public fun of(attributes: Map<String, SentryAttribute>): SentryAttributes {
            return SentryAttributes().apply {
                setAttributes(attributes)
            }
        }
    }
}
