package com.redbubble.hawk.validate

import java.nio.charset.StandardCharsets._

import com.redbubble.hawk.{HeaderValidationMethod, PayloadValidationMethod}
import com.redbubble.hawk.params.{PayloadContext, RequestContext}
import com.redbubble.hawk.{PayloadValidationMethod, _}

object NormalisedRequest {
  def normalisedHeaderMac(credentials: Credentials, context: RequestContext, normalisedPayloadMac: Option[MAC]): MAC = {
    val normalised =
      s"""
         |${HeaderValidationMethod.identifier}
         |${context.clientAuthHeader.timestamp.asSeconds}
         |${context.clientAuthHeader.nonce}
         |${context.method.httpRequestLineMethod}
         |${context.path.path}
         |${context.host.host}
         |${context.port.port}
         |${normalisedPayloadMac.map(h => h.encoded).getOrElse("")}
         |${context.clientAuthHeader.extendedData.getOrElse("")}
      """.stripMargin.trim + "\n"
    MacOps.mac(credentials, normalised.getBytes(UTF_8))
  }

  def normalisedPayloadMac(credentials: Credentials, payload: PayloadContext): MAC = {
    val normalised =
      s"""
         |${PayloadValidationMethod.identifier}
         |${payload.contentType.contentType.toLowerCase}
         |${new String(payload.data, UTF_8)}
      """.stripMargin.trim + "\n"
    MacOps.mac(credentials, normalised.getBytes(UTF_8))
  }

}
