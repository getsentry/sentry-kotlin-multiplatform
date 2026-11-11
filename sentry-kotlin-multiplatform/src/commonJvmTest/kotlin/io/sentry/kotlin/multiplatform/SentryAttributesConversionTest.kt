package io.sentry.kotlin.multiplatform

/** Tests for SentryAttributes to JVM conversion. */
class SentryAttributesConversionTest : BaseSentryAttributesConversionTest() {
    override fun SentryAttributes.toMap(): Map<String, Any?> = toJvmSentryAttributes().attributes.mapValues { it.value.value }
}
