/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * A name of a human with text, parts and usage information.
 */
@Serializable
data class HumanName(
    /**
     * Family name (often called 'Surname').
     */
    val family: String = "",

    /**
     * Given names (not always 'first'). Includes middle names.
     */
    val given: String = "",
)
