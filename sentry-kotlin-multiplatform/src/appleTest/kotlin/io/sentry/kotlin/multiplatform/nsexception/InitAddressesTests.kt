// https://github.com/rickclephas/NSExceptionKt/blob/master/nsexception-kt-core/src/commonTest/kotlin/com/rickclephas/kmp/nsexceptionkt/core/InitAddressesTests.kt
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

import kotlin.test.Test
import kotlin.test.assertEquals

class InitAddressesTests {

  private val qualifiedClassName = "my.app.CustomException"
  private val addresses = listOf<Long>(0, 1, 2, 3, 4, 5)
  private val stackTrace =
      arrayOf(
          "123 kfun:kotlin.Throwable#<init>(kotlin.String?){} + 24 abc",
          "456 kfun:kotlin.Exception#<init>(kotlin.String?){} + 5 def",
          "789 kfun:my.app.CustomException#<init>(kotlin.String?){} + 10 hij",
          "012 kfun:my.app.CustomException#<init>(){} + 12 klm",
          "345 kfun:my.app.class#function1(){} + 50 nop",
          "678 kfun:my.app.class#function2(){} + 60 qrs")

  @Test
  fun testDropInit() {
    val withoutInitAddresses = addresses.dropInitAddresses(qualifiedClassName, stackTrace, false)
    assertEquals(listOf<Long>(4, 5), withoutInitAddresses)
  }

  @Test
  fun testDropInitKeepLast() {
    val withoutInitAddresses = addresses.dropInitAddresses(qualifiedClassName, stackTrace, true)
    assertEquals(listOf<Long>(3, 4, 5), withoutInitAddresses)
  }

  private fun testDropInitUnknownClassName(keepLast: Boolean) {
    val qualifiedClassName = "my.app.SomeOtherException"
    val withoutInitAddresses = addresses.dropInitAddresses(qualifiedClassName, stackTrace, keepLast)
    assertEquals(addresses, withoutInitAddresses)
  }

  @Test fun testDropInitUnknownClassNameDropLast() = testDropInitUnknownClassName(false)

  @Test fun testDropInitUnknownClassNameKeepLast() = testDropInitUnknownClassName(true)
}
