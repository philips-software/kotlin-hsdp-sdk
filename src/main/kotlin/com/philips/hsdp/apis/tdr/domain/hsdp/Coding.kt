/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Representation of a known value in a system.
 *
 * NOTE: according to API spec both fields are optional; is that really the case? Seems not useful...
 */
@Serializable
data class Coding(
    /**
     * URN identifying the system of the value.
     */
    val system: String,

    /**
     * Value of the code within the system.
     */
    val code: String,
)
