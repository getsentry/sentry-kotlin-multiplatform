package io.sentry.kotlin.multiplatform.converters

import cocoapods.Sentry.SentrySampleDecision

internal fun SentrySampleDecision.toBoolean(): Boolean? = when (this) {
    SentrySampleDecision.kSentrySampleDecisionUndecided -> false
    SentrySampleDecision.kSentrySampleDecisionYes -> true
    else -> {
        null
    }
}

internal fun Boolean?.toSampleDecision(): SentrySampleDecision = when (this) {
    null -> SentrySampleDecision.kSentrySampleDecisionUndecided
    false -> SentrySampleDecision.kSentrySampleDecisionNo
    true -> SentrySampleDecision.kSentrySampleDecisionYes
}
