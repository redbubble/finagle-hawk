package com.redbubble.hawk.validate

import com.redbubble.hawk.HeaderValidationMethod
import com.redbubble.hawk._
import com.redbubble.hawk.params._
import com.redbubble.hawk.validate.TimeValidation.{acceptableTimeDelta, validate}
import com.redbubble.util.spec.SpecHelper
import com.redbubble.util.time.Time
import com.redbubble.util.time.Time.nowUtc
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class TimeValidationSpec extends Specification with SpecHelper {
  val credentials = Credentials(KeyId("fred"), Key("d0p1h1n5"), Sha256)

  val timestamps = new Properties("Timestamps") {
    property("are valid if within the interval") = forAll { (time: Time) =>
      val delta = nowUtc.minus(time).getStandardSeconds
      if (delta > acceptableTimeDelta.getStandardSeconds) {
        validate(credentials, context(time), HeaderValidationMethod) must beXorLeft
      } else {
        validate(credentials, context(time), HeaderValidationMethod) must beXorRight
      }
    }
  }

  s2"Validating timestamps$timestamps"

  private def context(time: Time): RequestContext = {
    val header = RequestAuthorisationHeader(KeyId("fred"), time, Nonce("nonce"), None, Some(ExtendedData("data")), MAC(Base64Encoded("base64")))
    RequestContext(Get, Host("example.com"), Port(80), UriPath("/"), header, None)
  }
}
