package io.sentry.kotlin.multiplatform

import io.sentry.ITransaction

public actual abstract class TransactionWrapper(jvmTransaction: ITransaction) : Transaction {
}