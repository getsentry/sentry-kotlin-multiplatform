package io.sentry.kotlin.multiplatform.log

/**
 * DSL marker for Sentry logging DSL.
 *
 * This annotation controls implicit receiver scope in nested DSL blocks,
 * preventing accidental access to outer receivers. For example, it prevents
 * calling [SentryLogBuilder.message] from within an [SentryLogBuilder.attributes] block.
 *
 * Without this marker, the following would compile (but is likely a mistake):
 * ```
 * logger.info {
 *     message("Outer")
 *     attributes {
 *         message("Oops!") // Accidentally calling outer receiver
 *     }
 * }
 * ```
 *
 * With this marker, accessing the outer receiver requires explicit qualification:
 * ```
 * logger.info {
 *     message("Outer")
 *     attributes {
 *         this@info.message("Explicit") // Must be explicit if intended
 *     }
 * }
 * ```
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
public annotation class SentryLogDsl
