/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Units of time to use in a [DeletePolicy][com.philips.hsdp.apis.tdr.domain.hsdp.DeletePolicy]
 */
@Serializable
enum class TimeUnit {
    @SerialName("DAY") Day,
    @SerialName("MONTH") Month,
    @SerialName("YEAR") Year,
}
