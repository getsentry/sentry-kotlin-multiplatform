package io.sentry.kotlin.multiplatform

expect abstract class BaseSentryScopeTest() {

  fun initializeScope(): Scope
}
