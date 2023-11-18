// https://github.com/rickclephas/NSExceptionKt/blob/master/nsexception-kt-core/src/commonTest/kotlin/com/rickclephas/kmp/nsexceptionkt/core/CommonAddressesTests.kt
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
import kotlin.test.assertSame

class CommonAddressesTests {

  @Test
  fun testDropCommon() {
    val commonAddresses = listOf<Long>(5, 4, 3, 2, 1, 0)
    val addresses = listOf<Long>(8, 7, 6, 2, 1, 0)
    val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
    assertEquals(listOf<Long>(8, 7, 6), withoutCommonAddresses)
  }

  @Test
  fun testDropCommonEmptyCommon() {
    val addresses = listOf<Long>(0, 1, 2)
    val withoutCommonAddresses = addresses.dropCommonAddresses(emptyList())
    assertSame(addresses, withoutCommonAddresses)
  }

  @Test
  fun testDropCommonSameAddresses() {
    val addresses = listOf<Long>(0, 1, 2)
    val withoutCommonAddresses = addresses.dropCommonAddresses(addresses)
    assertEquals(emptyList(), withoutCommonAddresses)
  }
}
