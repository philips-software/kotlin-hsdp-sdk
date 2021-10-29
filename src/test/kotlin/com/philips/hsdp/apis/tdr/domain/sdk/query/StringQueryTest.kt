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

class StringQueryTest {
    data class TestQuery(override val value: List<String>) : StringQuery {
        override val name = "test"

        constructor(value: String) : this(listOf(value))

        init {
            validate()
        }
    }

    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when supplying a single non-blank string value`() {
            shouldNotThrowAny { TestQuery("1") }
        }

        @Test
        fun `Should not throw when supplying a list of non-blank string values`() {
            shouldNotThrowAny { TestQuery(listOf("1", "2")) }
        }

        @Test
        fun `Should throw when supplying an empty string`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery("")
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when supplying a string with spaces-only`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery("  ")
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when supplying a string with a comma`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery("foo,bar")
            }
            exception.message shouldBe "The supplied values may not contain separator characters"
        }

        @Test
        fun `Should throw when supplying a list of strings with 1 empty one`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery(listOf("1", ""))
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when supplying a list of strings with 1 with spaces-only`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery(listOf("  ", "2"))
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when supplying an empty value list`() {
            val exception = shouldThrow<IllegalArgumentException> {
                TestQuery(emptyList())
            }
            exception.message shouldBe "The supplied value list should at least have one item"
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Should properly convert the query to a QueryParameter for a single value`() {
            TestQuery("foo")
                .asQueryParameter() shouldBe QueryParameter(name = "test", value = "foo")
        }

        @Test
        fun `Should properly convert the query to a QueryParameter for multiple values`() {
            TestQuery(listOf("foo", "bar"))
                .asQueryParameter() shouldBe QueryParameter(name = "test", value = "foo,bar")
        }
    }
}

class StringQueryDerivedClassesTest {
    @Nested
    inner class Validation {
        @Test
        fun `Derived classes call the interface validation implementation`() {
            shouldThrow<IllegalArgumentException> { PropositionQuery(emptyList()) }
            shouldThrow<IllegalArgumentException> { ApplicationQuery(emptyList()) }
            shouldThrow<IllegalArgumentException> { SubscriptionQuery(emptyList()) }
            shouldThrow<IllegalArgumentException> { DataCategoryQuery(emptyList()) }
            shouldThrow<IllegalArgumentException> { DataSourceQuery(emptyList()) }
            shouldThrow<IllegalArgumentException> { DataItemIdQuery(emptyList()) }
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Derived classes call the interface method implementation`() {
            PropositionQuery("foo").asQueryParameter() shouldBe QueryParameter(name = "proposition", value = "foo")
            ApplicationQuery("foo").asQueryParameter() shouldBe QueryParameter(name = "application", value = "foo")
            SubscriptionQuery("foo").asQueryParameter() shouldBe QueryParameter(name = "subscription", value = "foo")
            DataCategoryQuery("foo").asQueryParameter() shouldBe QueryParameter(name = "dataCategory", value = "foo")
            DataSourceQuery("foo").asQueryParameter() shouldBe QueryParameter(name = "dataSource", value = "foo")
            DataItemIdQuery("foo").asQueryParameter() shouldBe QueryParameter(name = "_id", value = "foo")
        }
    }
}
