/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class TimestampQueryTest {
    @Nested
    inner class Validation {
        @TestFactory
        fun `Valid timestamp queries using ISO8601 UTC timestamp and optionally a prefix`() = listOf(
            "2021-07-06T12:13:14.023Z" to "out prefix",
            "2021-07-06T12:13:14.023Z" to "out prefix with milliseconds",
            "eq2021-07-06T12:13:14Z" to " eq prefix",
            "ne2021-07-06T12:13:14Z" to " ne prefix",
            "lt2021-07-06T12:13:14Z" to " lt prefix",
            "le2021-07-06T12:13:14Z" to " le prefix",
            "gt2021-07-06T12:13:14Z" to " gt prefix",
            "ge2021-07-06T12:13:14Z" to " ge prefix",
        ).map { (input, scenario) ->
            DynamicTest.dynamicTest("Should not throw when supplying a timestamp with$scenario") {
                shouldNotThrowAny { TimestampQuery(input) }
            }
        }

        @TestFactory
        fun `Should throw when supplying a timestamp with space between date and time`() = listOf(
            "2021-07-06 12:13:14.000Z" to "a space between date and time",
            "2021-07-06T12:13:14.000" to "no Z after the time",
        ).map { (input, scenario) ->
            DynamicTest.dynamicTest("Should not throw when supplying a timestamp with $scenario") {
                val exception = shouldThrow<IllegalArgumentException> {
                    TimestampQuery(input)
                }
                exception.message shouldBe "Timestamp not according to ISO8601 ISO format with optional operator prefix"
            }
        }
    }

    @Nested
    inner class AsQueryParameter {
        @Test
        fun `Should properly convert the query to a QueryParameter for a single value`() {
            TimestampQuery("2021-07-06T12:13:14Z")
                .asQueryParameter() shouldBe QueryParameter(
                name = "timestamp",
                value = "2021-07-06T12:13:14Z"
            )
        }

        @Test
        fun `Should properly convert the query to a QueryParameter for multiple values`() {
            TimestampQuery(
                listOf(
                    "2021-07-06T12:13:14Z",
                    "2021-07-07T12:13:14.000Z"
                )
            ).asQueryParameter() shouldBe QueryParameter(
                name = "timestamp",
                value = "2021-07-06T12:13:14Z,2021-07-07T12:13:14.000Z"
            )
        }
    }
}
