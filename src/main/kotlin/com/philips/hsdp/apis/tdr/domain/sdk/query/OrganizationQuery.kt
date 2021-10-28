/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

/**
 * Query structure for organization of a contract or data item.
 *
 * Only one organization may be specified!
 */
data class OrganizationQuery(
    override val value: List<String>,
): StringQuery {
    override val name = "organization"

    constructor(value: String): this(listOf(value))

    init {
        validate()
        // No OR possibility allowed
        require(value.size == 1) {
            "Specify only one organization"
        }
    }
}
