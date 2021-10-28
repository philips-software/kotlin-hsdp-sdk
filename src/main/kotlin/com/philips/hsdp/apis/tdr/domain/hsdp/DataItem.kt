/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * The main resource in the system.
 *
 * NOTE: resourceType is apparently not a mandatory field, but it is used here to support polymorphic (de)serialization
 * Ask HSDP if they can promote it to a mandatory field.
 */
@Serializable
@SerialName("DataItem")
data class DataItem(
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
     * Observation timestamp in ISO 8601 format.
     */
    val timestamp: String,

    /**
     * To differentiate between DataItems with the same timestamp value, this attribute may be used
     * to give a unique sequence number to each individual DataItem.
     */
    val sequenceNumber: Int? = null,

    /**
     * A string that is associated with a single object or entity within a given system.
     * Typically, identifiers are used to connect content in resources to external content
     * available in other frameworks or protocols. If the identity is originating from HSDP's
     * Identity & Access Management service, the system should be left blank and the value can
     * either be the UUID of the IAM user or its (case-sensitive) username.
     */
    val device: Identifier? = null,

    /**
     * A string that is associated with a single object or entity within a given system.
     * Typically, identifiers are used to connect content in resources to external content
     * available in other frameworks or protocols. If the identity is originating from HSDP's
     * Identity & Access Management service, the system should be left blank and the value can
     * either be the UUID of the IAM user or its (case-sensitive) username.
     */
    val user: Identifier?= null,

    /**
     * A string that is associated with a single object or entity within a given system.
     * Typically, identifiers are used to connect content in resources to external content
     * available in other frameworks or protocols. If the identity is originating from HSDP's
     * Identity & Access Management service, the system should be left blank and the value can
     * either be the UUID of the IAM user or its (case-sensitive) username.
     */
    val relatedPeripheral: Identifier? = null,

    /**
     * A string that is associated with a single object or entity within a given system.
     * Typically, identifiers are used to connect content in resources to external content
     * available in other frameworks or protocols. If the identity is originating from HSDP's
     * Identity & Access Management service, the system should be left blank and the value can
     * either be the UUID of the IAM user or its (case-sensitive) username.
     */
    val relatedUser: Identifier? = null,

    /**
     * Representation of a known value in a system.
     */
    val dataType: Coding,

    /**
     * Textual representation of the TDR organization the DataItem belongs to.
     */
    val organization: String,

    /**
     * Textual representation of the application the DataItem belongs to.
     */
    val application: String? = null,

    /**
     * Textual representation of the proposition the DataItem belongs to.
     */
    val proposition: String? = null,

    /**
     * Textual representation of the logical grouping of observations the DataItem belongs to.
     */
    val subscription: String? = null,

    /**
     * The source the observation data is coming from. E.g. manual input, Fitbit, Withings, uGrow,
     * Air. This property cannot be set during a POST.
     */
    val dataSource: String? = null,

    /**
     * Textual representation of the category the DataItem belongs to.
     */
    val dataCategory: String? = null,

    /**
     * The JSON document described by the schema in the contract. If specified in the contract,
     * parts of the data can be searchable. The length of the value for each string type property
     * inside this data field is limited. Please contact HSDP Support for the exact limit for the
     * used environment.
     */
    val data: JsonObject? = null,

    /**
     * A larger object, not necessarily a JSON document. No search can be performed on the blob.
     */
    val blob: Blob? = null, // NOTE: the API spec does not mention that it is a base64 encoded string

    /**
     * Timestamp in ISO 8601 (UTC) format after which the DataItem will be removed. When a
     * DataItem is retrieved for which the deleteTimestamp has passed, a tombstoned DataItem
     * will be returned during the grace period. Note: This field cannot be provided when posting
     * a DataItem, but will be calculated by TDR when a DataItem is created, based on the
     * deletePolicy of the associated Contract.
     *
     * NOTE: the API spec indicates it is optional, but returned data items will always have a deleteTimestamp, right?!
     * This data structure is only used for retrieval, not for (batch) creation of data items.
     */
    val deleteTimestamp: String,

    /**
     * Timestamp in ISO 8601 (UTC) format of the time of creation of the DataItem.
     * Note: This field cannot be provided when posting a DataItem, but will be
     * calculated by TDR when a DataItem is created.
     *
     * NOTE: the API spec indicates it is optional, but returned data items will always have a deleteTimestamp, right?!
     * This data structure is only used for retrieval, not for (batch) creation of data items.
     */
    val creationTimestamp: String,

    /**
     * When true, the DataItem is tombstoned. The data and blob properties will not be returned
     * when a GET operation is performed on the DataItem. After the grace period, the DataItem
     * will be permanently deleted. To reverse the tombstoning process, perform a PATCH operation
     * on the DataItem and set the deteleTimestamp to a future moment. Note: This field cannot be
     * provided when posting a DataItem, but will be set by TDR when the deleteTimestamp has passed.
     */
    val tombstone: Boolean? = null,
): Resource
