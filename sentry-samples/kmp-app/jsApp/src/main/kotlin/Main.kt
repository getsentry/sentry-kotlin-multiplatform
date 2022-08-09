import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.init
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User
import sample.kpm_app.LoginImpl
import sample.kpm_app.configureSharedScope
import sample.kpm_app.optionsConfiguration

@JsExport
fun captureMessage() {
    Sentry.captureMessage("From Kotlin Multiplatform Js Browser") {
        it.level = SentryLevel.FATAL
        it.setContext("moment", "ME")
        it.setTag("js-tag", "test test")
    }
}

@JsExport
fun captureException() {
    LoginImpl.login("MyUsername")
}

@JsExport
fun initApplication() {
    Sentry.init(optionsConfiguration())

    configureSharedScope()

    Sentry.configureScope {
        it.level = SentryLevel.INFO
        val map = mapOf("to" to mapOf("here" to 12))
        it.setContext("Custom", map)
        it.setContext("medium", arrayOf(1, 2, "22222dada2"))
        it.setContext("text", setOf("2", "2", "44"))
        val breadcrumb = Breadcrumb.debug("test message")
        breadcrumb.level = SentryLevel.FATAL
        it.addBreadcrumb(breadcrumb)
        val user = User().apply {
            username = "hallo"
            email = "hallo@email"
            id = "testId"
        }
        it.user = user
    }
}
