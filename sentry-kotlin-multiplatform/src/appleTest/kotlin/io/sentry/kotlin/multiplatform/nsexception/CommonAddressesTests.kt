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
import kotlin.test.assertTrue

class CommonAddressesTests {

    // MARK: - Basic Functionality Tests (Positive Cases)

    @Test
    fun testDropCommon() {
        val commonAddresses = listOf<Long>(5, 4, 3, 2, 1, 0)
        val addresses = listOf<Long>(8, 7, 6, 2, 1, 0)
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(8, 7, 6), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonPartialMatch() {
        val commonAddresses = listOf<Long>(3, 2, 1)
        val addresses = listOf<Long>(9, 8, 7, 2, 1)
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(9, 8, 7), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonNoMatch() {
        val commonAddresses = listOf<Long>(5, 4, 3)
        val addresses = listOf<Long>(9, 8, 7, 6)
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertSame(addresses, withoutCommonAddresses)
    }

    @Test
    fun testDropCommonSingleElementMatch() {
        val commonAddresses = listOf<Long>(1)
        val addresses = listOf<Long>(5, 4, 3, 1)
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(5, 4, 3), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonSingleElementNoMatch() {
        val commonAddresses = listOf<Long>(2)
        val addresses = listOf<Long>(5, 4, 3, 1)
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertSame(addresses, withoutCommonAddresses)
    }

    // MARK: - Edge Cases (Boundary Conditions)

    @Test
    fun testDropCommonEmptyCommon() {
        val addresses = listOf<Long>(0, 1, 2)
        val withoutCommonAddresses = addresses.dropCommonAddresses(emptyList())
        assertSame(addresses, withoutCommonAddresses)
    }

    @Test
    fun testDropCommonEmptyAddresses() {
        val commonAddresses = listOf<Long>(1, 2, 3)
        val addresses = emptyList<Long>()
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertSame(addresses, withoutCommonAddresses)
    }

    @Test
    fun testDropCommonBothEmpty() {
        val addresses = emptyList<Long>()
        val commonAddresses = emptyList<Long>()
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertSame(addresses, withoutCommonAddresses)
    }

    @Test
    fun testDropCommonSameAddresses() {
        val addresses = listOf<Long>(0, 1, 2)
        val withoutCommonAddresses = addresses.dropCommonAddresses(addresses)
        assertEquals(emptyList(), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonLargerCommonList() {
        val commonAddresses = listOf<Long>(5, 4, 3, 2, 1, 0)
        val addresses = listOf<Long>(2, 1, 0)
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(emptyList(), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonLargerAddressList() {
        val commonAddresses = listOf<Long>(2, 1, 0)
        val addresses = listOf<Long>(9, 8, 7, 6, 5, 4, 2, 1, 0)
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(9, 8, 7, 6, 5, 4), withoutCommonAddresses)
    }

    // MARK: - Regression Tests (Prevent IndexOutOfBoundsException)

    @Test
    fun testDropCommonNoIndexOutOfBounds_LargeCommonList() {
        // This test specifically targets the original bug where i-- could become -1
        val commonAddresses = (0L..26L).toList().reversed() // 27 elements: [26, 25, ..., 1, 0]
        val addresses = listOf<Long>(30, 29, 28, 2, 1, 0)
        
        // This should not throw IndexOutOfBoundsException
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(30, 29, 28), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonNoIndexOutOfBounds_ExactSizeMatch() {
        // Test when commonAddresses.size equals the number of matching elements
        val commonAddresses = listOf<Long>(4, 3, 2, 1, 0)
        val addresses = listOf<Long>(9, 8, 7, 4, 3, 2, 1, 0)
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(9, 8, 7), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonNoIndexOutOfBounds_OffByOneScenario() {
        // Test the exact scenario that caused the original crash
        val commonAddresses = (1L..27L).toList() // size: 27
        val addresses = listOf<Long>(100L, 99L, 98L) + commonAddresses.takeLast(3)
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(100L, 99L, 98L), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonNoIndexOutOfBounds_SingleElementCommon() {
        val commonAddresses = listOf<Long>(0)
        val addresses = listOf<Long>(5, 4, 3, 2, 1, 0)
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(5, 4, 3, 2, 1), withoutCommonAddresses)
    }

    // MARK: - Performance and Stress Tests

    @Test
    fun testDropCommonLargeList() {
        val commonAddresses = (0L..999L).toList().reversed()
        val addresses = (500L..1499L).toList()
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertTrue("Result should contain elements not in common", withoutCommonAddresses.isNotEmpty())
        assertEquals(500, withoutCommonAddresses.size) // Should keep 1000-1499
    }

    @Test
    fun testDropCommonRepeatedElements() {
        val commonAddresses = listOf<Long>(1, 1, 1, 0, 0)
        val addresses = listOf<Long>(5, 4, 1, 1, 0)
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(5, 4, 1, 1), withoutCommonAddresses)
    }

    // MARK: - Sequence and Order Tests

    @Test
    fun testDropCommonMaintainsOrder() {
        val commonAddresses = listOf<Long>(3, 2, 1)
        val addresses = listOf<Long>(9, 8, 7, 6, 5, 3, 2, 1)
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(9, 8, 7, 6, 5), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonOutOfSequenceMatch() {
        // Common addresses are in different order than in the target list
        val commonAddresses = listOf<Long>(1, 3, 2) // Note: 3 and 2 are swapped
        val addresses = listOf<Long>(9, 8, 7, 6, 2)
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(9, 8, 7, 6), withoutCommonAddresses) // Should drop 2
    }

    @Test
    fun testDropCommonPartialSequenceMatch() {
        val commonAddresses = listOf<Long>(4, 3, 2, 1)
        val addresses = listOf<Long>(9, 8, 3, 2, 1) // Missing 4 in sequence
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(9, 8), withoutCommonAddresses) // Should drop 3,2,1 but keep others
    }

    // MARK: - Negative Test Cases

    @Test
    fun testDropCommonWithNegativeNumbers() {
        val commonAddresses = listOf<Long>(-1, -2, -3)
        val addresses = listOf<Long>(1, 0, -1, -2, -3)
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(1, 0), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonMixedPositiveNegative() {
        val commonAddresses = listOf<Long>(1, 0, -1)
        val addresses = listOf<Long>(5, 4, 3, 1, 0, -1)
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(5, 4, 3), withoutCommonAddresses)
    }

    @Test
    fun testDropCommonLargeNumbers() {
        val commonAddresses = listOf<Long>(Long.MAX_VALUE - 1, Long.MAX_VALUE - 2)
        val addresses = listOf<Long>(Long.MAX_VALUE, Long.MAX_VALUE - 1, Long.MAX_VALUE - 2)
        
        val withoutCommonAddresses = addresses.dropCommonAddresses(commonAddresses)
        assertEquals(listOf<Long>(Long.MAX_VALUE), withoutCommonAddresses)
    }
}
