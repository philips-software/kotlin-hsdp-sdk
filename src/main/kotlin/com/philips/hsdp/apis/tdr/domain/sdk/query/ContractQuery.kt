/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

import com.thinkinglogic.builder.annotation.Builder

/**
 * Query structure for getting contracts.
 *
 * In HSDP TDR API, all query parameters are part of the URL. They are sometimes also interrelated, and often optional.
 * The SDK abstracts this for the user into query data structures, performing checks on those structures, and assembles
 * the URL to be used in the call to HSDP TDR API.
 */
@Builder
data class ContractQuery(
    /**
     * Optional query that allows for filtering on certain data type. Either this or [organizationQuery] is required.
     */
    val dataTypeQuery: SystemCodeStringQuery? = null,
    /**
     * Optional query that allows for filtering on certain organization. Either this or [dataTypeQuery] is required.
     */
    val organizationQuery: OrganizationQuery? = null,
    /**
     * Optional query that allows for indicating the number of results to receive on a 'page'.
     *
     * If not provided, the default page size will be used, see
     * [defaultLimit][com.philips.hsdp.apis.tdr.domain.conversion.defaultLimit].
     */
    val countQuery: CountQuery? = null,
    /**
     * Optional query that allows for indicating which 'page' to receive.
     *
     * If not provided, the first page will be returned.
     */
    val offsetQuery: OffsetQuery? = null
) {
    init {
        require(dataTypeQuery != null || organizationQuery != null) {
            "Either dataType or organization must be provided"
        }
//         // NOTE: the API spec mentions that it should be in the range of 0..10 but TDR does not enforce this.
//         if (count != null) {
//             require(count >= 0 && count <= 10)
//         }
    }

    /**
     * Convert the query to a list of query parameter that can be used to construct the query parameters of a URL.
     */
    fun getQueryParameters(): List<QueryParameter> {
        return listOfNotNull(
            dataTypeQuery?.let { dataTypeQuery.asQueryParameter() },
            organizationQuery?.let { organizationQuery.asQueryParameter() },
            countQuery?.let { countQuery.asQueryParameter() },
            offsetQuery?.let { offsetQuery.asQueryParameter() },
        )
    }
}
