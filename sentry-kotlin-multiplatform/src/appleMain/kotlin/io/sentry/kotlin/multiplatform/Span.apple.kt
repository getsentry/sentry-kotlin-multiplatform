package io.sentry.kotlin.multiplatform

public actual interface TestSpan {
    /**
     * Starts a child Span.
     *
     * @param operation - new span operation name
     * @return a new transaction span
     */
    public actual fun startChild(operation: String): TestSpan

    /**
     * Starts a child Span.
     *
     * @param operation - new span operation name
     * @param description - new span description name
     * @return a new transaction span
     */
    public actual fun startChild(
        operation: String,
        description: String?
    ): TestSpan

    public actual fun finish()
}

public actual class SpanImpl : TestSpan {
    public actual override fun startChild(operation: String): TestSpan {
        TODO("Not yet implemented")
    }

    public actual override fun startChild(
        operation: String,
        description: String?
    ): TestSpan {
        TODO("Not yet implemented")
    }

    public actual override fun finish() {
    }

}