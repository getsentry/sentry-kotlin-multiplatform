// Integration tests for the complete stack trace filtering pipeline
//
// Copyright (c) 2024 Sentry
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
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class StackTraceFilteringIntegrationTests {

    // MARK: - Mock Implementation for Testing

    // Mock extension function to simulate getStackTraceAddresses for testing
    private fun createMockThrowable(
        addresses: List<Long>,
        stackTrace: Array<String>,
        className: String = "TestException"
    ): TestThrowable {
        return TestThrowable(addresses, stackTrace, className)
    }

    private class TestThrowable(
        private val mockAddresses: List<Long>,
        private val mockStackTrace: Array<String>,
        private val mockClassName: String
    ) : Throwable() {
        
        fun getStackTraceAddresses(): List<Long> = mockAddresses
        
        override fun getStackTrace(): Array<StackTraceElement> {
            return mockStackTrace.map { 
                StackTraceElement("", "", "", 0) 
            }.toTypedArray()
        }
        
        fun getStackTrace(): Array<String> = mockStackTrace
        
        fun getFilteredStackTraceAddresses(
            keepLastInit: Boolean = false,
            commonAddresses: List<Long> = emptyList()
        ): List<Long> = getStackTraceAddresses().dropInitAddresses(
            qualifiedClassName = mockClassName,
            stackTrace = getStackTrace(),
            keepLast = keepLastInit
        ).dropCommonAddresses(commonAddresses)
    }

    // MARK: - Integration Tests

    @Test
    fun testCompleteFilteringPipeline_NormalCase() {
        val addresses = listOf<Long>(10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
        val stackTrace = arrayOf(
            "kfun:kotlin.Throwable#<init>(kotlin.String?){} + 24",
            "kfun:kotlin.Exception#<init>(kotlin.String?){} + 5",
            "kfun:TestException#<init>(kotlin.String?){} + 10",  // addresses[2]
            "kfun:TestException#<init>(){} + 12",                // addresses[3]
            "kfun:my.app.class#function1(){} + 50",              // addresses[4] - should be kept
            "kfun:my.app.class#function2(){} + 60",              // addresses[5] - should be kept
            "kfun:my.app.class#function3(){} + 70",              // addresses[6] - should be kept
            "kfun:my.app.class#function4(){} + 80",              // addresses[7] - should be kept
            "kfun:my.app.class#function5(){} + 90",              // addresses[8] - common
            "kfun:my.app.class#function6(){} + 100"              // addresses[9] - common
        )
        val commonAddresses = listOf<Long>(18, 19) // Last two addresses
        
        val throwable = createMockThrowable(addresses, stackTrace, "TestException")
        val filtered = throwable.getFilteredStackTraceAddresses(
            keepLastInit = false,
            commonAddresses = commonAddresses
        )
        
        // Should drop addresses[0,1,2,3] (init) and addresses[8,9] (common)
        assertEquals(listOf<Long>(14, 15, 16, 17), filtered)
    }

    @Test
    fun testCompleteFilteringPipeline_WithIndexOutOfBoundsRegression() {
        // This test recreates the exact scenario that caused the original crash
        val addresses = (100L..126L).toList() // 27 addresses
        val stackTrace = arrayOf(
            "kfun:kotlin.Throwable#<init>(kotlin.String?){} + 24",
            "kfun:LargeException#<init>(kotlin.String?){} + 10",
            "kfun:my.app.class#function1(){} + 50"
        ) + (3..26).map { "kfun:my.app.class#function$it(){} + ${it * 10}" }.toTypedArray()
        
        // Create a large common addresses list (27 elements) that would trigger the bug
        val commonAddresses = (0L..26L).toList() // This caused the original IndexOutOfBoundsException
        
        val throwable = createMockThrowable(addresses, stackTrace, "LargeException")
        
        // This should NOT throw IndexOutOfBoundsException
        val filtered = throwable.getFilteredStackTraceAddresses(
            keepLastInit = false,
            commonAddresses = commonAddresses
        )
        
        // Should process without crashing
        assertNotNull(filtered)
        assertTrue("Filtered addresses should be a valid list", filtered is List<Long>)
    }

    @Test
    fun testCompleteFilteringPipeline_KeepLastInit() {
        val addresses = listOf<Long>(20, 21, 22, 23, 24, 25, 26)
        val stackTrace = arrayOf(
            "kfun:kotlin.Throwable#<init>(kotlin.String?){} + 24",
            "kfun:CustomException#<init>(kotlin.String?){} + 10", // addresses[1]
            "kfun:CustomException#<init>(){} + 12",               // addresses[2] - last init, should be kept
            "kfun:my.app.class#function1(){} + 50",               // addresses[3]
            "kfun:my.app.class#function2(){} + 60",               // addresses[4]
            "kfun:my.app.class#function3(){} + 70",               // addresses[5] - common
            "kfun:my.app.class#function4(){} + 80"                // addresses[6] - common
        )
        val commonAddresses = listOf<Long>(25, 26) // Last two addresses
        
        val throwable = createMockThrowable(addresses, stackTrace, "CustomException")
        val filtered = throwable.getFilteredStackTraceAddresses(
            keepLastInit = true,
            commonAddresses = commonAddresses
        )
        
        // Should drop addresses[0,1] (init except last), and addresses[5,6] (common)
        assertEquals(listOf<Long>(22, 23, 24), filtered)
    }

    @Test
    fun testCompleteFilteringPipeline_EmptyAfterFiltering() {
        val addresses = listOf<Long>(30, 31, 32)
        val stackTrace = arrayOf(
            "kfun:kotlin.Throwable#<init>(kotlin.String?){} + 24",
            "kfun:SmallException#<init>(kotlin.String?){} + 10",
            "kfun:my.app.class#function1(){} + 50"
        )
        val commonAddresses = listOf<Long>(32) // The only remaining address after init filtering
        
        val throwable = createMockThrowable(addresses, stackTrace, "SmallException")
        val filtered = throwable.getFilteredStackTraceAddresses(
            keepLastInit = false,
            commonAddresses = commonAddresses
        )
        
        // Should result in empty list after all filtering
        assertEquals(emptyList(), filtered)
    }

    @Test
    fun testCompleteFilteringPipeline_LargeStackTrace() {
        // Test with a large stack trace to ensure performance and correctness
        val addresses = (1000L..1099L).toList() // 100 addresses
        val stackTrace = arrayOf(
            "kfun:kotlin.Throwable#<init>(kotlin.String?){} + 24",
            "kfun:LargeStackException#<init>(kotlin.String?){} + 10"
        ) + (2..99).map { "kfun:my.app.class#function$it(){} + ${it * 10}" }.toTypedArray()
        
        val commonAddresses = (1080L..1099L).toList() // Last 20 addresses
        
        val throwable = createMockThrowable(addresses, stackTrace, "LargeStackException")
        val filtered = throwable.getFilteredStackTraceAddresses(
            keepLastInit = false,
            commonAddresses = commonAddresses
        )
        
        // Should have 78 addresses remaining (100 - 2 init - 20 common)
        assertEquals(78, filtered.size)
        assertEquals(1002L, filtered.first()) // First non-init address
        assertEquals(1079L, filtered.last())  // Last non-common address
    }

    @Test
    fun testCompleteFilteringPipeline_NoInitAddresses() {
        val addresses = listOf<Long>(40, 41, 42, 43, 44)
        val stackTrace = arrayOf(
            "kfun:my.app.class#function1(){} + 50",
            "kfun:my.app.class#function2(){} + 60", 
            "kfun:my.app.class#function3(){} + 70",
            "kfun:my.app.class#function4(){} + 80",
            "kfun:my.app.class#function5(){} + 90"
        )
        val commonAddresses = listOf<Long>(43, 44) // Last two
        
        val throwable = createMockThrowable(addresses, stackTrace, "NoInitException")
        val filtered = throwable.getFilteredStackTraceAddresses(
            keepLastInit = false,
            commonAddresses = commonAddresses
        )
        
        // Should only drop common addresses, no init addresses to drop
        assertEquals(listOf<Long>(40, 41, 42), filtered)
    }

    @Test
    fun testCompleteFilteringPipeline_NoCommonAddresses() {
        val addresses = listOf<Long>(50, 51, 52, 53, 54)
        val stackTrace = arrayOf(
            "kfun:kotlin.Throwable#<init>(kotlin.String?){} + 24",
            "kfun:NoCommonException#<init>(kotlin.String?){} + 10",
            "kfun:my.app.class#function1(){} + 50",
            "kfun:my.app.class#function2(){} + 60",
            "kfun:my.app.class#function3(){} + 70"
        )
        val commonAddresses = emptyList<Long>() // No common addresses
        
        val throwable = createMockThrowable(addresses, stackTrace, "NoCommonException")
        val filtered = throwable.getFilteredStackTraceAddresses(
            keepLastInit = false,
            commonAddresses = commonAddresses
        )
        
        // Should only drop init addresses, no common addresses to drop
        assertEquals(listOf<Long>(52, 53, 54), filtered)
    }

    @Test
    fun testCompleteFilteringPipeline_EdgeCaseSequentialIndices() {
        // Test the specific edge case where sequential processing could cause issues
        val addresses = listOf<Long>(60, 61, 62, 63, 64, 65)
        val stackTrace = arrayOf(
            "kfun:EdgeCaseException#<init>(kotlin.String?){} + 10",
            "kfun:my.app.class#function1(){} + 50",
            "kfun:my.app.class#function2(){} + 60",
            "kfun:my.app.class#function3(){} + 70",
            "kfun:my.app.class#function4(){} + 80",
            "kfun:my.app.class#function5(){} + 90"
        )
        val commonAddresses = listOf<Long>(64, 65) // Sequential at the end
        
        val throwable = createMockThrowable(addresses, stackTrace, "EdgeCaseException")
        val filtered = throwable.getFilteredStackTraceAddresses(
            keepLastInit = false,
            commonAddresses = commonAddresses
        )
        
        assertEquals(listOf<Long>(61, 62, 63), filtered)
    }
}