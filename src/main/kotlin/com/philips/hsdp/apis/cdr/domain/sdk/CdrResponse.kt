/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

/**
 * Interface that all CDR responses adhere to.
 */
interface CdrResponse {
    /**
     * HTTP status code returned by HSDP.
     */
    val status: Int
}