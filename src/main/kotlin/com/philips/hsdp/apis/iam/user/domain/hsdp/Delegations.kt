/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Delegations associated with user
 */
@Serializable
data class Delegations(
    /**
     * Granted Delegations.
     */
    val granted: List<GrantedDelegation>? = null,

    /**
     * Received Delegations.
     */
    val received: List<ReceivedDelegation>? = null,
)
