/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.conversion

/**
 * Default page offset when requesting contracts or data items from TDR.
 */
const val defaultOffset = 0

/**
 * Default page size when requesting contracts or data items from TDR.
 */
const val defaultLimit = 100

/**
 * Regular expression that is used to extract the page size that was used for pagination from the URL that
 * is returned by HSDP TDR.
 */
val countRegex = Regex("""^.*_count=(\d+).*""")
