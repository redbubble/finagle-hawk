package com.redbubble.hawk

import java.net.URI

import com.redbubble.hawk.HawkAuthenticate.hawkHeader
import com.redbubble.hawk.params._
import com.redbubble.hawk.spec.SpecHelper
import com.redbubble.hawk.util.Time
import com.twitter.finagle.http.Status.{Ok, Unauthorized}
import com.twitter.util.Await
import org.specs2.mutable.Specification

final class KnownClientsSpec extends Specification with SpecHelper with RequestSpecOps {
  private val hawkAuthenticatedService = HawkAuthFilter andThen TestService

  "Swift client" >> {
    /*
Received request on path '/v1/graphql' from /0:0:0:0:0:0:0:1 with header 'Hawk id="key-id", mac="nTIDWdPTanQSJaSv9VrcrSLUeNzf0SZzaAF9JNjbIRU=", ts="1506383154", nonce="5AFDA94D-0D44-4699-A98D-CD90645E1878"'
Built request context: ValidatableRequestContext(RequestContext(Post,Host(localhost),Port(80),UriPath(/v1/graphql),Some(PayloadContext(ContentType(application/json),[B@6b3e491b))),RequestAuthorisationHeader(key-id,Time(1506383154000),Nonce(5AFDA94D-0D44-4699-A98D-CD90645E1878),None,None,MAC(nTIDWdPTanQSJaSv9VrcrSLUeNzf0SZzaAF9JNjbIRU=)))
     */
    "can be authenticated" >> {
      val invalidHawkHeader = s""" Hawk id="API Client", mac="WiLQDi5ssWxbmEG64X8TOaEQjp0vxg0qRV0AzIEEhdc=", ts="1506382258", nonce="93AC926F-B0DB-45CB-83DD-C90C1EE2FAB2" """.trim
      val req = requestWithAuthHeader(URI.create("/v1/graphql"), invalidHawkHeader)

      "does not execute the service" >> {
        val result = Await.result(hawkAuthenticatedService(req))
        result.status should be(Ok)
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
