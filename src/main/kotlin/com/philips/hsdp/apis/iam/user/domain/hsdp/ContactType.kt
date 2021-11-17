/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
@file:Suppress("unused")

package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ContactType {
    @SerialName("mobile") Mobile,
    @SerialName("fax") Fax,
    @SerialName("email") Email,
    @SerialName("url") Url,
}
