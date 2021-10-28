/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Actor acting on behalf of subject as defined in `RFC8693`. This is returned when token is granted for impersonation.
 */
@Serializable
data class Actor(
    /**
     * This is a machine-readable unique identifier of the actor.
     */
    val sub: String
)
