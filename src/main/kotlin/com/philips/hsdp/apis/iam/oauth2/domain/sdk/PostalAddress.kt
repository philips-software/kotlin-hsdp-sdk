/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.sdk

import kotlinx.serialization.Serializable

@Serializable
data class PostalAddress(
    val formatted: String = "",
    val streetAddress: String = "",
    val postalCode: String = "",
)
