/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * A string that is associated with a single object or entity within a given system.
 * Typically, identifiers are used to connect content in resources to external content available in
 * other frameworks or protocols. In the data store, system and value are concatenated with a pipe '|'.
 */
@Serializable
data class Identifier(
    /**
     * The namespace for the identifier.
     */
    val system: String,

    /**
     * The value that is unique (within the given system/namespace).
     */
    val value: String,
)
