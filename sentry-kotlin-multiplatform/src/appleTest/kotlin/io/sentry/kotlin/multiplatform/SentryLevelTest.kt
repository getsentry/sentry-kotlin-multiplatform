package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.*
import kotlinx.cinterop.convert
import kotlin.test.Test
import kotlin.test.assertEquals

class CocoaSentryLevelTest {

    @Test
    fun `convert SentryLevel debug to AndroidSentryLevel debug has proper value`() {
        val level = SentryLevel.DEBUG
        val cocoaSentryLevelDebug = level.toCocoaSentryLevel()

        assertEquals((SentryLevelNumConstants.DEBUG_LEVEL).convert(), cocoaSentryLevelDebug)
    }

    @Test
    fun `convert SentryLevel info to AndroidSentryLevel info has proper value`() {
        val level = SentryLevel.INFO
        val cocoaSentryLevelInfo = level.toCocoaSentryLevel()

        assertEquals((SentryLevelNumConstants.INFO_LEVEL).convert(), cocoaSentryLevelInfo)
    }

    @Test
    fun `convert SentryLevel warning to AndroidSentryLevel warning has proper value`() {
        val level = SentryLevel.WARNING
        val cocoaSentryLevelWarning = level.toCocoaSentryLevel()

        assertEquals((SentryLevelNumConstants.WARNING_LEVEL).convert(), cocoaSentryLevelWarning)
    }

    @Test
    fun `convert SentryLevel error to AndroidSentryLevel error has proper value`() {
        val level = SentryLevel.ERROR
        val cocoaSentryLevelError = level.toCocoaSentryLevel()

        assertEquals((SentryLevelNumConstants.ERROR_LEVEL).convert(), cocoaSentryLevelError)
    }

    @Test
    fun `convert SentryLevel fatal to AndroidSentryLevel fatal has proper value`() {
        val level = SentryLevel.FATAL
        val cocoaSentryLevelFatal = level.toCocoaSentryLevel()

        assertEquals((SentryLevelNumConstants.FATAL_LEVEL).convert(), cocoaSentryLevelFatal)
    }

    @Test
    fun `convert AndroidSentryLevel debug to SentryLevel debug has proper value`() {
        val level: CocoaSentryLevel = SentryLevelNumConstants.DEBUG_LEVEL.convert()
        val kmpSentryLevel = level.toKmpSentryLevel()

        assertEquals(SentryLevel.DEBUG, kmpSentryLevel)
    }

    @Test
    fun `convert AndroidSentryLevel info to SentryLevel fatal has proper value`() {
        val level: CocoaSentryLevel = SentryLevelNumConstants.INFO_LEVEL.convert()
        val kmpSentryLevel = level.toKmpSentryLevel()

        assertEquals(SentryLevel.INFO, kmpSentryLevel)
    }

    @Test
    fun `convert AndroidSentryLevel warning to SentryLevel has proper value`() {
        val level: CocoaSentryLevel = SentryLevelNumConstants.WARNING_LEVEL.convert()
        val kmpSentryLevel = level.toKmpSentryLevel()

        assertEquals(SentryLevel.WARNING, kmpSentryLevel)
    }

    @Test
    fun `convert AndroidSentryLevel error to SentryLevel fatal has proper value`() {
        val level: CocoaSentryLevel = SentryLevelNumConstants.ERROR_LEVEL.convert()
        val kmpSentryLevel = level.toKmpSentryLevel()

        assertEquals(SentryLevel.ERROR, kmpSentryLevel)
    }

    @Test
    fun `convert AndroidSentryLevel fatal to SentryLevel fatal has proper value`() {
        val levelFatal: CocoaSentryLevel = SentryLevelNumConstants.FATAL_LEVEL.convert()
        val kmpSentryLevel = levelFatal.toKmpSentryLevel()

        assertEquals(SentryLevel.FATAL, kmpSentryLevel)
    }

    @Test
    fun `converting null SentryLevel to CocoaSentryLevel returns null`() {
        val level: SentryLevel? = null
        val actual = level?.toCocoaSentryLevel()

        assertEquals(null, actual)
    }

    @Test
    fun `converting null CocoaSentryLevel to SentryLevel returns null`() {
        val level: CocoaSentryLevel? = null
        val actual = level?.toKmpSentryLevel()

        assertEquals(null, actual)
    }
}
