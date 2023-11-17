package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource
import kotlinx.cinterop.UnsafeNumber
import platform.darwin.NSInteger

/*
Definition of TransactionNameSource in the Cocoa SDK

typedef NS_ENUM(NSInteger, SentryTransactionNameSource) {
    kSentryTransactionNameSourceCustom = 0,
    kSentryTransactionNameSourceUrl,
    kSentryTransactionNameSourceRoute,
    kSentryTransactionNameSourceView,
    kSentryTransactionNameSourceComponent,
    kSentryTransactionNameSourceTask
};
 */

@OptIn(UnsafeNumber::class)
internal fun NSInteger.toKmpTransactionNameSource(): TransactionNameSource {
    val transactionNameSource = when (this.toInt()) {
        0 -> TransactionNameSource.CUSTOM
        1 -> TransactionNameSource.URL
        2 -> TransactionNameSource.ROUTE
        3 -> TransactionNameSource.VIEW
        4 -> TransactionNameSource.COMPONENT
        5 -> TransactionNameSource.TASK
        else -> TransactionNameSource.CUSTOM
    }
    return transactionNameSource
}