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

class DynamicStringQueryTest {
    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when name starts with data and ends with exact with single value`() {
            shouldNotThrowAny { DynamicStringQuery("data.foo:exact", "bar") }
        }

        @Test
        fun `Should not throw when name starts with data and ends with exact with multiple values`() {
            shouldNotThrowAny { DynamicStringQuery("data.foo:exact", listOf("foo", "bar")) }
        }

        @Test
        fun `Should throw when name does not start with data`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DynamicStringQuery("foo", "bar")
            }
            exception.message shouldBe "Dynamic queries must prefix data fields with 'data.'"
        }

        @Test
        fun `Should throw when name does not end with exact`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DynamicStringQuery("data.foo", "bar")
            }
            exception.message shouldBe "Dynamic string query parameter names must end with ':exact'"
        }

        @Test
        fun `Should throw when value list is empty`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DynamicStringQuery("data.foo", emptyList())
            }
            exception.message shouldBe "The supplied value list should at least have one item"
        }

        @Test
        fun `Should throw when value is empty string`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DynamicStringQuery("data.foo", "")
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Should properly convert the query to a QueryParameter for a single value`() {
            DynamicStringQuery("data.foo:exact", "bar")
                .asQueryParameter() shouldBe QueryParameter(name = "data.foo:exact", value = "bar")
        }

        @Test
        fun `Should properly convert the query to a QueryParameter for multiple values`() {
            DynamicStringQuery("data.foo:exact", listOf("bar", "baz"))
                .asQueryParameter() shouldBe QueryParameter(name = "data.foo:exact", value = "bar,baz")
        }
    }
}
