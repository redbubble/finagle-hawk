package com.redbubble.hawk

import cats.syntax.either._
import com.redbubble.hawk.HawkAuthenticate.authenticateRequest
import com.redbubble.hawk.RequestContextBuilder.buildContext
import com.redbubble.hawk.validate.Credentials
import com.redbubble.hawk.validate.HawkTimeValidator.DefaultLeeway
import com.redbubble.util.http.ResponseOps.unauthorised
import com.redbubble.util.metrics.StatsReceiver
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import org.joda.time.Duration

/**
  * A Finagle filter that performs HAWK request authentication.
  *
  * @param credentials      The credentials to use to verify the request.
  * @param whitelistedPaths Paths to exclude from authentication.
  * @param leeway           An acceptible leeway to apply when validating timestamps.
  * @param statsReceiver    Where to log metrics.
  */
abstract class HawkAuthenticateRequestFilter(
    credentials: Credentials,
    whitelistedPaths: Seq[String],
    leeway: Duration = DefaultLeeway)
    (implicit statsReceiver: StatsReceiver) extends SimpleFilter[Request, Response] {

  private val stats = statsReceiver.scope("hawk_auth")
  private val failureCounter = stats.counter("failure")
  private val successCounter = stats.counter("success")

  final override def apply(request: Request, continue: Service[Request, Response]): Future[Response] =
    if (whitelistedPaths.exists(p => request.path.startsWith(p))) {
      continue(request)
    } else {
      authenticate(request).fold(
        e => {
          failureCounter.incr()
          unauthorised(e.getMessage)
        },
        _ => {
          successCounter.incr()
          continue(request)
        }
      )
    }

  private def authenticate(request: Request): Either[HawkError, RequestValid] = {
    val authenticationResult = buildContext(request).map { context =>
      authenticateRequest(credentials, context, leeway)
    }.getOrElse(errorE(s"Missing authentication header '$AuthorisationHttpHeader'"))
    authenticationResult.leftMap(e => HawkAuthenticationFailedError("Request is not authorised", Some(e)))
  }
}
