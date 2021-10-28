/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * The contract resource that describes the dynamic data for a given dataType.
 *
 * NOTE: resourceType is apparently not a mandatory field, but it is used here to support polymorphic (de)serialization
 * Ask HSDP if they can promote it to a mandatory field.
 */
@Serializable
@SerialName("Contract")
data class Contract(
    /**
     * The logical id of the resource. Once assigned, this value never changes.
     */
    override val id: String? = null,

    /**
     * The metadata about the resource. This is content that is maintained by the infrastructure.
     * Changes to the content may not always be associated with version changes to the resource.
     */
    override val meta: Meta? = null,

    /**
     * According to the HSDP API spec, the resourceType field should also be included in a Contract.
     * The deserialization requires a hint as to which data structure to decode to, and this is realized
     * by using the classDiscriminator, which will include the resourceType field to the serialized JSON
     * and use that to decode a JSON representation to a Contract.
     */

    /**
     * The JSON schema describing how the data belonging to this Contract looks.
     *
     * NOTE: according to API spec this is a string, but TDR returns an object.
     */
    val schema: JsonObject,

    /**
     * Representation of a known value in a system.
     */
    val dataType: Coding,

    /**
     * Textual representation of the TDR organization the DataItem belongs to.
     */
    val organization: String,

    /**
     * Use the HSDP Notification Service for sending notifications when POST or DELETE operations
     * are performed on DataItems for this Contract.
     */
    val sendNotifications: Boolean? = null,

    /**
     * TopicId of HSDP Notification Service associated with this contract. Note: This field cannot
     * be provided when posting a Contract, but will be filled by TDR when a Contract is created.
     */
    val notificationServiceTopicId: String? = null,

    /**
     * This policy specifies when the DataItem needs to be deleted.
     */
    val deletePolicy: DeletePolicy,
): Resource
