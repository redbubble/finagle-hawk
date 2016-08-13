package com.redbubble.hawk.validate

import cats.data.Xor
import com.github.benhutchison.mouse.all._
import com.redbubble.hawk.{HawkError, ValidationMethod}
import com.redbubble.hawk._
import com.redbubble.hawk.params.RequestContext
import com.redbubble.hawk.validate.Maccer.requestMac

trait MacValid

object MacValidation extends Validator[MacValid] {
  override def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[HawkError, MacValid] =
    requestMac(credentials, context, method).map {
      computedMac => validateMac(computedMac, context.clientAuthHeader.mac)
    }.getOrElse(errorXor("Request MAC does not match computed MAC"))

  private def validateMac(computedMac: MAC, providedMac: MAC): Xor[HawkError, MacValid] =
    (computedMac == providedMac).xor(error("Request MAC does not match computed MAC"), new MacValid {})
}
