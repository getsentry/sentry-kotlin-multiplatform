package io.sentry.kotlin.multiplatform.extensions

import platform.Foundation.NSMutableDictionary
import platform.Foundation.allKeys

fun <K, V> NSMutableDictionary.toMutableMap(): MutableMap<K, V> {
    val keys = this.allKeys
    val map = mutableMapOf<K, V>()
    for (key in keys) {
        map.put(key as K, this.objectForKey(key) as V)
    }
    return map
}
