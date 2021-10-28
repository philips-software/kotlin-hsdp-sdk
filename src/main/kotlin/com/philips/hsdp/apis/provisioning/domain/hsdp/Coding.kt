/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Representation of a known value in some coding system.
 */
@Serializable
data class Coding(
    /**
     * Identity of the terminology system in the format of URI.
     * For example https://philips-healthsuite.com/connect/provisioning/errorcodes
     */
    val system: String,

    /**
     * Value of the code within the coding system.
     */
    val code: String,
)
