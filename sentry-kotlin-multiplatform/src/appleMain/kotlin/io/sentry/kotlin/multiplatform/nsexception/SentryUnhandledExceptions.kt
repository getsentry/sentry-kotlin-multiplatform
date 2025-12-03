// https://github.com/rickclephas/NSExceptionKt/blob/master/nsexception-kt-sentry/src/commonMain/kotlin/com/rickclephas/kmp/nsexceptionkt/sentry/Sentry.kt
//
// Copyright (c) 2022 Rick Clephas
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

package io.sentry.kotlin.multiplatform.nsexception

import Internal.Sentry.NSExceptionKt_SentryCrashStackCursorFromNSException
import Internal.Sentry.kSentryLevelFatal
import kotlinx.cinterop.invoke
import platform.Foundation.NSException
import platform.Foundation.NSNumber

private typealias InternalSentryEvent = Internal.Sentry.SentryEvent
private typealias InternalSentrySDK = Internal.Sentry.SentrySDKInternal
private typealias InternalSentryDependencyContainer = Internal.Sentry.SentryDependencyContainer
private typealias InternalSentryThreadInspector = Internal.Sentry.SentryThreadInspector

private typealias CocoapodsSentryEvent = cocoapods.Sentry.SentryEvent
private typealias CocoapodsSentrySDK = cocoapods.Sentry.SentrySDK
private typealias CocoapodsSentryStacktrace = cocoapods.Sentry.SentryStacktrace
private typealias CocoapodsSentryException = cocoapods.Sentry.SentryException
private typealias CocoapodsSentryMechanism = cocoapods.Sentry.SentryMechanism
private typealias CocoapodsSentryThread = cocoapods.Sentry.SentryThread
private typealias CocoapodsSentryEnvelope = cocoapods.Sentry.SentryEnvelope
private typealias CocoapodsSentryEnvelopeHeader = cocoapods.Sentry.SentryEnvelopeHeader
private typealias CocoapodsSentryEnvelopeItem = cocoapods.Sentry.SentryEnvelopeItem

/**
 * Drops the Kotlin crash that follows an unhandled Kotlin exception except our custom SentryEvent.
 */
internal fun dropKotlinCrashEvent(event: CocoapodsSentryEvent?): CocoapodsSentryEvent? {
    return event?.takeUnless {
        (it as InternalSentryEvent).isFatalEvent && (
            it.tags?.containsKey(
                KOTLIN_CRASH_TAG
            ) ?: false
            )
    }
}

/**
 * Sets the unhandled exception hook such that all unhandled exceptions are logged to Sentry as fatal exceptions.
 * If an unhandled exception hook was already set, that hook will be invoked after the exception is logged.
 * Note: once the exception is logged the program will be terminated.
 * @see wrapUnhandledExceptionHook
 */
public fun setSentryUnhandledExceptionHook(): Unit = wrapUnhandledExceptionHook { throwable ->
    val crashReporter = InternalSentryDependencyContainer.sharedInstance().crashReporter
    val handler = crashReporter.uncaughtExceptionHandler

    if (handler != null) {
        // This will:
        // 1. Write a crash report to disk with ALL synced scope data
        // 2. Include tags, user, context, breadcrumbs, etc.
        // 3. The crash will be sent on next app launch
        handler.invoke(throwable.asNSException(appendCausedBy = true))
    } else {
        // Fallback to old approach if handler not available
        val envelope = throwable.asSentryEnvelope()
        InternalSentrySDK.storeEnvelope(envelope as objcnames.classes.SentryEnvelope)
        CocoapodsSentrySDK.configureScope { scope ->
            scope?.setTagValue(KOTLIN_CRASH_TAG, KOTLIN_CRASH_TAG)
        }
    }
}

/**
 * Tag used to mark the Kotlin termination crash.
 */
internal const val KOTLIN_CRASH_TAG = "nsexceptionkt.kotlin_crashed"

/**
 * Converts `this` [Throwable] to a [SentryEnvelope].
 */
private fun Throwable.asSentryEnvelope(): CocoapodsSentryEnvelope {
    val event = asSentryEvent() as InternalSentryEvent
    val preparedEvent = InternalSentrySDK.currentHub().let { hub ->
        hub.getClient()
            ?.prepareEvent(event, hub.scope, alwaysAttachStacktrace = false, isFatalEvent = true)
    } ?: event
    val item = CocoapodsSentryEnvelopeItem(event = preparedEvent as cocoapods.Sentry.SentryEvent)
    // TODO: pass traceState when enabling performance monitoring for KMP SDK
    val header = CocoapodsSentryEnvelopeHeader(id = preparedEvent.eventId)
    return CocoapodsSentryEnvelope(header, listOf(item))
}

/**
 * Converts `this` [Throwable] to a [cocoapods.Sentry.SentryEvent].
 */
@Suppress("UnnecessaryOptInAnnotation")
private fun Throwable.asSentryEvent(): CocoapodsSentryEvent =
    CocoapodsSentryEvent(kSentryLevelFatal).apply {
        @Suppress("UNCHECKED_CAST")
        val threads =
            threadInspector?.getCurrentThreadsWithStackTrace() as List<CocoapodsSentryThread>?
        this.threads = threads
        val currentThread = threads?.firstOrNull { it.current?.boolValue ?: false }?.apply {
            setCrashed(NSNumber(true))
            // Crashed threads shouldn't have a stacktrace, the thread_id should be set on the exception instead
            // https://develop.sentry.dev/sdk/event-payloads/threads/
            stacktrace = null
        }
        debugMeta = threads?.let {
            InternalSentryDependencyContainer.sharedInstance().debugImageProvider.getDebugImagesForThreads(
                it
            )
        }
        exceptions = this@asSentryEvent
            .let { throwable -> throwable.causes.asReversed() + throwable }
            .map { it.asNSException().asSentryException(currentThread?.threadId) }
    }

/**
 * Converts `this` [NSException] to a [io.sentry.kotlin.multiplatform.protocol.SentryException].
 */
private fun NSException.asSentryException(
    threadId: NSNumber?
): CocoapodsSentryException = CocoapodsSentryException(reason ?: "", name ?: "Throwable").apply {
    this.threadId = threadId
    mechanism = CocoapodsSentryMechanism("generic").apply {
        setHandled(NSNumber(false))
    }
    stacktrace = threadInspector?.stacktraceBuilder?.let { stacktraceBuilder ->
        val cursor = NSExceptionKt_SentryCrashStackCursorFromNSException(this@asSentryException)
        val stacktrace = stacktraceBuilder.retrieveStacktraceFromCursor(cursor)
        stacktrace as CocoapodsSentryStacktrace
    }
}

private val threadInspector: InternalSentryThreadInspector?
    get() = InternalSentrySDK.currentHub().getClient()?.threadInspector
