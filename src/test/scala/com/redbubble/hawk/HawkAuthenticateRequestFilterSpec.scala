package com.redbubble.hawk

import com.redbubble.hawk.spec.SpecHelper
import com.redbubble.hawk.util.Time
import com.redbubble.hawk.validate.{Credentials, Sha256}
import com.redbubble.util.http.ResponseOps.textResponse
import com.redbubble.util.io.BufOps.stringToBuf
import com.redbubble.util.metrics.StatsReceiver
import com.twitter.finagle.Service
import com.twitter.finagle.http.Method.Get
import com.twitter.finagle.http.Status.{Ok, Unauthorized}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Future}
import org.specs2.mutable.Specification

final class HawkAuthenticateRequestFilterSpec extends Specification with SpecHelper {
  private val credentials = Credentials(KeyId("key-id"), Key("secret"), Sha256)

  private object AuthFilter extends HawkAuthenticateRequestFilter(credentials, Seq.empty, StatsReceiver.stats)

  private object Service extends Service[Request, Response] {
    override def apply(request: Request) = Future.value(textResponse(Ok, stringToBuf("OK")))
  }

  "HawkAuthenticateRequestFilter" >> {
    val hawkAuthenticatedService = AuthFilter andThen Service

    "with invalid credentials" >> {
      val timeStamp = Time.nowUtc.millis
      val invalidHawkHeader = s"""Hawk id="dh37fgj492je", ts="$timeStamp", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""

      "does not execute the service" >> {
        val req = getRequest(invalidHawkHeader)
        val result = Await.result(hawkAuthenticatedService(req))
        result.status should be(Unauthorized)
      }
    }
  }

  private def getRequest(hawkHeader: String) = {
    val req = Request(Get, "/does/not/matter")
    req.headerMap.add(AuthorisationHttpHeader, hawkHeader)
    req
  }
}
