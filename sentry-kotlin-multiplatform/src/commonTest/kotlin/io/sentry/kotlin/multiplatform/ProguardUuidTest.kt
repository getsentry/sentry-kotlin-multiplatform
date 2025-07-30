package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProguardUuidTest : BaseSentryTest() {
    
    @Test
    fun `GIVEN SentryOptions WHEN proguardUuid is not set THEN returns null`() {
        val options = SentryOptions()
        
        assertNull(options.proguardUuid)
    }
    
    @Test
    fun `GIVEN SentryOptions WHEN proguardUuid is set THEN returns correct value`() {
        val options = SentryOptions()
        val testUuid = "test-proguard-uuid-1234-5678-abcd-efgh"
        
        options.proguardUuid = testUuid
        
        assertEquals(testUuid, options.proguardUuid)
    }
    
    @Test
    fun `GIVEN SentryOptions WHEN proguardUuid is set to null THEN returns null`() {
        val options = SentryOptions()
        val testUuid = "test-proguard-uuid-1234-5678-abcd-efgh"
        
        // First set a value
        options.proguardUuid = testUuid
        assertEquals(testUuid, options.proguardUuid)
        
        // Then set to null
        options.proguardUuid = null
        assertNull(options.proguardUuid)
    }
    
    @Test
    fun `GIVEN SentryOptions WHEN proguardUuid is set multiple times THEN returns latest value`() {
        val options = SentryOptions()
        val firstUuid = "first-proguard-uuid-1111"
        val secondUuid = "second-proguard-uuid-2222"
        
        options.proguardUuid = firstUuid
        assertEquals(firstUuid, options.proguardUuid)
        
        options.proguardUuid = secondUuid
        assertEquals(secondUuid, options.proguardUuid)
    }
    
    @Test
    fun `GIVEN SentryOptions WHEN proguardUuid is empty string THEN returns empty string`() {
        val options = SentryOptions()
        
        options.proguardUuid = ""
        
        assertEquals("", options.proguardUuid)
    }
    
    @Test
    fun `GIVEN realistic UUID format WHEN set as proguardUuid THEN stores correctly`() {
        val options = SentryOptions()
        val realisticUuid = "550e8400-e29b-41d4-a716-446655440000"
        
        options.proguardUuid = realisticUuid
        
        assertEquals(realisticUuid, options.proguardUuid)
    }
}