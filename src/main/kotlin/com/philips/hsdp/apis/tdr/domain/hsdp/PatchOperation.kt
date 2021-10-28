/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

// NOTE: this is specified in the API spec, but seems to be used nowhere
/**
 * A JSON Patch operation as defined by RFC 6902.
 */
@Serializable
data class PatchOperation(
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
     */
    val value: String? = null,

    /**
     * A string containing a JSON Pointer value.
     */
    val from: String? = null,
)
