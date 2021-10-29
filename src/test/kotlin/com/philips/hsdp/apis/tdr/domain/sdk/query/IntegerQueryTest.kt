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

class IntegerQueryTest {
    data class TestQuery(override val value: List<Int>) : IntegerQuery {
        override val name = "test"

        constructor(value: Int) : this(listOf(value))

        init {
            validate()
        }
    }

    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when supplying a single value`() {
            shouldNotThrowAny { TestQuery(1) }
        }

        @Test
        fun `Should not throw when supplying multiple values`() {
            shouldNotThrowAny { TestQuery(listOf(1, 2)) }
        }

        @Test
        fun `Should throw when value list is empty`() {
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
            TestQuery(1)
                .asQueryParameter() shouldBe QueryParameter(name = "test", value = "1")
        }

        @Test
        fun `Should properly convert the query to a QueryParameter for multiple values`() {
            TestQuery(listOf(1, 2))
                .asQueryParameter() shouldBe QueryParameter(name = "test", value = "1,2")
        }
    }
}

class IntegerQueryDerivedClassesTest {
    @Nested
    inner class Validation {
        @Test
        fun `Derived classes call the interface validation implementation`() {
            shouldThrow<IllegalArgumentException> { SequenceNumberQuery(emptyList()) }
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Derived classes call the interface method implementation`() {
            SequenceNumberQuery(10)
                .asQueryParameter() shouldBe QueryParameter(name = "sequenceNumber", value = "10")
        }
    }
}
