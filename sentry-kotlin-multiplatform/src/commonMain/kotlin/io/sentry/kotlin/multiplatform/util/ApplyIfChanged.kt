package io.sentry.kotlin.multiplatform.util

internal inline fun <T> applyIfChanged(old: T, new: T, applyNew: (T) -> Unit) {
    if (old != new) applyNew(new)
}
