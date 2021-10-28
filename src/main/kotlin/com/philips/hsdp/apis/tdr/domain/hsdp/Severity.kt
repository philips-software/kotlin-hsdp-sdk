/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The severity level of an issue.
 */
@Serializable
enum class Severity {
    @SerialName("fatal") Fatal,
    @SerialName("error") Error,
    @SerialName("warning") Warning,
    @SerialName("information") Information,
}
