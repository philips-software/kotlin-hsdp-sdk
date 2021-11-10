/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Membership of a user to an organization, and assigned groups and roles.
 */
@Serializable
data class Membership(
    val organizationId: String? = null,
    val organizationName: String? = null,
    val roles: List<String>? = null,
    val groups: List<String>? = null,
)
