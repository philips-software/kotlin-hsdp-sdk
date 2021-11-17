/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
@file:Suppress("unused")

package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Indicate the purpose of a bundle.
 */
@Serializable
enum class Type {
    @SerialName("document") Document,
    @SerialName("message") Message,
    @SerialName("transaction") Transaction,
    @SerialName("transaction-response") TransactionResponse,
    @SerialName("batch") Batch,
    @SerialName("batch-response") BatchResponse,
    @SerialName("history") History,
    @SerialName("searchset") SearchSet,
    @SerialName("collection") Collection,
}
