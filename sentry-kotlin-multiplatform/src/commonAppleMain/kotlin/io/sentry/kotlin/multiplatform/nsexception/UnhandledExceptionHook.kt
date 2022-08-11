// https://github.com/rickclephas/NSExceptionKt/blob/master/nsexception-kt-core/src/commonMain/kotlin/com/rickclephas/kmp/nsexceptionkt/core/UnhandledExceptionHook.kt
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

import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

/**
 * Wraps the unhandled exception hook such that the provided [hook] is invoked
 * before the currently set unhandled exception hook is invoked.
 * Note: once the unhandled exception hook returns the program will be terminated.
 * @see setUnhandledExceptionHook
 * @see terminateWithUnhandledException
 */
@OptIn(ExperimentalStdlibApi::class)
internal fun wrapUnhandledExceptionHook(hook: (Throwable) -> Unit) {
    val prevHook = AtomicReference<ReportUnhandledExceptionHook?>(null)
    val wrappedHook: ReportUnhandledExceptionHook = {
        hook(it)
        prevHook.value?.invoke(it)
        terminateWithUnhandledException(it)
    }
    prevHook.value = setUnhandledExceptionHook(wrappedHook.freeze())
}
