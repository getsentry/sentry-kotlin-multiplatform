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

import NSException.Sentry.*
import kotlinx.cinterop.UnsafeNumber
import platform.Foundation.NSException
import platform.Foundation.NSNumber

/**
 * Drops the Kotlin crash that follows an unhandled Kotlin exception.
 */
public fun dropKotlinCrashEvent(event: SentryEvent?): SentryEvent? {
    return event?.takeUnless { it.isCrashEvent && (it.tags?.containsKey(kotlinCrashedTag) ?: false) }
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
    SentrySDK.storeEnvelope(envelope)
    SentrySDK.configureScope { scope ->
        scope!!.setTagValue(kotlinCrashedTag, kotlinCrashedTag)
    }
}

/**
 * Tag used to mark the Kotlin termination crash.
 */
private const val kotlinCrashedTag = "nsexceptionkt.kotlin_crashed"

/**
 * Converts `this` [Throwable] to a [SentryEnvelope].
 */
private fun Throwable.asSentryEnvelope(): SentryEnvelope {
    val event = asSentryEvent()
    val preparedEvent = SentrySDK.currentHub().let { hub ->
        hub.getClient()?.prepareEvent(event, hub.scope, alwaysAttachStacktrace = false, isCrashEvent = true)
    } ?: event
    val item = SentryEnvelopeItem(preparedEvent)
    val header = SentryEnvelopeHeader(preparedEvent.eventId, SentrySDK.options?.sdkInfo, null)
    return SentryEnvelope(header, listOf(item))
}

/**
 * Converts `this` [Throwable] to a [SentryEvent].
 */
@Suppress("UnnecessaryOptInAnnotation")
@OptIn(UnsafeNumber::class)
private fun Throwable.asSentryEvent(): SentryEvent = SentryEvent(kSentryLevelFatal).apply {
    isCrashEvent = true
    @Suppress("UNCHECKED_CAST")
    val threads = threadInspector?.getCurrentThreadsWithStackTrace() as List<SentryThread>?
    this.threads = threads
    val currentThread = threads?.firstOrNull { it.current?.boolValue ?: false }?.apply {
        NSExceptionKt_SentryThreadSetCrashed(this)
        // Crashed threats shouldn't have a stacktrace, the thread_id should be set on the exception instead
        // https://develop.sentry.dev/sdk/event-payloads/threads/
        stacktrace = null
    }
    debugMeta = threads?.let {
        SentryDependencyContainer.sharedInstance().debugImageProvider.getDebugImagesForThreads(it)
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
): SentryException = SentryException(reason ?: "", name ?: "Throwable").apply {
    this.threadId = threadId
    mechanism = SentryMechanism("generic").apply {
        NSExceptionKt_SentryMechanismSetNotHandled(this)
    }
    stacktrace = threadInspector?.stacktraceBuilder?.let { stacktraceBuilder ->
        val cursor = NSExceptionKt_SentryCrashStackCursorFromNSException(this@asSentryException)
        val stacktrace = stacktraceBuilder.retrieveStacktraceFromCursor(cursor)
        NSExceptionKt_SentryCrashStackCursorCleanup(cursor)
        stacktrace
    }
}

private val threadInspector: SentryThreadInspector?
    get() = SentrySDK.currentHub().getClient()?.threadInspector
