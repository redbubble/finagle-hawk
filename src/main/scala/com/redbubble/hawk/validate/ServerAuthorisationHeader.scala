package com.redbubble.hawk.validate

import com.redbubble.hawk._

final case class ServerAuthorisationHeader(mac: MAC, payloadHash: PayloadHash, extendedData: ExtendedData)
