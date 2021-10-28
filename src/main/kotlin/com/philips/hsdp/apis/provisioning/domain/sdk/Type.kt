/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.sdk

import kotlinx.serialization.Serializable

/**
 * TODO: obtain information for properly documenting this data structure.
 */
@Serializable
data class Type(
    val code: String,
    val text: String,
)
