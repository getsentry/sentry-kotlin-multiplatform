package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentrySampleDecision

internal fun SentrySampleDecision.toBoolean(): Boolean = when (this) {
    SentrySampleDecision.kSentrySampleDecisionNo -> false
    SentrySampleDecision.kSentrySampleDecisionUndecided -> false
    SentrySampleDecision.kSentrySampleDecisionYes -> true
    else -> {
        false
    }
}