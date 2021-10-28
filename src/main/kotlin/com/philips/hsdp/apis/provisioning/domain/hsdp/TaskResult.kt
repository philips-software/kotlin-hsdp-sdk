/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Result of the asynchronous task
 */
@Serializable
data class TaskResult(
    /**
     * HTTP Status code to indicate Task Result
     */
    val code: Int,

    /**
     * Task Result message
     */
    val message: String,

    /**
     * Current status of task
     */
    @SerialName("Result")
    val result: Result,
)
