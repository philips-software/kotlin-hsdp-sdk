/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * IAM Organization.
 */
@Serializable
data class Organization(
    /**
     * The HSDP UUID of the organization. The organizationId field displays 'root', if subject of the token has
     * memberships in HSDP root organization.
     */
    @SerialName("organizationId")
    val organizationId: String,

    /**
     * The name of the organization. The organizationName field displays 'root', if subject of the token has
     * memberships in HSDP root organization.
     */
    @SerialName("organizationName")
    val organizationName: String,

    /**
     * A boolean value that specify if the organization is disabled.
     * This property will not be returned if the organization is active.
     */
    @SerialName("disabled")
    val disabled: Boolean? = null,

    /**
     * A set of permission names that are assigned to the sub of the token in this organization.
     */
    @SerialName("permissions")
    val permissions: List<String> = emptyList(),

    /**
     * A set of permission names that are assigned to the subject of the token in this organization
     * and all the organizations in its parent org structure. This property will be returned only
     * if org_ctx is passed in the request and the organization is not in disabled state.
     */
    @SerialName("effectivePermissions")
    val effectivePermissions: List<String> = emptyList(),

    /**
     * A set of roles available for the user within this organization.
     */
    @SerialName("roles")
    val roles: List<String> = emptyList(),

    /**
     * A set of group names within the organization under which the user has memberships.
     */
    @SerialName("groups")
    val groups: List<String> = emptyList(),
)
