package sample.kpm_app.android

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.extensions.init
import sample.kpm_app.SharedBusinessLogic
import sample.kpm_app.Platform
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val captureMessageBtn: Button = findViewById(R.id.captureMessageBtn)
        val captureExceptionBtn: Button = findViewById(R.id.captureExceptionBtn)
        val captureHardCrashBtn: Button = findViewById(R.id.captureHardCrash)

        captureMessageBtn.setOnClickListener {
            SharedBusinessLogic.captureMessage("From KMP Sample App: " + Platform().platform)
        }

        captureExceptionBtn.setOnClickListener {
            try {
                // will throw an outOfBounds exception
                val arr = arrayOf(1)
                arr[2]
            } catch (e: Exception) {
                SharedBusinessLogic.captureException(e)
            }
        }

        val map = HashMap<Any?, Any>()
        map.put("hello", 12)
        map.put("fighter", "Faaa")
        Sentry.configureScope {
            it.setLevel(SentryLevel.FATAL)
            it.setContext("mycontext", map)
        }

        captureHardCrashBtn.setOnClickListener {
            SharedBusinessLogic.hardCrash()
        }
    }
}

class SentryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Sentry.init(this) {
            it.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
            it.attachStackTrace = true
            it.attachThreads = true
        }
    }
}
