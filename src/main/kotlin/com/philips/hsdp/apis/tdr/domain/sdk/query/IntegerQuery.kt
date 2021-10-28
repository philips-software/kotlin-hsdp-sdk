/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

/**
 * Query interface for integer parameters.
 */
interface IntegerQuery {
    /**
     * Name of the parameter (will be left of the equals-sign in the eventual url query parameter).
     */
    val name: String
    /**
     * Value(s) of the parameter (will be right of the equals-sign in the eventual url query parameter).
     *
     * When supplying multiple values, they will be OR-ed by TDR.
     */
    val value: List<Int>

    /**
     * Validation for integer queries.
     *
     * Integer queries should at least have one value.
     */
    fun validate() {
        require(value.isNotEmpty()) {
            "The supplied value list should at least have one item"
        }
    }

    /**
     * Convert the query to a query parameter that can be used to construct the query parameters of a URL.
     */
    fun asQueryParameter(): QueryParameter = QueryParameter(name, value.joinToString(","))
}
