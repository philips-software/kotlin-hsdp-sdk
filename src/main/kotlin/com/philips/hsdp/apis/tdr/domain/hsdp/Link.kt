/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Links related to this Bundle
 *
 * NOTE: according to API spec, both fields are optional; a link in the parent data class can be optional,
 *  but if there is a link, it should probably contain both "relation" and "url".
 */
@Serializable
data class Link(
    /**
     * Description of the type of link.
     */
    val relation: Relation,

    /**
     * A Uniform Resource Identifier Reference (RFC 3986)
     */
    val url: String,
)
