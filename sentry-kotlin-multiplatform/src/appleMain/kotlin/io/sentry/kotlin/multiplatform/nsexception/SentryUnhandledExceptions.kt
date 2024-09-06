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
import platform.Foundation.NSException
import platform.Foundation.NSNumber

private typealias InternalSentryEvent = Internal.Sentry.SentryEvent
private typealias InternalSentrySDK = Internal.Sentry.SentrySDK
private typealias InternalSentryEnvelope = Internal.Sentry.SentryEnvelope
private typealias InternalSentryDependencyContainer = Internal.Sentry.SentryDependencyContainer
private typealias InternalSentryEnvelopeHeader = Internal.Sentry.SentryEnvelopeHeader
private typealias InternalSentryEnvelopeItem = Internal.Sentry.SentryEnvelopeItem
private typealias InternalSentryThreadInspector = Internal.Sentry.SentryThreadInspector

private typealias CocoapodsSentryEvent = cocoapods.Sentry.SentryEvent
private typealias CocoapodsSentrySDK = cocoapods.Sentry.SentrySDK
private typealias CocoapodsSentryStacktrace = cocoapods.Sentry.SentryStacktrace
private typealias CocoapodsSentryException = cocoapods.Sentry.SentryException
private typealias CocoapodsSentryMechanism = cocoapods.Sentry.SentryMechanism
private typealias CocoapodsSentryThread = cocoapods.Sentry.SentryThread

/**
 * Drops the Kotlin crash that follows an unhandled Kotlin exception except our custom SentryEvent.
 */
internal fun dropKotlinCrashEvent(event: CocoapodsSentryEvent?): CocoapodsSentryEvent? {
    return event?.takeUnless {
        (it as InternalSentryEvent).isCrashEvent && (
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
    val envelope = throwable.asSentryEnvelope()
    // The envelope will be persisted, so we can safely terminate afterwards.
    // https://github.com/getsentry/sentry-cocoa/blob/678172142ac1d10f5ed7978f69d16ab03e801057/Sources/Sentry/SentryClient.m#L409
    InternalSentrySDK.storeEnvelope(envelope)
    CocoapodsSentrySDK.configureScope { scope ->
        scope?.setTagValue(KOTLIN_CRASH_TAG, KOTLIN_CRASH_TAG)
    }
}

/**
 * Tag used to mark the Kotlin termination crash.
 */
internal const val KOTLIN_CRASH_TAG = "nsexceptionkt.kotlin_crashed"

/**
 * Converts `this` [Throwable] to a [SentryEnvelope].
 */
internal fun Throwable.asSentryEnvelope(): InternalSentryEnvelope {
    val event = asSentryEvent() as InternalSentryEvent
    val preparedEvent = InternalSentrySDK.currentHub().let { hub ->
        hub.getClient()
            ?.prepareEvent(event, hub.scope, alwaysAttachStacktrace = false, isCrashEvent = true)
    } ?: event
    val item = InternalSentryEnvelopeItem(preparedEvent)
    // TODO: pass traceState when enabling performance monitoring for KMP SDK
    val header = InternalSentryEnvelopeHeader(preparedEvent.eventId, null)
    return InternalSentryEnvelope(header, listOf(item))
}

/**
 * Converts `this` [Throwable] to a [SentryEvent].
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
            // Crashed threats shouldn't have a stacktrace, the thread_id should be set on the exception instead
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
 * Converts `this` [NSException] to a [SentryException].
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
