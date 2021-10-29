/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class BlobJsonTest {
    @Serializable
    data class Wrapper(val blob: Blob? = null)

    @Test
    fun `Should serialize and deserialize properly when passing an instance with a blob field`() {
        val blob = Blob(byteArrayOf(21, 22, 23, 24, 25))
        val jsonRepresentation = Json.encodeToString(Wrapper(blob))
        jsonRepresentation shouldBe """{"blob":"FRYXGBk="}"""
        val d = Json.decodeFromString<Wrapper>(jsonRepresentation)
        d shouldBe Wrapper(blob)
    }

    @Test
    fun `Should serialize and deserialize properly when passing no blob field`() {
        val jsonRepresentation = Json.encodeToString(Wrapper())
        jsonRepresentation shouldBe """{}"""
        val d = Json.decodeFromString<Wrapper>(jsonRepresentation)
        d shouldBe Wrapper()
    }
}
