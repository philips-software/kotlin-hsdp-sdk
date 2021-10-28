/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import com.philips.hsdp.apis.provisioning.ProvisioningService
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test

internal class ParametersSerializationTest {
    private val simpleParameters: Resource = Parameters(
        parameter = listOf(
            Parameter(name = "type", valueString = "IoTPRS"),
            Parameter(name = "identityType", valueString = "device"),
            Parameter(name = "DeviceAttributes", part = listOf(
                Parameter(name = "materialNumber", valueString = "123456"),
                Parameter(name = "serialNumber", valueString = "abc1234"),
                Parameter(name = "nameTag", valueString = "TestHub_2225"),
                Parameter(name = "parentNameTag", valueString = "PRS_RAM_IoTHub"),
            )),
        )
    )

    private val simpleJsonRepresentation = """{
        "resourceType": "Parameters",
        "parameter": [
            {
                "name": "type",
                "valueString": "IoTPRS"
            },
            {
                "name": "identityType",
                "valueString": "device"
            },
            {
                "name": "DeviceAttributes",
                "part": [
                    {
                        "name": "materialNumber",
                        "valueString": "123456"
                    },
                    {
                        "name": "serialNumber",
                        "valueString": "abc1234"
                    },
                    {
                        "name": "nameTag",
                        "valueString": "TestHub_2225"
                    },
                    {
                        "name": "parentNameTag",
                        "valueString": "PRS_RAM_IoTHub"
                    }
                ]
            }
        ]
    }""".replace(Regex("\\s"), "")

    private val complexParameters: Resource = Parameters(
        id = "id",
        meta = Meta("2021-09-20T12:13:14.000Z", "1"),
        parameter = listOf(
            Parameter(name = "type", valueString = "IoTPRS"),
            Parameter(name = "identityType", valueString = "device"),
            Parameter(name = "someBoolean", valueBoolean = true),
            Parameter(name = "someInt", valueInteger = 2),
            Parameter(name = "someCode", valueCode = "xxx|yyy"),
            Parameter(name = "someIdentifier", valueIdentifier = Identifier(
                system = "system",
                value = "value",
            )),
            Parameter(name = "someUri", valueUri = "uri"),
            Parameter(name = "DeviceAttributes", part = listOf(
                Parameter(name = "materialNumber", valueString = "123456"),
                Parameter(name = "serialNumber", valueString = "abc1234"),
                Parameter(name = "nameTag", valueString = "TestHub_2225"),
                Parameter(name = "parentNameTag", valueString = "PRS_RAM_IoTHub"),
                Parameter(name = "someLong", valueDecimal = 3L),
                Parameter(name = "someReference", valueReference = Reference(
                    reference = "reference",
                    display = "display",
                )
                ),
                Parameter(name = "someDateTime", valueDateTime = "2021-09-20T13:14:15.000Z"),
            )),
        )
    )

    private val complexJsonRepresentation = """{
        "resourceType": "Parameters",
        "id": "id",
        "meta": {
            "lastUpdated": "2021-09-20T12:13:14.000Z",
            "versionId": "1"
        },
        "parameter": [
            {
                "name": "type",
                "valueString": "IoTPRS"
            },
            {
                "name": "identityType",
                "valueString": "device"
            },
            {
                "name": "someBoolean",
                "valueBoolean": true
            },
            {
                "name": "someInt",
                "valueInteger": 2
            },
            {
                "name": "someCode",
                "valueCode": "xxx|yyy"
            },
            {
                "name": "someIdentifier",
                "valueIdentifier": {
                    "system": "system",
                    "value": "value"
                }
            },
            {
                "name": "someUri",
                "valueUri": "uri"
            },
            {
                "name": "DeviceAttributes",
                "part": [
                    {
                        "name": "materialNumber",
                        "valueString": "123456"
                    },
                    {
                        "name": "serialNumber",
                        "valueString": "abc1234"
                    },
                    {
                        "name": "nameTag",
                        "valueString": "TestHub_2225"
                    },
                    {
                        "name": "parentNameTag",
                        "valueString": "PRS_RAM_IoTHub"
                    },
                    {
                        "name": "someLong",
                        "valueDecimal": 3
                    },
                    {
                        "name": "someReference",
                        "valueReference": {
                            "reference": "reference",
                            "display": "display"
                        }
                    },
                    {
                        "name": "someDateTime",
                        "valueDateTime": "2021-09-20T13:14:15.000Z"
                    }
                ]
            }
        ]
    }""".replace(Regex("\\s"), "")

    @Test
    fun `Returns expected json string when serializing a simple Parameters structure`() {
        val result = ProvisioningService.json.encodeToString(simpleParameters)
        result shouldBe simpleJsonRepresentation
    }

    @Test
    fun `Returns expected object when deserializing a JSON string that represents a simple Parameters structure`() {
        val result: Resource = ProvisioningService.json.decodeFromString(simpleJsonRepresentation)
        result shouldBe simpleParameters
    }
    @Test
    fun `Returns expected json string when serializing a complex Parameters structure`() {
        val result = ProvisioningService.json.encodeToString(complexParameters)
        result shouldBe complexJsonRepresentation
    }

    @Test
    fun `Returns expected object when deserializing a JSON string that represents a complex Parameters structure`() {
        val result: Resource = ProvisioningService.json.decodeFromString(complexJsonRepresentation)
        result shouldBe complexParameters
    }
}
