/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

@Serializable
data class OperationOutcome(
    /**
     * The type of the resource (OperationOutcome).
     */
    val resourceType: String,

    /**
     * Issues with the request.
     */
    val issue: List<Issue>,
)