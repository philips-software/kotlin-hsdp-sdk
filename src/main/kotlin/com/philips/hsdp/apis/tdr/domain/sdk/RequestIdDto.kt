/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * RequestId contains traceability information, useful in microservice applications.
 *
 * Some HSDP TDR operations just return a HSDP-Request-Id header. For those operations, the SDK returns
 * this information in a [RequestIdDto] instance.
 */
@Serializable
data class RequestIdDto(
    val requestId: String,
)
