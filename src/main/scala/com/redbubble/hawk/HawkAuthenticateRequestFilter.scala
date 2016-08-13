package com.redbubble.hawk

import cats.data.Xor
import cats.data.Xor._
import com.redbubble.hawk.HawkAuthenticate.authenticateRequest
import com.redbubble.hawk.RequestContextBuilder.buildContext
import com.redbubble.hawk.validate.Credentials
import com.redbubble.util.http.{AuthenticationFailedError, ApiError}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future

abstract class HawkAuthenticateRequestFilter(credentials: Credentials) extends SimpleFilter[Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] =
    authenticate(request).fold(e => Future.exception(e), _ => service(request))

  private def authenticate(request: Request): Xor[ApiError, RequestValid] = {
    val valid = buildContext(request).map { context =>
      authenticateRequest(credentials, context)
    }.getOrElse(errorXor(s"Missing authentication header '$AuthorisationHttpHeader'"))
    valid.leftMap(e => AuthenticationFailedError("Request is not authorised", Some(e)))
  }

  def notAuthorised[T](message: String): Xor[ApiError, T] = left(AuthenticationFailedError(message))
}
