/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

/**
 * Query parameters for assembling a URL.
 *
 * Before creating the URL that will be used to call an HSDP TDR API endpoint, the various queries will be combined
 * into a [QueryParameter] list, which is then joined with '&' separator and prefixed with a '?', and then appended
 * to the baseUrl + resourcePath of the service.
 */
data class QueryParameter(
    /**
     * Name of the parameter (will be left of the equals-sign in the eventual url query parameter).
     */
    val name: String,
    /**
     * Value of the parameter (will be right of the equals-sign in the eventual url query parameter).
     */
    val value: String? = null
)
