/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Result of the resource deletion.
 */
@Serializable
data class DeleteResponse(
    /**
     * The HTTP status returned by HSDP.
     */
    override val status: Int,

    /**
     * The response body string in either JSON or XML-format, depending on the set format.
     */
    val body: String? = null,

    /**
     * Version ID of the resource.
     */
    val versionId: String? = null,
) : CdrResponse
