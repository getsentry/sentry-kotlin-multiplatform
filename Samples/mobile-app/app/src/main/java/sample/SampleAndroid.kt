package sample

import android.app.Application
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.sentry.ContextProvider

actual class Sample {
    actual fun checkMe() = 44
}

actual object Platform {
    actual val name: String = "Android"
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Sample().checkMe()
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.main_text).text = Proxy().proxyHello()
    }
}

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ContextProvider.init { this }
    }
}