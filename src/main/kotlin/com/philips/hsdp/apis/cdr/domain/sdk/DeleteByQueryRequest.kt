/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource deletion request by query.
 */
@Serializable
data class DeleteByQueryRequest(
    /**
     * Type of resource to delete.
     */
    val resourceType: String,

    /**
     * The query parameters to be used for the search of the resource(s) to be deleted.
     */
    val queryParameters: List<QueryParameter>,
)