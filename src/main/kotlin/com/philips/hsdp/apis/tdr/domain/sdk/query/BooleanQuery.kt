/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

/**
 * Query interface for boolean parameters.
 */
interface BooleanQuery {
    /**
     * Name of the parameter (will be left of the equals-sign in the eventual url query parameter).
     */
    val name: String
    /**
     * Value of the parameter (will be right of the equals-sign in the eventual url query parameter).
     *
     * Note: it does not make sense to make this field a list of Booleans, because OR-ing true and false
     * will always be true, rendering the query without filtering effect.
     */
    val value: Boolean

    /**
     * Convert the parameter to a query parameter that can be used to construct the query parameters of a URL.
     */
    fun asQueryParameter(): QueryParameter = QueryParameter(name, value.toString())
}
