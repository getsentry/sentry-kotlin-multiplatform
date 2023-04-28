package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySpanProtocol

public actual abstract class TransactionWrapper(private val cocoaTransaction: SentrySpanProtocol) :
    Transaction {
    override fun startChild(operation: String): Span {
        cocoaTransaction.startChildWithOperation(operation)
    }
}