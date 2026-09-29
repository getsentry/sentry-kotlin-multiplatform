package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryAttribute
import kotlinx.cinterop.convert
import platform.Foundation.NSNumber
import platform.darwin.NSInteger

/** Converts native scalar attributes for logging and metrics callbacks. */
internal fun Map<*, *>.toKmpSentryAttributes(): SentryAttributes {
    val converted = SentryAttributes.empty()
    forEach { (key, raw) ->
        val attribute = raw as? SentryAttribute ?: return@forEach
        val name = key as? String ?: return@forEach
        when (attribute.type()) {
            "string" -> converted[name] = attribute.value() as String
            "boolean" -> converted[name] = (attribute.value() as NSNumber).boolValue
            "integer" -> converted[name] = (attribute.value() as NSNumber).longLongValue
            "double" -> converted[name] = (attribute.value() as NSNumber).doubleValue
        }
    }
    return converted
}

/** Converts shared attributes to native attributes, skipping values the platform can't represent. */
internal fun SentryAttributes.toCocoaAttributes(): Map<Any?, SentryAttribute> =
    buildMap {
        this@toCocoaAttributes.forEach { (key, value) ->
            value.toCocoaAttribute()?.let { put(key, it) }
        }
    }

/** Returns null for integers outside the native NSInteger range, such as on arm64_32 watchOS. */
internal fun SentryAttributeValue.toCocoaAttribute(): SentryAttribute? =
    when (this) {
        is SentryAttributeValue.StringValue -> SentryAttribute(string = value as String)
        is SentryAttributeValue.BooleanValue -> SentryAttribute(boolean = value as Boolean)
        is SentryAttributeValue.DoubleValue -> SentryAttribute(double = value as Double)
        is SentryAttributeValue.LongValue -> {
            val long = value as Long
            val integer: NSInteger = long.convert()
            if (integer.toLong() == long) SentryAttribute(integer = integer) else null
        }
    }
