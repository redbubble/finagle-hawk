package com.redbubble.hawk

import java.net.URI

import com.redbubble.hawk.HawkAuthenticate.parseRawRequestAuthHeader
import com.redbubble.hawk.params.HttpMethod.payloadValidationMethods
import com.redbubble.hawk.params._
import com.redbubble.hawk.validate.RequestAuthorisationHeader
import com.redbubble.util.io.BufOps
import com.twitter.finagle.http.Request
import com.twitter.io.Buf
import com.twitter.util.Try
import mouse.all._

object RequestContextBuilder {
  private val portRegex = ":[0-9]+".r

  def buildContext(request: Request): Option[ValidatableRequestContext] =
    for {
      header <- parseAuthHeader(request)
      method <- HttpMethod.httpMethod(request.method.toString())
      requestUri <- Try(new URI(request.uri)).toOption
    } yield {
      val host = guessRequestHost(request.host, requestUri)
      val port = guessRequestPort(requestUri)
      val path = UriPath(requestUri.getRawPath)
      val pc = methodDependantPayloadContext(method, request.contentType, request.content)
      ValidatableRequestContext(RequestContext(method, host, port, path, pc), header)
    }

  private def parseAuthHeader(request: Request): Option[RequestAuthorisationHeader] =
    request.headerMap.get(AuthorisationHttpHeader).flatMap(s => parseRawRequestAuthHeader(RawAuthenticationHeader(s)))

  private def methodDependantPayloadContext(method: HttpMethod, contentType: Option[String], content: Buf): Option[PayloadContext] =
    payloadValidationMethods.contains(method).option(payloadContext(contentType, content))

  private def payloadContext(contentType: Option[String], content: Buf): PayloadContext = {
    val ct = contentType.map(ContentType(_)).getOrElse(ContentType.UnknownContentType)
    PayloadContext(ct, BufOps.bufToByteArray(content))
  }

  // Use the Host header if present, or default to the request URI.
  private def guessRequestHost(hostHeader: Option[String], requestUri: URI) =
    hostHeader.orElse(Option(requestUri.getHost)).map { h =>
      Host(portRegex.replaceAllIn(h, ""))
    }.getOrElse(Host.UnknownHost)

  // Do our best to figure out the port.
  private def guessRequestPort(requestUri: URI) = {
    val port =
      requestUri.getPort match {
        case -1 =>
          requestUri.getScheme match {
            case "https" => 443
            case _ => 80
          }
        case p => p
      }
    Port(port)
  }
}
