/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.conversion

import com.philips.hsdp.apis.provisioning.domain.hsdp.Identifier
import com.philips.hsdp.apis.provisioning.domain.hsdp.Parameter
import com.philips.hsdp.apis.provisioning.domain.hsdp.Parameters
import com.philips.hsdp.apis.provisioning.domain.hsdp.Reference
import com.philips.hsdp.apis.provisioning.domain.sdk.DeviceAttributes
import com.philips.hsdp.apis.provisioning.domain.sdk.DeviceIdentity
import com.philips.hsdp.apis.provisioning.domain.sdk.NewDeviceIdentity
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DeviceIdentityConversionKtTest {

    @Nested
    inner class NewDeviceIdentityToParameters {
        @Test
        fun `Convert a minimal NewDeviceIdentity to Parameters`() {
            val newDeviceIdentity = NewDeviceIdentity(
                type = "type",
                identityType = "device",
                deviceAttributes = DeviceAttributes(
                    systemIdentifier = "systemIdentifier",
                ),
                additionalAttributes = listOf(),
            )

            val expectedParameters = Parameters(
                parameter = listOf(
                    Parameter(name = "type", valueString = "type"),
                    Parameter(name = "identityType", valueString = "device"),
                    Parameter(
                        name = "DeviceAttributes", part = listOf(
                            Parameter(name = "systemIdentifier", valueString = "systemIdentifier"),
                        )
                    ),
                )
            )
            newDeviceIdentity.toParameters() shouldBe expectedParameters
        }

        @Test
        fun `Convert a more complex NewDeviceIdentity to Parameters`() {
            val newDeviceIdentity = NewDeviceIdentity(
                type = "type",
                identityType = "device",
                deviceAttributes = DeviceAttributes(
                    serialNumber = "serialNumber",
                    materialNumber = "materialNumber",
                    systemIdentifier = "systemIdentifier",
                    additionalAttributes = listOf(
                        Parameter(name = "nameTag", valueString = "TestHub_2225"),
                        Parameter(name = "parentNameTag", valueString = "PRS_RAM_IoTHub"),
                        Parameter(name = "someLong", valueDecimal = 3L),
                        Parameter(
                            name = "someReference", valueReference = Reference(
                                reference = "reference",
                                display = "display",
                            )
                        ),
                        Parameter(name = "someDateTime", valueDateTime = "2021-09-20T13:14:15.000Z"),
                    ),
                ),
                additionalAttributes = listOf(
                    Parameter(name = "someBoolean", valueBoolean = true),
                    Parameter(name = "someInt", valueInteger = 2),
                    Parameter(name = "someCode", valueCode = "xxx|yyy"),
                    Parameter(
                        name = "someIdentifier", valueIdentifier = Identifier(
                            system = "system",
                            value = "value",
                        )
                    ),
                    Parameter(name = "someUri", valueUri = "uri"),
                )
            )

            val expectedParameters = Parameters(
                parameter = listOf(
                    Parameter(name = "type", valueString = "type"),
                    Parameter(name = "identityType", valueString = "device"),
                    Parameter(
                        name = "DeviceAttributes", part = listOf(
                            Parameter(name = "serialNumber", valueString = "serialNumber"),
                            Parameter(name = "materialNumber", valueString = "materialNumber"),
                            Parameter(name = "systemIdentifier", valueString = "systemIdentifier"),
                            Parameter(name = "nameTag", valueString = "TestHub_2225"),
                            Parameter(name = "parentNameTag", valueString = "PRS_RAM_IoTHub"),
                            Parameter(name = "someLong", valueDecimal = 3L),
                            Parameter(
                                name = "someReference", valueReference = Reference(
                                    reference = "reference",
                                    display = "display",
                                )
                            ),
                            Parameter(name = "someDateTime", valueDateTime = "2021-09-20T13:14:15.000Z"),
                        )
                    ),
                    Parameter(name = "someBoolean", valueBoolean = true),
                    Parameter(name = "someInt", valueInteger = 2),
                    Parameter(name = "someCode", valueCode = "xxx|yyy"),
                    Parameter(
                        name = "someIdentifier", valueIdentifier = Identifier(
                            system = "system",
                            value = "value",
                        )
                    ),
                    Parameter(name = "someUri", valueUri = "uri"),
                )
            )
            newDeviceIdentity.toParameters() shouldBe expectedParameters
        }
    }

    @Nested
    inner class ParametersToDeviceIdentity {
        @Test
        fun `Convert a minimal Parameters to DeviceIdentity`() {
            val parameters = Parameters(
                parameter = listOf(
                    Parameter(name = "type", valueString = "type"),
                    Parameter(name = "identityType", valueString = "device"),
                    Parameter(name = "identitySignature", valueString = "identitySignature"),
                    Parameter(name = "loginId", valueString = "loginId"),
                    Parameter(name = "password", valueString = "password"),
                    Parameter(name = "HSDPId", valueString = "HSDPId"),
                    Parameter(name = "OAuthClientId", valueString = "OAuthClientId"),
                    Parameter(name = "OAuthClientSecret", valueString = "OAuthClientSecret"),
                    Parameter(name = "DeviceAttributes", part = listOf(
                        Parameter(name = "systemIdentifier", valueString = "systemIdentifier"),
                    )),
                )
            )
            val expectedDeviceIdentity = DeviceIdentity(
                type = "type",
                identityType = "device",
                identitySignature = "identitySignature",
                loginId = "loginId",
                password = "password",
                hsdpId = "HSDPId",
                oauthClientId = "OAuthClientId",
                oauthClientSecret = "OAuthClientSecret",
                deviceAttributes = DeviceAttributes(
                    systemIdentifier = "systemIdentifier",
                ),
            )
            parameters.toDeviceIdentity() shouldBe expectedDeviceIdentity
        }

        @Test
        fun `Convert a more complex Parameters to DeviceIdentity`() {
            val parameters = Parameters(
                parameter = listOf(
                    Parameter(name = "type", valueString = "type"),
                    Parameter(name = "identityType", valueString = "device"),
                    Parameter(name = "identitySignature", valueString = "identitySignature"),
                    Parameter(name = "loginId", valueString = "loginId"),
                    Parameter(name = "password", valueString = "password"),
                    Parameter(name = "HSDPId", valueString = "HSDPId"),
                    Parameter(name = "OAuthClientId", valueString = "OAuthClientId"),
                    Parameter(name = "OAuthClientSecret", valueString = "OAuthClientSecret"),
                    Parameter(name = "DeviceAttributes", part = listOf(
                        Parameter(name = "serialNumber", valueString = "serialNumber"),
                        Parameter(name = "materialNumber", valueString = "materialNumber"),
                        Parameter(name = "systemIdentifier", valueString = "systemIdentifier"),
                        Parameter(name = "nameTag", valueString = "TestHub_2225"),
                        Parameter(name = "parentNameTag", valueString = "PRS_RAM_IoTHub"),
                        Parameter(name = "someLong", valueDecimal = 3L),
                        Parameter(
                            name = "someReference", valueReference = Reference(
                                reference = "reference",
                                display = "display",
                            )
                        ),
                        Parameter(name = "someDateTime", valueDateTime = "2021-09-20T13:14:15.000Z"),
                    )),
                    Parameter(name = "someBoolean", valueBoolean = true),
                    Parameter(name = "someInt", valueInteger = 2),
                    Parameter(name = "someCode", valueCode = "xxx|yyy"),
                    Parameter(
                        name = "someIdentifier", valueIdentifier = Identifier(
                            system = "system",
                            value = "value",
                        )
                    ),
                    Parameter(name = "someUri", valueUri = "uri"),
                )
            )
            val expectedDeviceIdentity = DeviceIdentity(
                type = "type",
                identityType = "device",
                identitySignature = "identitySignature",
                loginId = "loginId",
                password = "password",
                hsdpId = "HSDPId",
                oauthClientId = "OAuthClientId",
                oauthClientSecret = "OAuthClientSecret",
                deviceAttributes = DeviceAttributes(
                    serialNumber = "serialNumber",
                    materialNumber = "materialNumber",
                    systemIdentifier = "systemIdentifier",
                    additionalAttributes = listOf(
                        Parameter(name = "nameTag", valueString = "TestHub_2225"),
                        Parameter(name = "parentNameTag", valueString = "PRS_RAM_IoTHub"),
                        Parameter(name = "someLong", valueDecimal = 3L),
                        Parameter(
                            name = "someReference", valueReference = Reference(
                                reference = "reference",
                                display = "display",
                            )
                        ),
                        Parameter(name = "someDateTime", valueDateTime = "2021-09-20T13:14:15.000Z"),
                    ),
                ),
                additionalAttributes = listOf(
                    Parameter(name = "someBoolean", valueBoolean = true),
                    Parameter(name = "someInt", valueInteger = 2),
                    Parameter(name = "someCode", valueCode = "xxx|yyy"),
                    Parameter(
                        name = "someIdentifier", valueIdentifier = Identifier(
                            system = "system",
                            value = "value",
                        )
                    ),
                    Parameter(name = "someUri", valueUri = "uri"),
                ),
            )
            parameters.toDeviceIdentity() shouldBe expectedDeviceIdentity
        }
    }
}