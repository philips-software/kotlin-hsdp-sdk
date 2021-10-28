/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

/**
 * Query structure for the number of items per page.
 *
 * The [value] field must have a single value and must be in the range 1..100.
 */
data class CountQuery(
    override val value: List<Int>,
): IntegerQuery {
    override val name = "_count"

    constructor(value: Int): this(listOf(value))

    init {
        validate()
        require(value.size == 1) {
            "Count value must have a single value"
        }
        require(value[0] in 1..100) {
            "Count must be a value in the range 1 to 100"
        }
    }
}
