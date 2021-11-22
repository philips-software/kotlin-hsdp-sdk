/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource update request by query.
 */
@Serializable
data class UpdateByQueryRequest(
    /**
     * Type of resource to update.
     */
    val resourceType: String,

    /**
     * The query parameters to be used for the search of the resource(s) to be updated.
     */
    val queryParameters: List<QueryParameter>,

    /**
     * String representation for the resource update.
     */
    val body: String,

    /**
     * Indicate for which version this patch is intended (optimistic locking feature),
     * to make sure no (parallel) changes are lost.
     */
    val forVersion: String? = null,

    /**
     * Optional parameter to choose the response MIME type for clients that cannot modify the 'Accept' header.
     * Overrides the value given in the 'Accept' header.
     */
    val format: FormatParameter? = null,

    /**
     * Preference for the amount of data to be returned.
     */
    val preference: ReturnPreference? = null,
)
