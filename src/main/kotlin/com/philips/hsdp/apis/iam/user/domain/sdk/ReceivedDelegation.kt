/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Received Delegation object.
 */
@Serializable
data class ReceivedDelegation(
    val delegatorId: String,
    val validFrom: String,
    val validUntil: String
)
