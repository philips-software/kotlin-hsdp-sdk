/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import com.philips.hsdp.apis.tdr.domain.hsdp.Issue
import kotlinx.serialization.Serializable

sealed interface ResourceCreationResultDto

/**
 * A successfully created resource in TDR, be it a contract or a data item.
 */
@Serializable
data class CreatedResource(
    /**
     * The url to the resource.
     */
    val location: String,
    /**
     * The version of the resource, corresponds to versionId in the
     * [Meta][com.philips.hsdp.apis.tdr.domain.hsdp.Meta] object.
     */
    val etag: String,
    /**
     * The timestamp of last modification, corresponds to lastModified in the
     * [Meta][com.philips.hsdp.apis.tdr.domain.hsdp.Meta] object.
     */
    val lastModified: String,
): ResourceCreationResultDto

/**
 * Represents a resource creation failure, be it for a contract of data item.
 */
@Serializable
data class CreationFailure(
    /**
     * List of issues that were reported by TDR when trying to create a resource.
     */
    val issues: List<Issue>,
): ResourceCreationResultDto

