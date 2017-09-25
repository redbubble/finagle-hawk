package com.redbubble.hawk.validate

import java.security.MessageDigest

import com.redbubble.hawk._

final case class MAC(encoded: Base64Encoded)

object MAC {
  def isEqual(macA: MAC, macB: MAC): Boolean = {
    MessageDigest.isEqual(macA.encoded.getBytes, macB.encoded.getBytes)
  }
}
