package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration
import kotlinx.cinterop.convert
import platform.Foundation.NSMakeRange
import platform.Foundation.NSRegularExpression
import platform.Foundation.numberOfMatchesInString
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FailedRequestTargetsOptionsTest {
    private fun cocoaFailedRequestTargets(vararg kmpTargets: String): List<*> {
        val nativeOptions = CocoaSentryOptions()
        SentryOptions()
            .apply { if (kmpTargets.isNotEmpty()) failedRequestTargets = kmpTargets.toList() }
            .toCocoaOptionsConfiguration()
            .invoke(nativeOptions)
        return nativeOptions.failedRequestTargets()
    }

    // Mirrors +[SentryTracePropagation isTargetMatch:withTargets:] in sentry-cocoa, which is not
    // exposed to Kotlin: an NSString target matches when the URL contains it, an NSRegularExpression
    // target when it matches anywhere in the URL.
    private fun List<*>.cocoaMatches(url: String): Boolean =
        any { target ->
            when (target) {
                is NSRegularExpression ->
                    target.numberOfMatchesInString(url, 0u, NSMakeRange(0u, url.length.convert())) > 0u
                is String -> url.contains(target)
                else -> false
            }
        }

    @Test
    fun `default targets match every URL`() {
        val targets = cocoaFailedRequestTargets()

        assertTrue(targets.cocoaMatches("https://example.com/api/items"))
        assertTrue(targets.cocoaMatches("http://localhost:8080/"))
    }

    @Test
    fun `plain string target matches URLs containing it`() {
        val targets = cocoaFailedRequestTargets("example.com")

        assertTrue(targets.cocoaMatches("https://api.example.com/items"))
        assertFalse(targets.cocoaMatches("https://other.org/items"))
    }

    @Test
    fun `regex target matches URLs`() {
        val targets = cocoaFailedRequestTargets("https://.*\\.example\\.com/.*")

        assertTrue(targets.cocoaMatches("https://api.example.com/items"))
        assertFalse(targets.cocoaMatches("https://example.org/items"))
    }

    @Test
    fun `regex target matches part of a URL`() {
        val targets = cocoaFailedRequestTargets("api\\.example\\.(com|org)")

        assertTrue(targets.cocoaMatches("https://api.example.org/items"))
        assertFalse(targets.cocoaMatches("https://api.example.net/items"))
    }

    @Test
    fun `invalid regex target still matches as a plain string`() {
        val targets = cocoaFailedRequestTargets("[")

        assertTrue(targets.cocoaMatches("https://example.com/[]"))
        assertFalse(targets.cocoaMatches("https://example.com/items"))
    }
}
