package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.updateFromKmpEventChanges
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import kotlinx.cinterop.convert
import kotlin.test.Test
import kotlin.test.assertEquals

expect class UpdateFromKmpEventChangesTest {
    fun `native value is untouched when before and after values are the same`()
    fun `native value is updated when before and after values are different`()
}
