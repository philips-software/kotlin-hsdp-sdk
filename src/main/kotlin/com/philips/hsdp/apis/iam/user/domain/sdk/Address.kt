/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val use: AddressUse? = null,
    val text: String? = null,
    val lines: List<String>? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
)
