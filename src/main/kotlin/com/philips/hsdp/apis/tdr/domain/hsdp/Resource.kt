/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

/**
 * Base Resource. This is not an entity in itself. However, root resources (currently only
 * DataItem and contract) that extend from the base resource inherit the properties defined here.
 */
sealed interface Resource {
    /**
     * The logical id of the resource. Once assigned, this value never changes.
     */
    val id: String?

    /**
     * The metadata about the resource. This is content that is maintained by the infrastructure.
     * Changes to the content may not always be associated with version changes to the resource.
     */
    val meta: Meta?
}
