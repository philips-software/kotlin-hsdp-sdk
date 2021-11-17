/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * All kinds of technology-mediated contact details for a person or organization, including telephone, email, etc.
 */
@Serializable
data class ContactPoint(
    /**
     * Type of contact address: mobile | fax | email | url.
     */
    val system: ContactType,

    /**
     * The actual contact details.
     */
    val value: String? = null,
)
