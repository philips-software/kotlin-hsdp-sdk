/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Indicates the level of detail to be returned.
 */
@Serializable
enum class ReturnPreference(val value: String) {
    Minimal("minimal"),
    Representation("representation"),
    OperationOutcome("OperationOutcome"),
}
