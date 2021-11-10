/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Membership of a user to an organization, and assigned groups and roles.
 */
@Serializable
data class Membership(
    /**
     * The target organization where user is having membership.
     */
    val organizationId: String? = null,

    /**
     * Name of the target organization.
     */
    val organizationName: String? = null,

    /**
     * Contains a list of role names the user is having in the target organization.
     */
    val roles: List<String>? = null,

    /**
     * Contains a list of group names that the user is a member of in the target organization.
     */
    val groups: List<String>? = null,
)
