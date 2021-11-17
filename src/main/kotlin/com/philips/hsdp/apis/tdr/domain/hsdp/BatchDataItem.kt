/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * The main resource in the system.
 */
@Serializable
data class BatchDataItem(
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
    val user: Identifier? = null,

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
     * A larger object, not necessarily a JSON document. No search can be performed on data in the blob.
     */
    val blob: Blob? = null, // NOTE: the API spec does not mention that it is a base64 encoded string
)
