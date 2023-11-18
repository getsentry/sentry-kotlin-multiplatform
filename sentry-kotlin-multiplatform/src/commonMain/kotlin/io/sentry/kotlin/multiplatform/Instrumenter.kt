package io.sentry.kotlin.multiplatform

/**
 * Which framework is responsible for instrumenting. This includes starting and stopping of
 * transactions and spans.
 */
public enum class Instrumenter {
  SENTRY,

  /** OpenTelemetry */
  OTEL
}
