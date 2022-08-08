import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.init
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import kotlinx.browser.document
import react.create
import react.dom.client.createRoot
import sample.kpm_app.configureSharedScope

fun main() {
    Sentry.init {
        it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
    }

    configureSharedScope()

    Sentry.configureScope {
        it.level = SentryLevel.INFO
        val map = mapOf("to" to mapOf("here" to 12))
        it.setContext("Custom", map)
        it.setContext("medium", arrayOf(1, 2, "22222dada2"))
        it.setContext("text", setOf("2", "2", "44"))
        val breadcrumb = Breadcrumb.debug("test message")
        breadcrumb.setLevel(SentryLevel.FATAL)
        it.addBreadcrumb(breadcrumb)
    }

    val container = document.createElement("div")
    document.body!!.appendChild(container)

    val welcome = Welcome.create {
        name = "Kotlin/JS"
    }

    createRoot(container).render(welcome)
}
