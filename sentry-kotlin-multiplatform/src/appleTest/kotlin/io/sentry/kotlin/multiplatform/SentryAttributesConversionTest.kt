package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.toCocoaMap

class SentryAttributesConversionTest : BaseSentryAttributesConversionTest() {
    @Suppress("UNCHECKED_CAST")
    override fun SentryAttributes.toMap(): Map<String, Any?> =
        toCocoaMap() as Map<String, Any?>
}
