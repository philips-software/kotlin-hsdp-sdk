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

class DynamicIntegerQueryTest {
    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when name starts with data and ends with exact with single value`() {
            shouldNotThrowAny { DynamicIntegerQuery("data.foo:exact", 1) }
        }

        @Test
        fun `Should not throw when name starts with data and ends with exact with multiple values`() {
            shouldNotThrowAny { DynamicIntegerQuery("data.foo:exact", listOf(1, 2)) }
        }

        @Test
        fun `Should throw when name does not start with data`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DynamicIntegerQuery("foo", 1)
            }
            exception.message shouldBe "Dynamic queries must prefix data fields with 'data.'"
        }

        @Test
        fun `Should throw when value list is empty`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DynamicIntegerQuery("data.foo", emptyList())
            }
            exception.message shouldBe "The supplied value list should at least have one item"
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Should properly convert the query to a QueryParameter for a single value`() {
            DynamicIntegerQuery("data.foo:exact", 1)
                .asQueryParameter() shouldBe QueryParameter(name = "data.foo:exact", value = "1")
        }

        @Test
        fun `Should properly convert the query to a QueryParameter for multiple values`() {
            DynamicIntegerQuery("data.foo:exact", listOf(1, 2))
                .asQueryParameter() shouldBe QueryParameter(name = "data.foo:exact", value = "1,2")
        }
    }
}
