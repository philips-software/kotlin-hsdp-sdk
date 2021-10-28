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

class SystemCodeStringQueryTest {

    data class TestQuery(
        override val system: List<String>,
        override val value: List<String>
    ): SystemCodeStringQuery {
        override val name = "test"

        constructor(system: String, value: String): this(listOf(system), listOf(value))

        init {
            validate()
        }
    }

    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when supplying non-blank string for system and value`() {
            shouldNotThrowAny { TestQuery("1", "2") }
        }

        @Test
        fun `Should not throw when supplying a list of non-blank string systems and values`() {
            shouldNotThrowAny { TestQuery(listOf("1", "2"), listOf("3", "4")) }
        }

        @Test
        fun `Should throw when supplying a single empty string for system and value`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery("", "")
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when supplying a single string with spaces for system and value`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery("  ", "  ")
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when supplying a list of strings with 1 empty one`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery(listOf("1", ""), listOf("3", ""))
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when supplying a list of string with 1 with spaces-only`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery(listOf("  ", "2"), listOf("  ", "4"))
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when supplying an empty value list`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery(emptyList(), emptyList())
            }
            exception.message shouldBe "The supplied value list should at least have one item"
        }

        @Test
        fun `Should throw when supplying system and value lists with different size`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery(listOf("1"), listOf("2", "3"))
            }
            exception.message shouldBe "The supplied system and value lists must have same size"
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Should properly convert the query to a QueryParameter for a single value`() {
            TestQuery("foo", "bar")
                .asQueryParameter() shouldBe QueryParameter(name = "test", value = "foo|bar")
        }

        @Test
        fun `Should properly convert the query to a QueryParameter for multiple values`() {
            TestQuery(
                listOf("foo", "foz"),
                listOf("bar", "baz")
            ).asQueryParameter() shouldBe QueryParameter(name = "test", value = "foo|bar,foz|baz")
        }
    }
}

class SystemCodeStringQueryDerivedClassesTest {
    @Nested
    inner class Validation {
        @Test
        fun `Derived classes call the interface validation implementation`() {
            shouldThrow<IllegalArgumentException> { UserQuery(emptyList(), emptyList()) }
            shouldThrow<IllegalArgumentException> { DeviceQuery(emptyList(), emptyList()) }
            shouldThrow<IllegalArgumentException> { RelatedUserQuery(emptyList(), emptyList()) }
            shouldThrow<IllegalArgumentException> { RelatedPeripheralQuery(emptyList(), emptyList()) }
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Derived classes call the interface method implementation`() {
            UserQuery("foo", "bar").asQueryParameter() shouldBe QueryParameter(name = "user", value = "foo|bar")
            DeviceQuery("foo", "bar").asQueryParameter() shouldBe QueryParameter(name = "device", value = "foo|bar")
            RelatedUserQuery("foo", "bar").asQueryParameter() shouldBe QueryParameter(name = "relatedUser", value = "foo|bar")
            RelatedPeripheralQuery("foo", "bar").asQueryParameter() shouldBe QueryParameter(name = "relatedPeripheral", value = "foo|bar")
        }
    }
}
