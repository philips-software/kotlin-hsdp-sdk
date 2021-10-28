/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr

import com.philips.hsdp.apis.tdr.domain.hsdp.Meta
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MetaTest {
    @Nested
    inner class Validation {
        @Test
        fun check() {
            val exception = shouldThrow<IllegalArgumentException> {
                Meta("", "")
            }

            exception.message shouldBe "lastUpdated is not in ISO 8601 (UTC) format"
        }
    }
}
