/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Metadata about a resource.
 */
@Serializable
data class Meta(
    /**
     * The date and time the resource was created. In ISO 8601 (UTC) format.
     */
    val lastUpdated: String,

    /**
     * Version ID of the resource. Representation of this value in ETag should be W/"versionId"
     * where the versionId for Provisioning is an integer.
     */
    val versionId: String,
)
