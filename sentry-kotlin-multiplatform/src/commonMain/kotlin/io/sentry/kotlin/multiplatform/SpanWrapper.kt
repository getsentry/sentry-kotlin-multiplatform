package io.sentry.kotlin.multiplatform

public class SpanWrapper(private val span: Span) : Span by span