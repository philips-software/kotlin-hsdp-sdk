/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import com.philips.hsdp.apis.tdr.domain.hsdp.Blob
import com.philips.hsdp.apis.tdr.domain.hsdp.Coding
import com.philips.hsdp.apis.tdr.domain.hsdp.Identifier
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

internal class NewDataItemDtoTest {
    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when supplying the data field`() {
            shouldNotThrowAny {
                NewDataItemDto(
                    timestamp = "2021-08-28T14:15:16.000Z",
                    user = Identifier(
                        system = "s1",
                        value = "v1"
                    ),
                    dataType = Coding(
                        system = "s2",
                        code = "c2",
                    ),
                    organization = "org1",
                    data = JsonObject(emptyMap()),
                )
            }
        }

        @Test
        fun `Should not throw when supplying the blob field`() {
            shouldNotThrowAny {
                NewDataItemDto(
                    timestamp = "2021-08-28T14:15:16.000Z",
                    user = Identifier(
                        system = "s1",
                        value = "v1"
                    ),
                    dataType = Coding(
                        system = "s2",
                        code = "c2",
                    ),
                    organization = "org1",
                    blob = Blob(byteArrayOf(21, 22, 23, 24, 25)),
                )
            }
        }

        @Test
        fun `Should not throw when supplying the data and blob field`() {
            shouldNotThrowAny {
                NewDataItemDto(
                    timestamp = "2021-08-28T14:15:16.000Z",
                    user = Identifier(
                        system = "s1",
                        value = "v1"
                    ),
                    dataType = Coding(
                        system = "s2",
                        code = "c2",
                    ),
                    organization = "org1",
                    data = JsonObject(emptyMap()),
                    blob = Blob(byteArrayOf(21, 22, 23, 24, 25)),
                )
            }
        }

        @Test
        fun `Should not throw when supplying a ISO8601 timestamp without milliseconds`() {
            shouldNotThrowAny {
                NewDataItemDto(
                    timestamp = "2021-08-28T14:15:16Z",
                    user = Identifier(
                        system = "s1",
                        value = "v1"
                    ),
                    dataType = Coding(
                        system = "s2",
                        code = "c2",
                    ),
                    organization = "org1",
                    data = JsonObject(emptyMap()),
                )
            }
        }

        @Test
        fun `Should throw when not supplying neither the data nor the blob field`() {
            val exception = shouldThrow<IllegalArgumentException> {
                NewDataItemDto(
                    timestamp = "2021-08-28T14:15:16.000Z",
                    user = Identifier(
                        system = "s1",
                        value = "v1"
                    ),
                    dataType = Coding(
                        system = "s2",
                        code = "c2",
                    ),
                    organization = "org1",
                )
            }
            exception.message shouldBe "At least one of the fields data and blob must be supplied"
        }

        @Test
        fun `Should throw when not supplying an ISO8601 timestamp`() {
            val exception = shouldThrow<IllegalArgumentException> {
                NewDataItemDto(
                    timestamp = "2021-08-28 14:15:16",
                    user = Identifier(
                        system = "s1",
                        value = "v1"
                    ),
                    dataType = Coding(
                        system = "s2",
                        code = "c2",
                    ),
                    organization = "org1",
                    data = JsonObject(emptyMap()),
                )
            }
            exception.message shouldBe "Timestamp must be in ISO8601 format (including milliseconds)"
        }
    }
}
