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

class DataTypeQueryTest {
    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when using a single non-blank string for system and value`() {
            shouldNotThrowAny {
                DataTypeQuery("1", "2")
            }
        }

        @Test
        fun `Should throw when using lists with multiple entries for system and value`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataTypeQuery(listOf("1", "2"), listOf("3", "4"))
            }
            exception.message shouldBe "The supplied system and value lists must each consist of a single item"
        }

        @Test
        fun `Should throw when using system and value lists with different size`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataTypeQuery(listOf("1", "2"), listOf("3"))
            }
            exception.message shouldBe "The supplied system and value lists must have same size"
        }

        @Test
        fun `Should throw when using an empty value list`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataTypeQuery(listOf("1"), emptyList())
            }
            exception.message shouldBe "The supplied value list should at least have one item"
        }

        @Test
        fun `Should throw when using an empty string value`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataTypeQuery("foo", "")
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when using a value with spaces-only`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataTypeQuery(listOf("foo"), listOf("  "))
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when using a value with separator character`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataTypeQuery(listOf("foo"), listOf("bar,baz"))
            }
            exception.message shouldBe "The supplied values may not contain separator characters"
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Should properly convert the query to a QueryParameter`() {
            DataTypeQuery("foo", "bar")
                .asQueryParameter() shouldBe QueryParameter(name = "dataType", value = "foo|bar")
        }
    }
}