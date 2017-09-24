package com.redbubble.hawk.validate

import java.nio.charset.StandardCharsets._

import com.redbubble.hawk.params.{Nonce, PayloadContext, RequestContext, ValidatableRequestContext}
import com.redbubble.hawk.util.Time
import com.redbubble.hawk.{ExtendedData, HeaderValidationMethod, PayloadValidationMethod}

object NormalisedRequest {
  def normalisedHeaderMac(credentials: Credentials, time: Time, nonce: Nonce, context: RequestContext,
      extendedData: Option[ExtendedData], normalisedPayloadMac: Option[MAC]): MAC = {
    val headerFields: Seq[String] = Seq(
      HeaderValidationMethod.identifier,
      time.asSeconds.toString,
      nonce.encoded,
      context.method.httpRequestLineMethod,
      context.path.path,
      context.host.host,
      context.port.port.toString,
      normalisedPayloadMac.map(h => h.encoded).getOrElse(""),
      extendedData.getOrElse("")
    )
    val normalised = headerFields.map(_.trim).mkString("", "\n", "\n")
    MacOps.mac(credentials, normalised.getBytes(UTF_8))
  }

  def normalisedHeaderMac(credentials: Credentials, context: ValidatableRequestContext, normalisedPayloadMac: Option[MAC]): MAC =
    normalisedHeaderMac(credentials, context.clientAuthHeader.timestamp,
      context.clientAuthHeader.nonce, context.context, context.clientAuthHeader.extendedData, normalisedPayloadMac)

  def normalisedPayloadMac(credentials: Credentials, payload: PayloadContext): MAC = {
    val headerFields: Seq[String] = Seq(
      PayloadValidationMethod.identifier,
      payload.contentType.contentType.toLowerCase,
      new String(payload.data, UTF_8)
    )
    val normalised = headerFields.map(_.trim).mkString("", "\n", "\n")
    MacOps.mac(credentials, normalised.getBytes(UTF_8))
  }
}
