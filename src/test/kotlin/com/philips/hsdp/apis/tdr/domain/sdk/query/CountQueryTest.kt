/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class CountQueryTest {

    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when using the minimum value of 1`() {
            shouldNotThrowAny { CountQuery(1) }
        }

        @Test
        fun `Should not throw when using the maximum value of 100`() {
            shouldNotThrowAny { CountQuery(100) }
        }

        @Test
        fun `Should throw when using a value below the minimum value`() {
            val exception = shouldThrow<IllegalArgumentException> {
                CountQuery(0)
            }
            exception.message shouldBe "Count must be a value in the range 1 to 100"
        }

        @Test
        fun `Should throw when using a value above the maximum value`() {
            val exception = shouldThrow<IllegalArgumentException> {
                CountQuery(101)
            }
            exception.message shouldBe "Count must be a value in the range 1 to 100"
        }

        @Test
        fun `Should throw when using an empty list of count values`() {
            val exception = shouldThrow<IllegalArgumentException> {
                CountQuery(emptyList())
            }
            exception.message shouldBe "The supplied value list should at least have one item"
        }

        @Test
        fun `Should throw when using a list with multiple count values`() {
            val exception = shouldThrow<IllegalArgumentException> {
                CountQuery(listOf(10, 20))
            }
            exception.message shouldBe "Count value must have a single value"
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Should properly convert the query to a QueryParameter`() {
            CountQuery(1)
                .asQueryParameter() shouldBe QueryParameter(name = "_count", value = "1")
        }
    }
}
