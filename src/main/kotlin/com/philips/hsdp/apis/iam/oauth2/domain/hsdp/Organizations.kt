/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.hsdp

import com.philips.hsdp.apis.support.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Organizations for an identity account.
 */
@Serializable
@SerialName("organizations")
data class Organizations(
    /**
     * HSDP UUID of the organization to which the identity account is registered.
     */
    @SerialName("managingOrganization")
    @Serializable(with = UUIDSerializer::class)
    val managingOrganization: UUID,

    /**
     * A list of organizations to which the user has been granted some form of access rights.
     * Organization list will be empty if the token passed is refresh token or org_ctx is passed
     * in the request with an organization where the subject of the token doesn't have any permissions
     * assigned in the entire parent org structure.
     */
    @SerialName("organizationList")
    val organizationList: List<Organization>,
)
