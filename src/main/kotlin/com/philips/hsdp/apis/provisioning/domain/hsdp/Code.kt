/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Error or warning code.
 */
@Serializable
enum class Code {
    @SerialName("invalid") Invalid,
    @SerialName("processing") Processing,
    @SerialName("informational") Informational,
    @SerialName("timeout") Timeout,
}
