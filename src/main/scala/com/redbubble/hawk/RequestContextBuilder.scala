package com.redbubble.hawk

import java.net.URI

import com.github.benhutchison.mouse.all._
import com.redbubble.hawk.HawkAuthenticate.parseRawRequestAuthHeader
import com.redbubble.hawk.params._
import com.redbubble.hawk.validate.RequestAuthorisationHeader
import com.twitter.finagle.http.Request
import com.twitter.io.Buf
import com.twitter.util.Try

object RequestContextBuilder {
  def buildContext(request: Request): Option[RequestContext] =
    for {
      header <- parseAuthHeader(request)
      method <- HttpMethod.httpMethod(request.method.toString())
      requestUri <- Try(new URI(request.uri)).toOption
    } yield {
      val host = request.host.map(Host(_)).getOrElse(Host.UnknownHost)
      val port = Port(requestUri.getPort)
      val path = UriPath(requestUri.getRawPath)
      val pc = methodDependantPayloadContext(method, request.contentType, request.content)
      RequestContext(method, host, port, path, header, pc)
    }

  private def parseAuthHeader(request: Request): Option[RequestAuthorisationHeader] =
    request.headerMap.get(AuthorisationHttpHeader).flatMap(s => parseRawRequestAuthHeader(RawAuthenticationHeader(s)))

  private def methodDependantPayloadContext(method: HttpMethod, contentType: Option[String], content: Buf): Option[PayloadContext] =
    List(Put, Post, Patch).contains(method).option(payloadContext(contentType, content))

  private def payloadContext(contentType: Option[String], content: Buf): PayloadContext = {
    val ct = contentType.map(ContentType(_)).getOrElse(ContentType.UnknownContentType)
    PayloadContext(ct, bufToStream(content))
  }

  private def bufToStream(b: Buf): Array[Byte] = {
    val output = new Array[Byte](b.length)
    b.write(output, 0)
    output
  }
}
