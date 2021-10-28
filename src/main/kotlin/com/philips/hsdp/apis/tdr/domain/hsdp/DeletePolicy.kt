/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

// NOTE: the API spec shows both fields are optional; this seems to be wrong.
// What does a DeletePolicy mean without duration and/or unit value?
/**
 * This policy specifies when the DataItem needs to be deleted.
 */
@Serializable
data class DeletePolicy(
    val duration: Int,
    val unit: TimeUnit,
)
