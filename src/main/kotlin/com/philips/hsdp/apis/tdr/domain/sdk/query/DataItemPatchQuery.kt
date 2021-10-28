/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

import com.thinkinglogic.builder.annotation.Builder

/**
 * Query structure for patching a data item.
 *
 * In HSDP TDR API, all query parameters are part of the URL. They are sometimes also interrelated, and often optional.
 * The SDK abstracts this for the user into query data structures, performing checks on those structures, and assembles
 * the URL to be used in the call to HSDP TDR API.
 */
@Builder
data class DataItemPatchQuery(
    /**
     * Mandatory query that identifies the organization.
     */
    val organizationQuery: OrganizationQuery,
    /**
     * Mandatory query that identifies the data item.
     */
    val idQuery: DataItemIdQuery,
    /**
     * Optional query that identifies the user for the data item. Either this or [deviceQuery] must be provided.
     */
    val userQuery: UserQuery? = null,
    /**
     * Optional query that identifies the device for the data item. Either this or [userQuery] must be provided.
     */
    val deviceQuery: DeviceQuery? = null,
) {
    init {
        require(userQuery != null || deviceQuery != null) {
            "At least one of user or device must be specified"
        }
    }

    /**
     * Convert the query to a list of query parameter that can be used to construct the query parameters of a URL.
     */
    fun getQueryParameters(): List<QueryParameter> {
        return listOfNotNull(
            organizationQuery.asQueryParameter(),
            idQuery.asQueryParameter(),
            userQuery?.asQueryParameter(),
            deviceQuery?.asQueryParameter(),
        )
    }
}
