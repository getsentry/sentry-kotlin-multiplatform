package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmTransactionNameSource
import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource

internal fun JvmTransactionNameSource.toKmp(): TransactionNameSource = when (this) {
    JvmTransactionNameSource.COMPONENT -> TransactionNameSource.COMPONENT
    JvmTransactionNameSource.ROUTE -> TransactionNameSource.ROUTE
    JvmTransactionNameSource.URL -> TransactionNameSource.URL
    JvmTransactionNameSource.VIEW -> TransactionNameSource.VIEW
    JvmTransactionNameSource.TASK -> TransactionNameSource.TASK
    JvmTransactionNameSource.CUSTOM -> TransactionNameSource.CUSTOM
}
