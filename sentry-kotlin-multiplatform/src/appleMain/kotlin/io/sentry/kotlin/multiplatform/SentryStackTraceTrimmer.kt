package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryFrame
import cocoapods.Sentry.SentryStacktrace
import cocoapods.Sentry.SentryThread
import platform.Foundation.NSThread

internal object SentryStackTraceTrimmer {
    private const val SENTRY_FRAME_PACKAGE = "io.sentry.kotlin.multiplatform"

    private fun getCallStackSymbols(): List<String> {
        return NSThread.callStackSymbols() as? List<String> ?: emptyList()
    }

    private fun findLastSentryFrameIndex(callStackSymbols: List<String>): Int {
        return callStackSymbols.indexOfLast { it.contains(SENTRY_FRAME_PACKAGE) }
    }

    private fun extractInstructionAddress(sentryFrame: String): String? {
        return sentryFrame.replace(Regex("\\s+"), " ").split(" ")[2].takeIf { it.isNotEmpty() }
    }

    private fun trimStackTrace(stacktrace: SentryStacktrace, instructionAddress: String?) {
        val frames = stacktrace.frames as List<SentryFrame>
        val newFrames = frames.takeWhile { frame -> frame.instructionAddress!= instructionAddress }
        stacktrace.setFrames(newFrames)
    }

    fun removeSentryFrames(stacktrace: SentryStacktrace) {
        val callStackSymbols = getCallStackSymbols()
        val lastSentryFrameIndex = findLastSentryFrameIndex(callStackSymbols)

        if (lastSentryFrameIndex!= -1) {
            val lastSentryFrame = callStackSymbols[lastSentryFrameIndex]
            val instructionAddress = extractInstructionAddress(lastSentryFrame)
            instructionAddress?.let { address -> trimStackTrace(stacktrace, address) }
        }
    }
}
