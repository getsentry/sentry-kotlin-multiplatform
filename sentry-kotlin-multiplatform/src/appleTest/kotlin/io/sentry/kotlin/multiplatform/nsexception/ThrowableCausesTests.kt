// https://github.com/rickclephas/NSExceptionKt/blob/master/nsexception-kt-core/src/commonTest/kotlin/com/rickclephas/kmp/nsexceptionkt/core/ThrowableCausesTests.kt
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

@file:OptIn(ExperimentalNativeApi::class)

package io.sentry.kotlin.multiplatform.nsexception

import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals

class ThrowableCausesTests {

  @Test
  fun testNoCauses() {
    assert(Throwable().causes.isEmpty())
  }

  @Test
  fun testSingleCause() {
    val cause = Throwable("Cause throwable")
    val throwable = Throwable("Test throwable", cause)
    assertEquals(listOf(cause), throwable.causes)
  }

  @Test
  fun testMultipleCauses() {
    val cause1 = Throwable("Cause 1 throwable")
    val cause2 = Throwable("Cause 2 throwable", cause1)
    val throwable = Throwable("Test throwable", cause2)
    assertEquals(listOf(cause2, cause1), throwable.causes)
  }

  private class MyThrowable(override val message: String?) : Throwable() {
    override var cause: Throwable? = null
  }

  @Test
  fun testReferenceCycle() {
    val cause = MyThrowable("Cause throwable")
    val throwable = Throwable("Test throwable", cause)
    cause.cause = throwable
    assertEquals(listOf(cause, throwable), throwable.causes)
  }
}
