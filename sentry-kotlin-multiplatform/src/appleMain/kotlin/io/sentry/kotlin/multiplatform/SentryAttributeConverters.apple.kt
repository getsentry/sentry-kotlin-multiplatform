package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryAttribute
import platform.Foundation.NSNumber

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
