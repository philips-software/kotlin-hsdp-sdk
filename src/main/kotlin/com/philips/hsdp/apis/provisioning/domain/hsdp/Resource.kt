/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

/**
 * Base class for all resources.
 */
sealed interface Resource {
    /**
     * resourceType is provided by the polymorphic (de)serialization
     */

    /**
     * The logical ID of the resource.
     */
    val id: String?

    /**
     * Metadata about a resource.
     */
    val meta: Meta?
}
