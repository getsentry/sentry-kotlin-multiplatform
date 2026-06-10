package io.sentry.kotlin.multiplatform.extensions

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSMutableDictionary
import platform.Foundation.allKeys
import platform.Foundation.create
import platform.posix.memcpy

internal fun <K, V> NSMutableDictionary.toMutableMap(): MutableMap<K, V> {
    val keys = this.allKeys
    val map = mutableMapOf<K, V>()
    for (key in keys) {
        map.put(key as K, this.objectForKey(key) as V)
    }
    return map
}

internal fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
    usePinned {
        memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
    }
}

internal fun ByteArray.toNSData(): NSData = memScoped {
    NSData.create(
        bytes = allocArrayOf(this@toNSData),
        length = this@toNSData.size.toULong().convert()
    )
}
