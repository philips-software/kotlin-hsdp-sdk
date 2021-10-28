/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * A response containing the output for a single operation.
 */
@Serializable
data class OperationResponse(
    /**
     * HTTP status code
     *
     * NOTE: looking at batch create responses, it seems that status should be a number i.s.o. a string
     */
    val status: String,

    /**
     * Full URL to the resource if created successfully.
     */
    val location: String? = null,

    /**
     * The version of the newly created resource.
     */
    val etag: String? = null,

    /**
     * Date and time of the creation of this resource.
     */
    val lastModified: String? = null,

    /**
     * A collection of error, warning or information messages that result from a system action.
     * Returned only in case of an API call failure.
     */
    val outcome: OperationOutcome? = null,
)
