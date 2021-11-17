/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AddressUse {
    @SerialName("home") Home,
    @SerialName("work") Work,
    @SerialName("temp") Temp,
    @SerialName("old") Old,
}