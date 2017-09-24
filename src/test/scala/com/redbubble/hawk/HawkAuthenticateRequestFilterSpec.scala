package com.redbubble.hawk

import com.redbubble.hawk.HawkAuthenticate.hawkHeader
import com.redbubble.hawk.params._
import com.redbubble.hawk.spec.SpecHelper
import com.redbubble.hawk.validate.{Credentials, Sha256}
import com.redbubble.util.http.ResponseOps.textResponse
import com.redbubble.util.metrics.StatsReceiver
import com.twitter.finagle.Service
import com.twitter.finagle.http.Status.Ok
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.io.Buf
import com.twitter.util.{Await, Future}
import org.specs2.mutable.Specification

final class HawkAuthenticateRequestFilterSpec extends Specification with SpecHelper {
  private val credentials = Credentials(KeyId("key-id"), Key("secret"), Sha256)

  private object HawkAuthFilter extends HawkAuthenticateRequestFilter(credentials, Seq.empty)(StatsReceiver.stats)

  private object TestService extends Service[Request, Response] {
    override def apply(request: Request) = Future.value(textResponse(Ok, Buf.Utf8("OK")))
  }

  "HawkAuthenticateRequestFilter" >> {
    val hawkAuthenticatedService = HawkAuthFilter andThen TestService

//    "with an invalid Hawk authorisation header" >> {
//      val timeStamp = Time.nowUtc.millis
//      val nonce = Nonce.generateNonce
//      val invalidHawkHeader = s"""Hawk id="dh37fgj492je", ts="$timeStamp", nonce="$nonce", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
//      val req = requestWithAuthHeader(invalidHawkHeader)
//
//      "does not execute the service" >> {
//        val result = Await.result(hawkAuthenticatedService(req))
//        result.status should be(Unauthorized)
//      }
//    }

    "with a valid Hawk authorisation header" >> {
      val context = RequestContext(Get, Host("example.com"), Port(80), UriPath(baseRequest.path), None)
      val validHawkHeader = hawkHeader(credentials, context).httpHeaderForm
      println(s"validHawkHeader ${validHawkHeader}")
      val req = requestWithAuthHeader(validHawkHeader)

      "executes the service" >> {
        val result = Await.result(hawkAuthenticatedService(req))
        result.status should be(Ok)
      }
    }
  }

  private def baseRequest: Request = Request(Method.Get, "/does/not/matter")

  private def requestWithAuthHeader(hawkHeader: String) = {
    val request = baseRequest
    request.headerMap.add(AuthorisationHttpHeader, hawkHeader)
    request
  }
}
