package io.sentry.kotlin.multiplatform

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Event(val id: String)

expect abstract class BaseSentryE2ETest() {
    val platform: String
}

class SentryE2ETest : BaseSentryE2ETest() {
    private val realDsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
    private val client = HttpClient()
    private val jsonDecoder = Json { ignoreUnknownKeys = true }

    private fun authToken(): String {
        return "" // TODO: get auth token from env var
    }

    @BeforeTest
    fun init() {
        Sentry.init {
            it.dsn = realDsn
            it.beforeSend = { event ->
                event
            }
        }
    }

    private suspend fun fetchEvent(eventId: String): String {
        val url =
            "https://sentry.io/api/0/projects/sentry-sdks/sentry-kotlin-multiplatform/events/$eventId/"
        val response = client.get(url) {
            headers {
                append(
                    HttpHeaders.Authorization,
                    "Bearer ${authToken()}"
                )
            }
        }
        return response.bodyAsText()
    }

    private suspend fun waitForEventRetrieval(eventId: String): Event {
        val json = fetchEvent(eventId)
        return jsonDecoder.decodeFromString(json)
    }

    @Test
    fun `capture message and persist it in the sentry project`() = runTest(timeout = 30.seconds) {
        if (platform != "Apple") {
            val eventId = Sentry.captureMessage("Test running on $platform")
            val result = withContext(Dispatchers.Default) {
                delay(20000)
                waitForEventRetrieval(eventId.toString())
            }
            assertEquals(result.id, eventId.toString())
        }
    }

    @Test
    fun `capture exception and persist it in the sentry project`() = runTest(timeout = 30.seconds) {
        if (platform != "Apple") {
            val eventId =
                Sentry.captureException(IllegalArgumentException("Test exception on platform $platform"))
            val result = withContext(Dispatchers.Default) {
                delay(20000)
                waitForEventRetrieval(eventId.toString())
            }
            assertEquals(result.id, eventId.toString())
        }
    }

    @AfterTest
    fun tearDown() {
        Sentry.close()
    }
}
