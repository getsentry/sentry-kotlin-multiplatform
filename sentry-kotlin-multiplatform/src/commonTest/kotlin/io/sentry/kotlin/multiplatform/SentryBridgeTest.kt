package io.sentry.kotlin.multiplatform

expect class SentryBridgeTest {
    fun `init sets correct configuration`()
    fun `setting null in beforeSend during init drops the event`()
    fun `default beforeSend in init does not drop the event`()
    fun `init sets the SDK packages`()
    fun `init sets SDK version and name`()
}
