package io.sentry.kotlin.multiplatform.protocol

/** The source of the transaction name. */
public enum class TransactionNameSource {
  /**
   * User-defined name
   *
   * Examples:
   * * my_transaction
   */
  CUSTOM,

  /**
   * Raw URL, potentially containing identifiers.
   *
   * Examples:
   * * /auth/login/john123/
   * * GET /auth/login/john123/
   */
  URL,

  /**
   * Parametrized URL / route
   *
   * Examples:
   * * /auth/login/:userId/
   * * GET /auth/login/{user}/
   */
  ROUTE,

  /**
   * Name of the view handling the request.
   *
   * Examples:
   * * UserListView
   */
  VIEW,

  /**
   * Named after a software component, such as a function or class name.
   *
   * Examples:
   * * AuthLogin.login
   * * LoginActivity.login_button
   */
  COMPONENT,

  /**
   * Name of a background task
   *
   * Examples:
   * * sentry.tasks.do_something
   */
  TASK
}
