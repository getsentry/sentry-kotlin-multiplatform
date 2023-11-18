package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException
import io.sentry.kotlin.multiplatform.protocol.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Tests that verify if the beforeSend hook correctly modifies events */
class BeforeSendTest {

  @Test
  fun `beforeSend drops event`() {
    val options = SentryOptions()
    options.beforeSend = { null }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(null, event)
  }

  @Test
  fun `beforeSend modifies message`() {
    val expected = Message("test")

    val options = SentryOptions()
    options.beforeSend = {
      it.message = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.message)
  }

  @Test
  fun `beforeSend modifies logger`() {
    val expected = "test"

    val options = SentryOptions()
    options.beforeSend = {
      it.logger = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.logger)
  }

  @Test
  fun `beforeSend modifies level`() {
    val expected = SentryLevel.DEBUG

    val options = SentryOptions()
    options.beforeSend = {
      it.level = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.level)
  }

  @Test
  fun `beforeSend modifies fingerprint`() {
    val expected = mutableListOf("test")

    val options = SentryOptions()
    options.beforeSend = {
      it.fingerprint = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.fingerprint)
  }

  @Test
  fun `beforeSend modifies release`() {
    val expected = "test"

    val options = SentryOptions()
    options.beforeSend = {
      it.release = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.release)
  }

  @Test
  fun `beforeSend modifies environment`() {
    val expected = "test"

    val options = SentryOptions()
    options.beforeSend = {
      it.environment = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.environment)
  }

  @Test
  fun `beforeSend modifies platform`() {
    val expected = "test"

    val options = SentryOptions()
    options.beforeSend = {
      it.platform = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.platform)
  }

  @Test
  fun `beforeSend modifies user`() {
    val expected =
        User().apply {
          id = "test"
          username = "username"
          email = "email"
        }

    val options = SentryOptions()
    options.beforeSend = {
      it.user = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.user)
  }

  @Test
  fun `beforeSend modifies serverName`() {
    val expected = "test"

    val options = SentryOptions()
    options.beforeSend = {
      it.serverName = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.serverName)
  }

  @Test
  fun `beforeSend modifies dist`() {
    val expected = "test"

    val options = SentryOptions()
    options.beforeSend = {
      it.dist = expected
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertEquals(expected, event?.dist)
  }

  @Test
  fun `beforeSend modifies breadcrumbs`() {
    val expected = Breadcrumb.debug("test breadcrumb")

    val options = SentryOptions()
    options.beforeSend = {
      it.addBreadcrumb(expected)
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertTrue(event?.breadcrumbs?.contains(expected) ?: false)
  }

  @Test
  fun `beforeSend modifies tags`() {
    val expectedKey = "key"
    val expectedValue = "value"

    val options = SentryOptions()
    options.beforeSend = {
      it.setTag(expectedKey, expectedValue)
      it
    }

    val event = options.beforeSend?.invoke(SentryEvent())

    assertTrue(event?.tags?.containsKey(expectedKey) ?: false)
    assertEquals(event?.tags?.get(expectedKey), expectedValue)
  }

  @Test
  fun `beforeSend receives contexts`() {
    var contexts: Map<String, Any>? = mapOf()
    val options = SentryOptions()
    options.beforeSend = {
      contexts = it.contexts
      it
    }

    val event =
        options.beforeSend?.invoke(SentryEvent().apply { contexts = mapOf("test" to "test") })

    assertEquals(contexts, event?.contexts)
  }

  @Test
  fun `beforeSend modifies exceptions`() {
    var exceptions: List<SentryException>? = listOf()
    val options = SentryOptions()
    options.beforeSend = {
      exceptions = it.exceptions
      it
    }

    val event =
        options.beforeSend?.invoke(
            SentryEvent().apply { exceptions = listOf(SentryException("test")) })

    assertEquals(exceptions, event?.exceptions)
  }
}
