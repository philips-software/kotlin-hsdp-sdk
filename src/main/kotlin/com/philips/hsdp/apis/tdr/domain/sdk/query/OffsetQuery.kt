/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

/**
 * Query structure for the page offset in the paginated results.
 *
 * The [value] field must have a single value.
 */
data class OffsetQuery(
    override val value: List<Int>,
): IntegerQuery {
    override val name = "_startAt"

    constructor(value: Int): this(listOf(value))

    init {
        validate()
        require(value.size == 1) {
            "Count value must have a single value"
        }
    }
}
