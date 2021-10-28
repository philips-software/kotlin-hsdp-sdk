/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Asynchronous task
 */
@Serializable
data class Task(
    /**
     * Task Async Id
     */
    val id: String,

    /**
     * Result of the task
     */
    @SerialName("Result")
    val result: TaskResult,

    /**
     * Current status of task
     */
    val status: Status,
)
