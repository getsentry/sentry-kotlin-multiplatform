package io.sentry.kotlin.multiplatform

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.sentry.kotlin.multiplatform.utils.realDsn
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
private data class Event(val id: String)

class SentryE2ETest : BaseSentryTest() {
    private val client = HttpClient()
    private val jsonDecoder = Json { ignoreUnknownKeys = true }
    private val authToken = "" // TODO: Add auth token to github env

    @BeforeTest
    fun setup() {
        sentryInit { options ->
            options.dsn = realDsn
            options.beforeSend = { event ->
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
                    "Bearer $authToken"
                )
            }
        }
        return response.bodyAsText()
    }

    private suspend fun waitForEventRetrieval(eventId: String): Event {
        var json = ""
        val result: Event = withContext(Dispatchers.Default) {
            while (json.isEmpty() || json.contains("Event not found")) {
                delay(5000)
                json = fetchEvent(eventId)
            }
            jsonDecoder.decodeFromString(json)
        }
        return result
    }

    @Test
    fun `capture message and persist it in the sentry project`() = runTest(timeout = 30.seconds) {
        if (platform != "Apple") {
            val eventId = Sentry.captureMessage("Test running on $platform")
            val fetchedEvent = waitForEventRetrieval(eventId.toString())
            assertEquals(eventId.toString(), fetchedEvent.id)
        }
    }

    @Test
    fun `capture exception and persist it in the sentry project`() = runTest(timeout = 30.seconds) {
        if (platform != "Apple") {
            val eventId =
                Sentry.captureException(IllegalArgumentException("Test exception on platform $platform"))
            val fetchedEvent = waitForEventRetrieval(eventId.toString())
            assertEquals(eventId.toString(), fetchedEvent.id)
        }
    }

    @AfterTest
    fun tearDown() {
        Sentry.close()
    }
}
