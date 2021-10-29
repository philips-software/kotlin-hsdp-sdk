/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

internal class ValidationPatternsTest {
    @Nested
    inner class Iso8601UtcPattern {
        @TestFactory
        fun `Supplying valid timestamp strings`() = listOf(
            "2021-10-05T12:13:14.000Z" to listOf("2021-10-05T12:13:14.000Z", "2021", "10", "05", "12", "13", "14", ".000", "Z"),
            "2021-10-05T12:13:14.000+01:00" to listOf("2021-10-05T12:13:14.000+01:00", "2021", "10", "05", "12", "13", "14", ".000", "+01:00"),
            "2021-10-05T12:13:14.000-01:00" to listOf("2021-10-05T12:13:14.000-01:00", "2021", "10", "05", "12", "13", "14", ".000", "-01:00"),
            "2021-10-05T12:13:14Z" to listOf("2021-10-05T12:13:14Z", "2021", "10", "05", "12", "13", "14", "", "Z"),
            "2021-10-05T12:13:14+01:00" to listOf("2021-10-05T12:13:14+01:00", "2021", "10", "05", "12", "13", "14", "", "+01:00"),
            "2021-10-05T12:13:14-01:00" to listOf("2021-10-05T12:13:14-01:00", "2021", "10", "05", "12", "13", "14", "", "-01:00"),
        )
            .map { (input, expected) ->
                DynamicTest.dynamicTest("Should match when providing a valid timestamp $input") {
                    val result = ValidationPatterns.iso8601UtcPattern.matchEntire(input)
                    result shouldNotBe null
                    result!!.groupValues shouldBe expected
                }
            }

        @TestFactory
        fun `Supplying invalid timestamp strings`() = listOf(
            "2021-10-05T12:13:14.000",
            "2021-10-05T12:13:14.00",
            "2021-10-05T12:13:14.00+01:00",
            "2021-10-05T12:13:14.00-01:00",
            "2021-10-05T12:13:14.0",
            "2021-10-05T12:13:14.0+01:00",
            "2021-10-05T12:13:14.0-01:00",
            "2021-10-05T12:13:14.",
            "2021-10-05T12:13:14.+01:00",
            "2021-10-05T12:13:14.-01:00",
            "2021-10-05T12:13:14",
            "2021-10-05 12:13:14Z",
            "2021-10-05 12:13:14+01:00",
            "2021-10-05 12:13:14-01:00",
        )
            .map { input ->
                DynamicTest.dynamicTest("Should fail when providing an invalid timestamp $input") {
                    val result = ValidationPatterns.iso8601UtcPattern.matchEntire(input)
                    result shouldBe null
                }
            }
    }

    @Nested
    inner class Iso8601UtcPlusOperatorPattern {
        @TestFactory
        fun `Supplying valid timestamp strings`() = listOf(
            "eq2021-10-05T12:13:14.000Z" to listOf("eq2021-10-05T12:13:14.000Z", "eq", "2021", "10", "05", "12", "13", "14", ".000", "Z"),
            "ne2021-10-05T12:13:14.000+01:00" to listOf("ne2021-10-05T12:13:14.000+01:00", "ne", "2021", "10", "05", "12", "13", "14", ".000", "+01:00"),
            "le2021-10-05T12:13:14.000-01:00" to listOf("le2021-10-05T12:13:14.000-01:00", "le", "2021", "10", "05", "12", "13", "14", ".000", "-01:00"),
            "ge2021-10-05T12:13:14Z" to listOf("ge2021-10-05T12:13:14Z", "ge", "2021", "10", "05", "12", "13", "14", "", "Z"),
            "lt2021-10-05T12:13:14+01:00" to listOf("lt2021-10-05T12:13:14+01:00", "lt", "2021", "10", "05", "12", "13", "14", "", "+01:00"),
            "gt2021-10-05T12:13:14-01:00" to listOf("gt2021-10-05T12:13:14-01:00", "gt", "2021", "10", "05", "12", "13", "14", "", "-01:00"),
        )
            .map { (input, expected) ->
                DynamicTest.dynamicTest("Should match when providing a valid timestamp $input") {
                    val result = ValidationPatterns.iso8601UtcPlusOperatorPattern.matchEntire(input)
                    result shouldNotBe null
                    result!!.groupValues shouldBe expected
                }
            }

        @TestFactory
        fun `Supplying invalid timestamp strings`() = listOf(
            "eq2021-10-05T12:13:14.000",
            "eq2021-10-05T12:13:14.00",
            "eq2021-10-05T12:13:14.00+01:00",
            "ne2021-10-05T12:13:14.00-01:00",
            "ne2021-10-05T12:13:14.0",
            "ne2021-10-05T12:13:14.0+01:00",
            "le2021-10-05T12:13:14.0-01:00",
            "le2021-10-05T12:13:14.",
            "ge2021-10-05T12:13:14.+01:00",
            "ge2021-10-05T12:13:14.-01:00",
            "lt2021-10-05T12:13:14",
            "lt2021-10-05 12:13:14Z",
            "gt2021-10-05 12:13:14+01:00",
            "gt2021-10-05 12:13:14-01:00",
        )
            .map { input ->
                DynamicTest.dynamicTest("Should fail when providing an invalid timestamp $input") {
                    val result = ValidationPatterns.iso8601UtcPattern.matchEntire(input)
                    result shouldBe null
                }
            }
    }
}
