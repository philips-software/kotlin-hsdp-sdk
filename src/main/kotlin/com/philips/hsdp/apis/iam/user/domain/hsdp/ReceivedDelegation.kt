/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Received Delegation object.
 */
@Serializable
data class ReceivedDelegation(
    /**
     * Unique id of the delegator
     */
    val delegatorId: String,

    /**
     * Start time stamp of time window when delegation is activated. Date time format per RFC 3339 in UTC time zone.
     */
    val validFrom: String,

    /**
     * End time stamp of time window when delegation is deactivated. Date time format per RFC 3339 in UTC time zone.
     */
    val validUntil: String
)
