package io.sentry.kotlin.multiplatform

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.sentry.kotlin.multiplatform.utils.org
import io.sentry.kotlin.multiplatform.utils.projectSlug
import io.sentry.kotlin.multiplatform.utils.realDsn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@Serializable
private data class SentryEventSerializable(
    val id: String? = null,
    val project: Long? = null,
    val release: String? = null,
    val platform: String? = null,
    val message: String? = "",
    val tags: List<Map<String, String>> = listOf(),
    val fingerprint: List<String> = listOf(),
    val level: String? = null,
    val logger: String? = null,
    val title: String? = null
)

class SentryE2ETest : BaseSentryTest() {
    private val client = HttpClient()
    private val jsonDecoder = Json { ignoreUnknownKeys = true }
    private var sentEvent: SentryEvent? = null

    @BeforeTest
    fun setup() {
        assertNotNull(authToken)
        assertTrue(authToken!!.isNotEmpty())
        sentryInit { options ->
            options.dsn = realDsn
            options.beforeSend = { event ->
                sentEvent = event
                event
            }
        }
    }

    private suspend fun fetchEvent(eventId: String): String {
        val url =
            "https://sentry.io/api/0/projects/$org/$projectSlug/events/$eventId/"
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

    private suspend fun waitForEventRetrieval(eventId: String): SentryEventSerializable {
        var json = ""
        val result: SentryEventSerializable = withContext(Dispatchers.Default) {
            while (json.isEmpty() || json.contains("Event not found")) {
                delay(20000)
                json = fetchEvent(eventId)
                assertFalse(json.contains("Invalid token"), "Invalid auth token")
            }
            jsonDecoder.decodeFromString(json)
        }
        return result
    }

    // TODO: e2e tests are currently disabled for Apple targets as there are SSL issues that prevent sending events in tests
    // See: https://github.com/getsentry/sentry-kotlin-multiplatform/issues/17

    @Test
    fun `capture message and fetch event from Sentry`() = runTest(timeout = 60.seconds) {
        if (platform != "Apple") {
            val message = "Test running on $platform"
            val eventId = Sentry.captureMessage(message)
            val fetchedEvent = waitForEventRetrieval(eventId.toString())
            fetchedEvent.tags.forEach { println(it["value"]) }
            assertEquals(eventId.toString(), fetchedEvent.id)
            assertEquals(sentEvent?.message?.formatted, fetchedEvent.message)
            assertEquals(message, fetchedEvent.title)
            assertEquals(sentEvent?.release, fetchedEvent.release)
            assertEquals(2, fetchedEvent.tags.find { it["value"] == sentEvent?.environment }?.size)
            assertEquals(sentEvent?.fingerprint?.toList(), fetchedEvent.fingerprint)
            assertEquals(2, fetchedEvent.tags.find { it["value"] == sentEvent?.level?.name?.lowercase() }?.size)
            assertEquals(sentEvent?.logger, fetchedEvent.logger)
        }
    }

    @Test
    fun `capture exception and fetch event from Sentry`() = runTest(timeout = 30.seconds) {
        if (platform != "Apple") {
            val exceptionMessage = "Test exception on platform $platform"
            val eventId =
                Sentry.captureException(IllegalArgumentException(exceptionMessage))
            val fetchedEvent = waitForEventRetrieval(eventId.toString())
            assertEquals(eventId.toString(), fetchedEvent.id)
            assertEquals("IllegalArgumentException: $exceptionMessage", fetchedEvent.title)
            assertEquals(sentEvent?.release, fetchedEvent.release)
            assertEquals(2, fetchedEvent.tags.find { it["value"] == sentEvent?.environment }?.size)
            assertEquals(sentEvent?.fingerprint?.toList(), fetchedEvent.fingerprint)
            assertEquals(2, fetchedEvent.tags.find { it["value"] == SentryLevel.ERROR.toString().lowercase() }?.size)
            assertEquals(sentEvent?.logger, fetchedEvent.logger)
        }
    }

    @AfterTest
    fun tearDown() {
        sentEvent = null
        Sentry.close()
    }
}
