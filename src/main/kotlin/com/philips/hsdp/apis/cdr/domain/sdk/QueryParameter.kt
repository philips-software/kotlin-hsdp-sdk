/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Query parameters for addition to the request url.
 */
@Serializable
data class QueryParameter(
    /**
     * Name of the query parameter.
     */
    val name: String,

    /**
     * Value of the query parameter.
     */
    val value: String,
)
