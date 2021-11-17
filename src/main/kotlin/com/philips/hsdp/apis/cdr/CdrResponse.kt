/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import kotlinx.serialization.Serializable

@Serializable
data class CdrResponse(
    val status: Int,
    val jsonRepresentation: String,
    val versionId: String,
    val lastModified: String,
)
