/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
@file:Suppress("unused")

package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Possible statuses of a task.
 */
@Serializable
enum class Status {
    @SerialName("InProgress") InProgress,
    @SerialName("Completed") Completed,
    @SerialName("Failed") Failed,
}
