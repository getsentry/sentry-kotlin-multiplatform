package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toByteArray
import io.sentry.kotlin.multiplatform.extensions.toNSData
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class FoundationTest {

    @Test
    fun `convert string to NSData and ByteArray is correct`() {
        val text = "$!()I!(DKDASKDKSD(#(ldkiadjk91jd"
        val nsString = "$!()I!(DKDASKDKSD(#(ldkiadjk91jd" as NSString

        val nsData = nsString.dataUsingEncoding(NSUTF8StringEncoding)!!
        val byteArray = text.encodeToByteArray()

        assertContentEquals(byteArray, nsData.toByteArray())
        assertContentEquals(byteArray, byteArray.toNSData().toByteArray())
        assertEquals(nsData, byteArray.toNSData())
        assertEquals(nsData, nsData.toByteArray().toNSData())
    }

    @Test
    fun `NSNumber longLong converts to Long correctly`() {
        val longValue = 4937446359977427944L
        val nsNumber = NSNumber(longLong = longValue)
        assertEquals(longValue, nsNumber.longLongValue)
    }

    @Test
    fun `NSNumber int converts to Long correctly`() {
        val intValue = 493744635
        val nsNumber = NSNumber(int = intValue)
        assertEquals(intValue.toLong(), nsNumber.longLongValue)
    }

    @Test
    fun `NSNumber short converts to Long correctly`() {
        val shortValue: Short = 4937
        val nsNumber = NSNumber(short = shortValue)
        assertEquals(shortValue.toLong(), nsNumber.longLongValue)
    }
}
