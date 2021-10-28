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

class OrganizationQueryTest {
    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when supplying a single organization`() {
            shouldNotThrowAny { OrganizationQuery("Philips") }
        }

        @Test
        fun `Should throw when supplying multiple organizations`() {
            val exception = shouldThrow<IllegalArgumentException> {
                OrganizationQuery(listOf("Philips","Other"))
            }
            exception.message shouldBe "Specify only one organization"
        }

        @Test
        fun `Should throw when supplying an organization with a comma`() {
            val exception = shouldThrow<IllegalArgumentException> {
                OrganizationQuery("Philips,Other")
            }
            exception.message shouldBe "The supplied values may not contain separator characters"
        }

        @Test
        fun `Should throw when supplying an empty string`() {
            val exception = shouldThrow<IllegalArgumentException> {
                OrganizationQuery("")
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }

        @Test
        fun `Should throw when supplying a value with only spaces`() {
            val exception = shouldThrow<IllegalArgumentException> {
                OrganizationQuery("  ")
            }
            exception.message shouldBe "The supplied values may not be empty or only consist of spaces"
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Should properly convert the query to a QueryParameter`() {
            OrganizationQuery("foo")
                .asQueryParameter() shouldBe QueryParameter(name = "organization", value = "foo")
        }
    }
}