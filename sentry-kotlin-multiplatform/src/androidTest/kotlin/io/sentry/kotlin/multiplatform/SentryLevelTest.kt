package io.sentry.kotlin.multiplatform

import org.junit.Test
import kotlin.test.assertEquals
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel

class AndroidSentryLevelTest {

    @Test
    fun `convert SentryLevel debug to AndroidSentryLevel debug has proper value`() {
        val level = SentryLevel.DEBUG
        val androidSentryLevelDebug = level.toAndroidSentryLevel()

        assertEquals(AndroidSentryLevel.DEBUG, androidSentryLevelDebug)
    }

    @Test
    fun `convert SentryLevel info to AndroidSentryLevel info has proper value`() {
        val level = SentryLevel.INFO
        val androidSentryLevelInfo = level.toAndroidSentryLevel()

        assertEquals(AndroidSentryLevel.INFO, androidSentryLevelInfo)
    }

    @Test
    fun `convert SentryLevel warning to AndroidSentryLevel warning has proper value`() {
        val level = SentryLevel.WARNING
        val androidSentryLevelWarning = level.toAndroidSentryLevel()

        assertEquals(AndroidSentryLevel.WARNING, androidSentryLevelWarning)
    }

    @Test
    fun `convert SentryLevel error to AndroidSentryLevel error has proper value`() {
        val level = SentryLevel.ERROR
        val androidSentryLevelError = level.toAndroidSentryLevel()

        assertEquals(AndroidSentryLevel.ERROR, androidSentryLevelError)
    }

    @Test
    fun `convert SentryLevel fatal to AndroidSentryLevel fatal has proper value`() {
        val level = SentryLevel.FATAL
        val androidSentryLevelFatal = level.toAndroidSentryLevel()

        assertEquals(AndroidSentryLevel.FATAL, androidSentryLevelFatal)
    }

    @Test
    fun `convert AndroidSentryLevel debug to SentryLevel debug has proper value`() {
        val level = AndroidSentryLevel.DEBUG
        val kmpSentryLevel = level.toKmpSentryLevel()

        assertEquals(SentryLevel.DEBUG, kmpSentryLevel)
    }

    @Test
    fun `convert AndroidSentryLevel info to SentryLevel info has proper value`() {
        val level = AndroidSentryLevel.INFO
        val kmpSentryLevel = level.toKmpSentryLevel()

        assertEquals(SentryLevel.INFO, kmpSentryLevel)
    }

    @Test
    fun `convert AndroidSentryLevel warning to SentryLevel warning has proper value`() {
        val level = AndroidSentryLevel.WARNING
        val kmpSentryLevel = level.toKmpSentryLevel()

        assertEquals(SentryLevel.WARNING, kmpSentryLevel)
    }

    @Test
    fun `convert AndroidSentryLevel error to SentryLevel error has proper value`() {
        val level = AndroidSentryLevel.ERROR
        val kmpSentryLevel = level.toKmpSentryLevel()

        assertEquals(SentryLevel.ERROR, kmpSentryLevel)
    }

    @Test
    fun `convert AndroidSentryLevel fatal to SentryLevel fatal has proper value`() {
        val level = AndroidSentryLevel.FATAL
        val kmpSentryLevel = level.toKmpSentryLevel()

        assertEquals(SentryLevel.FATAL, kmpSentryLevel)
    }

    @Test
    fun `converting null SentryLevel returns null`() {
        val level: SentryLevel? = null
        val actual = level?.toAndroidSentryLevel()

        assertEquals(null, actual)
    }
}
