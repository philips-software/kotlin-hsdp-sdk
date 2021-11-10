/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Representation of a known value in some coding system.
 */
@Serializable
data class Coding(
    /**
     * URL identifying the terminology of the system.
     */
    val system: String? = null,

    /**
     * Version of the coding system.
     */
    val version: String? = null,

    /**
     * Value of the code within the coding system.
     */
    val code: String? = null,

    /**
     * Textual, human-readable representation of the code for use in UIs.
     */
    val display: String? = null,

    /**
     * If this coding was chosen directly by the user.
     */
    val userSelected: Boolean? = null,
)
