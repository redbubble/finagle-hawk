package com.redbubble.hawk.validate

import com.redbubble.hawk._
import com.redbubble.util.time.Time

final case class RequestAuthorisationHeader(keyId: KeyId, timestamp: Time, nonce: Nonce, payloadHash: Option[PayloadHash],
  extendedData: Option[ExtendedData], mac: MAC)
