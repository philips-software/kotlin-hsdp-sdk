/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * A string that is associated with a single object or entity within a given system.
 * Typically, identifiers are used to connect content in resources to external content
 * available in other frameworks or protocols. If the identity is originating from HSDP's
 * Identity & Access Management service, the system should be left blank and the value can
 * either be the UUID of the IAM user or its (case-sensitive) username.
 */
@Serializable
data class Identifier(
    /**
     * Establishes the namespace in which set of possible id values is unique.
     * If the identity is originating from HSDP's Identity & Access Management service,
     * the system should be left blank.
     *
     * NOTE: the API spec shows the field as optional; it seems that an empty string is used (not null)
     * for IAM identities, so it is not optional, but it may be an empty string.
     */
    val system: String,

    /**
     * The value that is unique (within the given system/namespace).
     *
     * NOTE: the API spec shows the field as optional; this seems to be incorrect. The parent data class
     * can have optional Identifier fields ((Batch)DataItem), but inside Identifier, the value field
     * probably is required.
     */
    val value: String,
)
