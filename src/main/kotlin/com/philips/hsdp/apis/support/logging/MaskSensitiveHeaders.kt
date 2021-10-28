/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support.logging

import okhttp3.Headers

fun Headers.maskSensitiveHeaders() = toMultimap().map { (key, values) ->
    val valuesWithHiddenSecrets = when (key.lowercase()) {
        "authorization" -> values.map { it.substringBefore(" ", "") + " XXXXXX" }
        else -> values
    }
    Pair(key, valuesWithHiddenSecrets)
}