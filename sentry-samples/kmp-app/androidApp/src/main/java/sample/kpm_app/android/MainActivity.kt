package sample.kpm_app.android

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import sample.kpm_app.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val captureMessageBtn: Button = findViewById(R.id.captureMessageBtn)
        val captureExceptionBtn: Button = findViewById(R.id.captureExceptionBtn)
        val captureHardCrashBtn: Button = findViewById(R.id.captureHardCrash)

        captureMessageBtn.setOnClickListener {
            Sentry.captureMessage("From KMP Sample App: " + Platform().platform)
        }

        captureExceptionBtn.setOnClickListener {
            LoginImpl.login("MyUsername")
        }

        captureHardCrashBtn.setOnClickListener {
            LoginImpl.login()
        }
    }
}

class SentryApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Sentry using shared code
        initializeSentry()

        // Shared scope across all platforms
        configureSentryScope()

        // Add platform specific scope in addition to the shared scope
        Sentry.configureScope {
            it.setContext("Android Context", mapOf("context1" to 12, "context2" to false))
            it.addBreadcrumb(Breadcrumb.debug("initialized Sentry on Android"))
        }
    }
}
