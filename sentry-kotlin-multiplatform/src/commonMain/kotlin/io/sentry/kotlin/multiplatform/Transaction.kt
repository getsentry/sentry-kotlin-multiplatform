package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId

public interface Transaction : Span {
    /** The transaction name. */
    public var name: String

    /**
     * Returns if transaction is sampled.
     *
     * @return is sampled
     */
    public fun isSampled(): Boolean?

    /**
     * Returns the latest span that is not finished.
     *
     * @return span or null if not found.
     */
    public fun getLatestActiveSpan(): Span

    /**
     * Returns transaction's event id.
     *
     * @return the event id
     */
    public fun getEventId(): SentryId
}
