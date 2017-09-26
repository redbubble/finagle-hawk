package com.redbubble.hawk

import java.net.URI

import com.redbubble.hawk.params.Port
import com.redbubble.hawk.validate.{Credentials, Sha256}
import com.redbubble.util.http.ResponseOps.textResponse
import com.redbubble.util.metrics.StatsReceiver
import com.twitter.finagle.Service
import com.twitter.finagle.http.Status.Ok
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.io.Buf
import com.twitter.util.Future

trait RequestSpecOps {
  final val credentials = Credentials(KeyId("key-id"), Key("secret"), Sha256)

  final object HawkAuthFilter extends HawkAuthenticateRequestFilter(credentials, Seq.empty)(StatsReceiver.stats)

  final object TestService extends Service[Request, Response] {
    override def apply(request: Request) = Future.value(textResponse(Ok, Buf.Utf8("OK")))
  }

  // Note. We explicitly set the host header as most clients will send this.
  final def baseRequest(uri: URI, port: Port = Port(443)): Request = {
    val r = Request(Method.Get, uri.toString)
    r.host = s"${uri.getHost}:${port.toString}"
    r
  }

  final def requestWithAuthHeader(uri: URI, hawkHeader: String) = {
    val request = baseRequest(uri)
    request.headerMap.set(AuthorisationHttpHeader, hawkHeader)
    request
  }
}
