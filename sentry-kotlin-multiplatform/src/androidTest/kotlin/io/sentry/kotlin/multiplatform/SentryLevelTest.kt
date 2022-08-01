package io.sentry.kotlin.multiplatform

import org.junit.Test
import kotlin.test.assertEquals
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel

class AndroidSentryLevelTest {

    @Test
    fun `convert SentryLevel to AndroidSentryLevel has proper value`() {
        val levelDebug = SentryLevel.DEBUG
        val androidSentryLevelDebug = levelDebug.toAndroidSentryLevel()

        val levelInfo = SentryLevel.INFO
        val androidSentryLevelInfo = levelInfo.toAndroidSentryLevel()

        val levelWarning = SentryLevel.WARNING
        val androidSentryLevelWarning = levelWarning.toAndroidSentryLevel()

        val levelError = SentryLevel.ERROR
        val androidSentryLevelError = levelError.toAndroidSentryLevel()

        val levelFatal = SentryLevel.FATAL
        val androidSentryLevelFatal = levelFatal.toAndroidSentryLevel()

        assertEquals(AndroidSentryLevel.DEBUG, androidSentryLevelDebug)
        assertEquals(AndroidSentryLevel.INFO, androidSentryLevelInfo)
        assertEquals(AndroidSentryLevel.WARNING, androidSentryLevelWarning)
        assertEquals(AndroidSentryLevel.ERROR, androidSentryLevelError)
        assertEquals(AndroidSentryLevel.FATAL, androidSentryLevelFatal)
    }
}
