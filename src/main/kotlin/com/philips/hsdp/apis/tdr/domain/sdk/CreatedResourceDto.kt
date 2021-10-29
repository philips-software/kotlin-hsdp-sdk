/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

/**
 * Response for a resource creation request, be it a contract or a data item.
 */
@Serializable
data class CreatedResourceDto(
    /**
     * Resource creation result, either a success or failure.
     */
    @Serializable(with = ResourceCreationResultDtoSerializer::class)
    val resource: ResourceCreationResultDto,
    /**
     * Identifier that can be used for tracing requests in a flow.
     */
    val requestId: String,
)

/**
 * Serializer that assists in (de)serializing polymorphic [ResourceCreationResultDto] data structures.
 */
object ResourceCreationResultDtoSerializer : JsonContentPolymorphicSerializer<ResourceCreationResultDto>(
    ResourceCreationResultDto::class
) {
    override fun selectDeserializer(element: JsonElement) = when {
        "location" in element.jsonObject -> CreatedResource.serializer()
        else -> CreationFailure.serializer()
    }
}
