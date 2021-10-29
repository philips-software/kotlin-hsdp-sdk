/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * A container for a collection of resources.
 */
@Serializable
data class Bundle(
    /**
     * The logical id of the resource. Once assigned, this value never changes.
     */
    val id: String? = null,

    /**
     * The metadata about the resource. This is content that is maintained by the infrastructure.
     * Changes to the content may not always be associated with version changes to the resource.
     */
    val meta: Meta? = null,

    /**
     * The type of the resource represented as string.
     */
    val resourceType: ResourceType,

    /**
     * Indicates the purpose of this bundle. How it is intended to be used.
     */
    val type: Type,

    /**
     * This is the number of matches for the search in this bundle.
     *
     * NOTE: the API spec indicates it is optional; that seems inconvenient in the case of getting multiple items
     * and trying to return pagination information
     */
    val total: Int,

    /**
     * This is the (pagination) page offset for the search in this bundle.
     *
     * NOTE: this is not mentioned in the API spec, but is present in the returned bundle with contracts.
     */
    val _startAt: Int? = null,

    /**
     * Links to previous and next pages
     *
     * NOTE: API spec indicates it is optional; for getting contracts and data items (plural), it is assumed that
     * it is there. Correct?!
     */
    val link: List<Link>? = null,

    /**
     * An entry in a bundle.
     *
     * NOTE: According to API spec this field is optional; does that make any sense?
     */
    val entry: List<BundleEntry>,
)
