package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes

/** Configures the attributes of a metric. */
public class SentryMetricBuilder internal constructor() {
    /**
     * Attributes attached to this call, overriding attributes inherited from native scope.
     * On 32-bit watchOS, integer attributes outside the signed 32-bit range are omitted.
     */
    public val attributes: SentryAttributes = SentryAttributes.empty()

    /** Adds attributes from an existing collection. */
    public fun attributes(attributes: SentryAttributes) {
        this.attributes.putAll(attributes)
    }

    /** Configures attributes using the same syntax as structured logging. */
    public fun attributes(block: SentryAttributes.() -> Unit) {
        attributes.block()
    }
}
