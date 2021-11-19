/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Result of the resource patch.
 */
@Serializable
data class PatchResponse(
    /**
     * The HTTP status returned by HSDP.
     */
    override val status: Int,

    /**
     * The response body string in either JSON or XML-format, depending on the set format.
     */
    val body: String,

    /**
     * URL of the patched resource.
     */
    val location: String? = null,

    /**
     * Version ID of the resource.
     */
    val versionId: String,

    /**
     * Timestamp of last modification.
     */
    val lastModified: String,
) : CdrResponse
