package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.JvmTransactionContextProvider
import io.sentry.kotlin.multiplatform.SamplingContext
import io.sentry.kotlin.multiplatform.SentryEvent
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.TransactionContextAdapter

internal fun SentryOptions.toJvmSentryOptionsCallback(): (JvmSentryOptions) -> Unit = {
    it.applyJvmBaseOptions(this)

    // Apply JVM specific options
    it.sdkVersion?.name = sdk?.name ?: BuildKonfig.SENTRY_KMP_JAVA_SDK_NAME
    it.sdkVersion?.version = sdk?.version ?: BuildKonfig.VERSION_NAME

    sdk?.packages?.forEach { sdkPackage ->
        it.sdkVersion?.addPackage(sdkPackage.name, sdkPackage.version)
    }

    if (it.sdkVersion?.packages?.none { it.name == BuildKonfig.SENTRY_JAVA_PACKAGE_NAME } == true) {
        it.sdkVersion?.addPackage(
            BuildKonfig.SENTRY_JAVA_PACKAGE_NAME,
            BuildKonfig.SENTRY_JAVA_VERSION
        )
    }
}


/**
 * Applies the given base SentryOptions to this JvmSentryOption
 * This avoids code duplication during init on Android
 */
internal fun JvmSentryOptions.applyJvmBaseOptions(options: SentryOptions) {
    dsn = options.dsn
    isAttachThreads = options.attachThreads
    isAttachStacktrace = options.attachStackTrace
    dist = options.dist
    tracesSampleRate = options.tracesSampleRate
    environment = options.environment
    release = options.release
    isDebug = options.debug
    sessionTrackingIntervalMillis = options.sessionTrackingIntervalMillis
    isEnableAutoSessionTracking = options.enableAutoSessionTracking
    maxAttachmentSize = options.maxAttachmentSize
    maxBreadcrumbs = options.maxBreadcrumbs
    setBeforeBreadcrumb { jvmBreadcrumb, _ ->
        options.beforeBreadcrumb?.invoke(jvmBreadcrumb.toKmpBreadcrumb())?.toJvmBreadcrumb()
    }
    setBeforeSend { jvmSentryEvent, _ ->
        if (options.beforeSend == null) {
            jvmSentryEvent
        } else {
            options.beforeSend?.invoke(SentryEvent(jvmSentryEvent))?.let {
                jvmSentryEvent.applyKmpEvent(it)
            }
        }
    }
    setTracesSampler { jvmSamplingContext ->
        val jvmTransactionContext = JvmTransactionContextProvider(jvmSamplingContext.transactionContext)
        val transactionContext = TransactionContextAdapter(jvmTransactionContext)
        val samplingContext = SamplingContext(transactionContext)
        // returns null if KMP tracesSampler is null
        val sampleRate = options.tracesSampler?.invoke(samplingContext)
        sampleRate
    }
}