package com.redbubble.hawk

import java.net.URI

import com.redbubble.hawk.HawkAuthenticate.hawkHeader
import com.redbubble.hawk.params._
import com.redbubble.hawk.spec.SpecHelper
import com.redbubble.hawk.util.Time
import com.twitter.finagle.http.Status.{Ok, Unauthorized}
import com.twitter.util.Await
import org.specs2.mutable.Specification

final class HawkAuthenticateRequestFilterSpec extends Specification with SpecHelper with RequestSpecOps {
  "HawkAuthenticateRequestFilter" >> {
    val hawkAuthenticatedService = HawkAuthFilter andThen TestService

    "with an invalid Hawk authorisation header" >> {
      val timeStamp = Time.nowUtc.millis
      val nonce = Nonce.generateNonce
      val invalidHawkHeader = s"""Hawk id="dh37fgj492je", ts="$timeStamp", nonce="$nonce", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
      val req = requestWithAuthHeader(URI.create("/uri/does/not/matter"), invalidHawkHeader)

      "does not execute the service" >> {
        val result = Await.result(hawkAuthenticatedService(req))
        result.status should be(Unauthorized)
      }
    }

    "with a valid Hawk authorisation header" >> {
      val uri = URI.create("https://example.com/foo/bar")
      val context = RequestContext(Get, Host("example.com"), Port(443), UriPath(uri.getPath), None)
      val validHawkHeader = hawkHeader(credentials, context).httpHeaderForm
      val req = requestWithAuthHeader(uri, validHawkHeader)

      "executes the service" >> {
        val result = Await.result(hawkAuthenticatedService(req))
        result.status should be(Ok)
      }
    }
  }
}
