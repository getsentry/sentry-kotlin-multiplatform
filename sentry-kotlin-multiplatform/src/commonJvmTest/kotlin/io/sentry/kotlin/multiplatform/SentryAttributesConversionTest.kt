package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.toJvmSentryAttributes

class SentryAttributesConversionTest : BaseSentryAttributesConversionTest() {
    override fun SentryAttributes.toMap(): Map<String, Any?> = toJvmSentryAttributes().attributes
}
