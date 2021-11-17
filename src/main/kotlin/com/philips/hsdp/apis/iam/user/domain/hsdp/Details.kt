/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Additional details about the error occurred.
 */
@Serializable
data class Details(
    /**
     * Representation of a known value in some coding system.
     */
    val coding: Coding? = null,

    /**
     * Detailed message of the error.
     */
    val text: String? = null,
)
