package io.sentry.kotlin.multiplatform.extensions

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.posix.memcpy

fun <K, V> NSMutableDictionary.toMutableMap(): MutableMap<K, V> {
    val keys = this.allKeys
    val map = mutableMapOf<K, V>()
    for (key in keys) {
        map.put(key as K, this.objectForKey(key) as V)
    }
    return map
}

fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
    usePinned {
        memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
    }
}

fun ByteArray.toNSData(): NSData = memScoped {
    NSData.create(
        bytes = allocArrayOf(this@toNSData),
        length = this@toNSData.size.toULong().convert()
    )
}
