/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject

// NOTE: BatchCreateResponseBundle is required for the store-data-items endpoint, as that returns a status field in
// each entry in the entry field (which is not specified in the TDR API)

/**
 * Bundle that contains the batch creation results for data items.
 *
 * The Bundle that is returned by HSDP TDR is inconsistent with other places. As a result, a purpose-built
 * data structure is introduced to handle the result.
 */
@Serializable
data class BatchCreateResponseBundle(
    val type: String,
    // Note: as we don't need polymorphic resource deserialization here,
    // 'resourceType' is now part of the data structure.
    val resourceType: ResourceType,
    val total: Int,
    @Serializable(with = BatchCreateResponseBundleEntrySerializer::class)
    val entry: List<BatchCreateResponseBundleEntry>
)

/**
 * Serializer that assists in deserialization of a polymorphic list with [BatchCreateResponseBundleEntry] results.
 */
object BatchCreateResponseBundleEntrySerializer : JsonTransformingSerializer<List<BatchCreateResponseBundleEntry>>(
    ListSerializer(ResourceCreationResultWithStatusSerializer)
) {
    override fun transformDeserialize(element: JsonElement): JsonElement = element
}

/**
 * Serializer that assists in deserialization of a polymorphic [BatchCreateResponseBundleEntry] result.
 */
object ResourceCreationResultWithStatusSerializer : JsonContentPolymorphicSerializer<BatchCreateResponseBundleEntry>(
    BatchCreateResponseBundleEntry::class
) {
    override fun selectDeserializer(element: JsonElement) = when {
        "location" in element.jsonObject -> BatchCreatedResource.serializer()
        else -> BatchCreationFailure.serializer()
    }
}

sealed interface BatchCreateResponseBundleEntry {
    val status: Int
}

/**
 * A successfully created resource (data item) in a batch creation request.
 */
@Serializable
data class BatchCreatedResource(
    override val status: Int,
    val location: String,
    val etag: String,
    val lastModified: String,
) : BatchCreateResponseBundleEntry

/**
 * A failed creation of a resource (data item) in a batch creation request.
 */
@Serializable
data class BatchCreationFailure(
    override val status: Int,
    val outcome: CreationOutcome,
) : BatchCreateResponseBundleEntry

// NOTE: OperationOutcome cannot be used here, as batch create returns a different data structure than specified in the TDR API.
/**
 * Contains details on the issues that led to the creation failure of a resource.
 */
@Serializable
data class CreationOutcome(
    /**
     * List of issues that were reported by TDR when trying to create a resource.
     */
    val issue: List<Issue>,
    // Note: as we don't need polymorphic resource deserialization here,
    // 'resourceType' is now part of the data structure.
    val resourceType: ResourceType,
)
