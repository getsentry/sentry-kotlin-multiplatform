package sample.kmp.app.android

import android.app.Application
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.sentry.kotlin.multiplatform.Attachment
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import sample.kmp.app.LoginImpl
import sample.kmp.app.Platform
import sample.kmp.app.configureSentryScope
import sample.kmp.app.initializeSentry
import java.io.FileOutputStream
import java.io.IOException

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
        initializeSentry(this)

        // Shared scope across all platforms
        configureSentryScope()

        val imageFile = applicationContext.getFileStreamPath("sentry.png")
        try {
            applicationContext.resources.openRawResource(R.raw.sentry).use { inputStream ->
                FileOutputStream(imageFile).use { outputStream ->
                    val bytes = ByteArray(1024)
                    while (inputStream.read(bytes) !== -1) {
                        // To keep the sample code simple this happens on the main thread. Don't do this in a
                        // real app.
                        outputStream.write(bytes)
                    }
                    outputStream.flush()
                }
            }
        } catch (e: IOException) {
            Sentry.captureException(e)
        }

        val imageAttachment = Attachment(imageFile.getAbsolutePath(), "sentry.png", "image/png")

        // Add platform specific scope in addition to the shared scope
        Sentry.configureScope {
            it.setContext("Android Context", mapOf("context1" to 12, "context2" to false))
            it.addBreadcrumb(Breadcrumb.debug("initialized Sentry on Android"))
            it.addAttachment(imageAttachment)
        }
    }
}
