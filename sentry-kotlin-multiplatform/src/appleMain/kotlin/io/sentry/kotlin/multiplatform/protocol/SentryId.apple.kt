package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.CocoaSentryId

@OptIn(ExperimentalForeignApi::class)
public actual class SentryId actual constructor(sentryIdString: String) {

  public actual companion object {
    public actual val EMPTY_ID: SentryId = SentryId("")
  }

  private var cocoaSentryId: CocoaSentryId? = null

  init {
    cocoaSentryId =
        if (sentryIdString.isEmpty()) {
          CocoaSentryId.empty()
        } else {
          CocoaSentryId(sentryIdString)
        }
  }

  actual override fun toString(): String {
    return cocoaSentryId.toString()
  }
}
