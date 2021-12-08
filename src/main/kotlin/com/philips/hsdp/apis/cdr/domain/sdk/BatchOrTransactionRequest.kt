/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource creation request.
 */
@Serializable
@Builder
data class BatchOrTransactionRequest(
    /**
     * String representation of the resource to create. Can be JSON or XML format.
     */
    val body: String,

    /**
     * Optional parameter to choose the response MIME type for clients that cannot modify the Accept_header.
     * Overrides the value given in the 'Accept' header.
     */
    val format: FormatParameter? = null,

    /**
     * Preference for the amount of data to be returned.
     */
    val preference: ReturnPreference? = null,
)