/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * Contains the response for the creation of multiple data items.
 */
@Serializable
data class CreatedDataItemsDto(
    /**
     * List of data item creation results. The results are in the same order as the list of data items that was
     * to be created. A result can be a success or a failure.
     */
    @Serializable(with = BatchResourceCreationResultDtoSerializer::class)
    val dataItems: List<ResourceCreationResultDto>,
    /**
     * Identifier that can be used for tracing requests in a flow.
     */
    val requestId: String,
)

/**
 * Serializer that assists in serialization of a polymorphic list with [ResourceCreationResultDto] results.
 */
object BatchResourceCreationResultDtoSerializer : JsonTransformingSerializer<List<ResourceCreationResultDto>>(
    ListSerializer(ResourceCreationResultDtoSerializer)
) {
    override fun transformSerialize(element: JsonElement): JsonElement = element
}
