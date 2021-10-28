/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * List of users returned from a searchUser request.
 */
@Serializable
data class Users(
    /**
     * Total count of users resulted as per request.
     */
    val total: Int = 0,

    /**
     * List of users returned based on search criteria.
     */
    val entry: List<UserDetails> = emptyList(),
)
