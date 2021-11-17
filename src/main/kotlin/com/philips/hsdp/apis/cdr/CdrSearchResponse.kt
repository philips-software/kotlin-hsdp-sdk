/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import kotlinx.serialization.Serializable

@Serializable
data class CdrSearchResponse(
    val status: Int,
    val jsonRepresentation: String,
)
