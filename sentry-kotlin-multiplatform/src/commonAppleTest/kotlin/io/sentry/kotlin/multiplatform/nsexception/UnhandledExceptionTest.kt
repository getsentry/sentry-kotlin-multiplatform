package io.sentry.kotlin.multiplatform.nsexception

import NSException.Sentry.SentrySDK
import NSException.Sentry.storeEnvelope
import kotlin.test.Test

class UnhandledExceptionTest {

    @Test
    fun `test creating envelopes don't crash`() {
        val throwable = Throwable("test")
        val envelope = throwable.asSentryEnvelope()
        SentrySDK.storeEnvelope(envelope)
        SentrySDK.configureScope { scope ->
            scope?.setTagValue(kotlinCrashedTag, kotlinCrashedTag)
        }
    }
}
