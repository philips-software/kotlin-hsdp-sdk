/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.conversion

import com.philips.hsdp.apis.provisioning.domain.hsdp.Parameter
import com.philips.hsdp.apis.provisioning.domain.hsdp.Parameters
import com.philips.hsdp.apis.provisioning.domain.sdk.DeviceAttributes
import com.philips.hsdp.apis.provisioning.domain.sdk.DeviceExtId
import com.philips.hsdp.apis.provisioning.domain.sdk.DeviceIdentity
import com.philips.hsdp.apis.provisioning.domain.sdk.NewDeviceIdentity
import com.philips.hsdp.apis.provisioning.domain.sdk.Type

/**
 * Convert a [NewDeviceIdentity] to a [Parameters][com.philips.hsdp.apis.provisioning.domain.hsdp.Parameters] structure
 * that is required by HSDP Provisioning API for creation of a new identity.
 */
fun NewDeviceIdentity.toParameters(): Parameters =
    Parameters(
        parameter = listOfNotNull(
            Parameter(name = "type", valueString = type),
            Parameter(name = "identityType", valueString = identityType),
            Parameter(name = "DeviceAttributes", part = deviceAttributes.toParameterList()),
        ) + (additionalAttributes ?: emptyList()),
    )

/**
 * Convert a [DeviceAttributes] to a [Parameter][com.philips.hsdp.apis.provisioning.domain.hsdp.Parameter] list that is
 * required by HSDP Provisioning API for creation of a new identity.
 */
fun DeviceAttributes.toParameterList(): List<Parameter> =
    listOfNotNull(
        serialNumber?.let { Parameter(name = "serialNumber", valueString = it) },
        materialNumber?.let { Parameter(name = "materialNumber", valueString = it) },
        systemIdentifier?.let { Parameter(name = "systemIdentifier", valueString = it) },
//         deviceExtId?.let {
//             Parameter(name = "deviceExtId", resource = DeviceExtId()) // TODO: does not yet fit
//         }
    ) + (additionalAttributes ?: emptyList())

/**
 * Convert a [Parameters][com.philips.hsdp.apis.provisioning.domain.hsdp.Parameters] response from HSDP Provisioning API
 * to a [DeviceIdentity] that is returned to the SDK-user.
 */
fun Parameters.toDeviceIdentity(): DeviceIdentity {
    val propertyNamesForOwnFields = listOf(
        "type", "identityType", "identitySignature", "loginId", "password",
        "HSDPId", "OAuthClientId", "OAuthClientSecret", "DeviceAttributes"
    )
    val (ownProperties, extraProperties) = parameter.partition { it.name in propertyNamesForOwnFields }
    return with(ownProperties) {
        DeviceIdentity(
            type = find { it.name == "type" }?.valueString!!,
            identityType = find { it.name == "identityType" }?.valueString!!,
            identitySignature = find { it.name == "identitySignature" }?.valueString!!,
            loginId = find { it.name == "loginId" }?.valueString!!,
            password = find { it.name == "password" }?.valueString!!,
            hsdpId = find { it.name == "HSDPId" }?.valueString!!,
            oauthClientId = find { it.name == "OAuthClientId" }?.valueString!!,
            oauthClientSecret = find { it.name == "OAuthClientSecret" }?.valueString!!,
            deviceAttributes = find { it.name == "DeviceAttributes" }?.part?.toDeviceAttributes(),
            additionalAttributes = extraProperties.ifEmpty { null },
        )
    }
}

/**
 * Convert a [Parameter][com.philips.hsdp.apis.provisioning.domain.hsdp.Parameter] list (part of response from HSDP
 * Provisioning API) to [DeviceAttributes] that is returned as part of [DeviceIdentity] to the SDK-user.
 */
fun List<Parameter>.toDeviceAttributes(): DeviceAttributes {
    val propertyNamesForOwnFields = listOf("materialNumber", "serialNumber", "systemIdentifier", "deviceExtId")
    val (ownProperties, extraProperties) = partition { it.name in propertyNamesForOwnFields }
    return with(ownProperties) {
        DeviceAttributes(
            materialNumber = find { it.name == "materialNumber" }?.valueString,
            serialNumber = find { it.name == "serialNumber" }?.valueString,
            systemIdentifier = find { it.name == "systemIdentifier" }?.valueString,
            deviceExtId = find { it.name == "deviceExtId" }?.part?.toDeviceExtId(),
            additionalAttributes = extraProperties.ifEmpty { null },
        )
    }
}

/**
 * Convert a [Parameter][com.philips.hsdp.apis.provisioning.domain.hsdp.Parameter] list (part of response from HSDP
 * Provisioning API) to [DeviceExtId] that is returned as part of [DeviceAttributes] to the SDK-user.
 */
fun List<Parameter>.toDeviceExtId(): DeviceExtId =
    DeviceExtId(
        system = find { it.name == "system" }?.valueString!!,
        value = find { it.name == "value" }?.valueString!!,
        type = find { it.name == "type" }?.valueString?.let { Type(it, "TODO") }!!,
    )
