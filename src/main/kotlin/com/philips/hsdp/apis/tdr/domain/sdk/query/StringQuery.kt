/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

/**
 * Query interface for string parameters.
 */
interface StringQuery {
    /**
     * Name of the parameter (will be left of the equals-sign in the eventual url query parameter).
     */
    val name: String
    /**
     * Value(s) of the parameter (will be right of the equals-sign in the eventual url query parameter).
     *
     * When supplying multiple values, they will be OR-ed by TDR.
     */
    val value: List<String>

    /**
     * Validation for string queries.
     *
     * String queries should at least have one value, each value should not be blank, and they should not
     * contain commas.
     */
    fun validate() {
        require(value.isNotEmpty()) {
            "The supplied value list should at least have one item"
        }
        require(value.all { it.isNotBlank() }) {
            "The supplied values may not be empty or only consist of spaces"
        }
        require(value.all { it.none { c -> c == ',' } }) {
            "The supplied values may not contain separator characters"
        }
    }

    /**
     * Convert the query to a query parameter that can be used to construct the query parameters of a URL.
     */
    fun asQueryParameter(): QueryParameter = QueryParameter(name, value.joinToString(","))
}
