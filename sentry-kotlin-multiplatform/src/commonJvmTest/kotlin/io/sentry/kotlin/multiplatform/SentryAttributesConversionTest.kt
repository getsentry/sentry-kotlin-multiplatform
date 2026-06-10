package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.toJvmSentryAttributes

/** Tests for SentryAttributes to JVM conversion. */
class SentryAttributesConversionTest : BaseSentryAttributesConversionTest() {
    override fun SentryAttributes.toMap(): Map<String, Any?> =
        toJvmSentryAttributes().attributes.mapValues { it.value.value }
}
