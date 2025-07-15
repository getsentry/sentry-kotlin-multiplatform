package io.sentry.kotlin.multiplatform

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(): Scope = InMemoryScope()
}