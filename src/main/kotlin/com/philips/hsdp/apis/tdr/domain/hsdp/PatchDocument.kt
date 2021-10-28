/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

/**
 * A JSON Patch operation as defined by RFC 6902.
 */
@Serializable
data class PatchDocument(
    /**
     * The operation to be performed.
     */
    val op: Operation,

    /**
     * A JSON-Pointer as defined by RFC 6901.
     */
    val path: String,

    /**
     * The value to be used within the operations.
     *
     * NOTE: according to the API spec it is a string, but when patching an integer value by passing in a string
     * here, TDR returns an error that the contract requires the value to be an integer.
     */
    val value: JsonPrimitive? = null,

    /**
     * A string containing a JSON Pointer value.
     */
    val from: String? = null,
)
