# Strict Trace Continuation

Configure native trace continuation through shared Kotlin options:

```kotlin
Sentry.init { options ->
    options.dsn = "https://public@o123.ingest.sentry.io/1"
    options.strictTraceContinuation = true
    // Optional override for self-hosted or Relay setups:
    options.orgId = "123"
}
```

With `strictTraceContinuation = true`, the SDK continues an incoming trace only when its effective organization ID and the organization ID in incoming baggage are both present and match. Otherwise, it starts a new trace.

The default is `false`: missing organization IDs are tolerated, but two present, different IDs still start a new trace. `orgId` defaults to `null`, allowing the native SDK to derive the organization ID from the DSN. An explicit ID overrides that derived value.

These options are forwarded to Cocoa on supported Apple targets and to the Java SDK on Android and JVM. They configure native continuation entry points and integrations; they do not add an incoming-trace or transaction API to common Kotlin code. The legacy watchosArm32 target remains a no-op.
