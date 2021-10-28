/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

/**
 * Query interface for strings with system and code parameters.
 *
 * The [value] and [system] list fields should have the same size.
 */
interface SystemCodeStringQuery: StringQuery {
    /**
     * Additional field containing the "system" values.
     */
    val system: List<String>

    /**
     * Validate that both lists have same size.
     */
    override fun validate() {
        super.validate()
        require(system.size == value.size) {
            "The supplied system and value lists must have same size"
        }
    }

    /**
     * Convert the query to a query parameter that can be used to construct the query parameters of a URL.
     *
     * Each system-value pair is joined with a "|" as separator, and the results are joined with a "," as separator.
     */
    override fun asQueryParameter(): QueryParameter =
        QueryParameter(
            name,
            system.zip(value).joinToString(",") { "${it.first}|${it.second}" }
        )
}
