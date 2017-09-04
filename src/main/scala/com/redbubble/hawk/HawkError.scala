package com.redbubble.hawk

abstract class HawkError extends Exception

abstract class HawkError_(message: String, cause: Option[Throwable]) extends HawkError {
  override def getMessage: String = message

  override def getCause: Throwable = cause.orNull
}

final case class HawkAuthenticationFailedError(message: String, cause: Option[Throwable] = None)
    extends HawkError_(message, cause)
