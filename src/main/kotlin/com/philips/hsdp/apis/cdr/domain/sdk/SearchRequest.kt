/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource search.
 */
@Serializable
data class SearchRequest(
    /**
     * The logical grouping of resources for which to perform the search.
     */
    val compartment: Compartment? = null,

    /**
     * Type of resource to patch.
     */
    val resourceType: String,

    /**
     * Optional parameter to choose the response MIME type for clients that cannot modify the 'Accept' header.
     * Overrides the value given in the 'Accept' header.
     */
    val format: FormatParameter? = null,

    /**
     * The query parameters to be used for the search of the resource(s) to be found.
     */
    val queryParameters: List<QueryParameter>? = null,
)