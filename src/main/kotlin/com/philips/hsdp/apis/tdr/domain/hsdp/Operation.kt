/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The operation to be performed in a patch operation of a data item, see also
 * [PatchOperation][com.philips.hsdp.apis.tdr.domain.hsdp.PatchOperation].
 */
@Serializable
enum class Operation {
    @SerialName("add") Add,
    @SerialName("remove") Remove,
    @SerialName("replace") Replace,
    @SerialName("move") Move,
    @SerialName("copy") Copy,
    @SerialName("test") Test,
}
