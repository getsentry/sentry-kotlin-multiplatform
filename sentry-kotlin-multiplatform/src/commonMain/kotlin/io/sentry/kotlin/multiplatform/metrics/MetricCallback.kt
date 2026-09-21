package io.sentry.kotlin.multiplatform.metrics

/** User callback failures drop the metric, matching the native Java SDK's privacy policy. */
@Suppress("TooGenericExceptionCaught", "SwallowedException")
internal fun applyMetricCallback(
    callback: (SentryMetric) -> SentryMetric?,
    metric: SentryMetric,
): SentryMetric? =
    try {
        callback(metric)?.takeIf { it.value.isValid() }
    } catch (exception: Throwable) {
        null
    }
