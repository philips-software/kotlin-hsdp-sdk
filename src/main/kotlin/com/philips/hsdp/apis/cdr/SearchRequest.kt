/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val compartment: Compartment? = null,
    val resourceType: String,
    val format: FormatParameter?  = null,
    val queryParameters: List<Pair<String, String>>? = null
)