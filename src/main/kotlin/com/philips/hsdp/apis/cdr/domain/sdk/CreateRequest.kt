/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource creation request.
 */
@Serializable
data class CreateRequest(
    /**
     * Type of resource to create.
     */
    val resourceType: String,

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
     * Condition that must be satisfied to create the resource.
     * If defined, will lead to an 'If-None-Exists' header with given expression.
     */
    val condition: String? = null,

    /**
     * Preference for the amount of data to be returned.
     */
    val preference: ReturnPreference? = null,
)