/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * An entry in a bundle.
 */
@Serializable
data class BundleEntry(
    /**
     * Absolute URL for retrieving the resource.
     */
    val fullUrl: String? = null,

    /**
     * Base Resource. This is not an entity in itself. However, root resources (currently only
     * DataItem and contract) that extend from the base resource inherit the properties defined
     * here.
     *
     * NOTE: according to API spec. this field is optional; does that make any sense?
     */
    val resource: Resource,

    /**
     * A response containing the output for a single operation.
     *
     * NOTE: according to API spec, this field is can be part of a bundle entry, but I never see this used in the
     * returned data from the various API calls. It seems like OperationResponse would more be like a Resource
     * implementation, but then a field resourceType should be added (for polymorphic deserialization)
     */
    val response: OperationResponse? = null,
)
