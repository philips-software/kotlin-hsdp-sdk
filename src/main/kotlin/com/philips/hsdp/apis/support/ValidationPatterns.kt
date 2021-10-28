/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support

/**
 * Various regular expressions that are used to validate strings, e.g. ISO-8601 timestamps.
 */
object ValidationPatterns {
    val iso8601UtcPattern = Regex("""^([0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(\.[0-9]{3})?([+-]\d{2}:\d{2}|Z)${'$'}""")
    val iso8601UtcPlusOperatorPattern = Regex("""^(eq|ne|le|ge|lt|gt)?([0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(\.[0-9]{3})?([+-]\d{2}:\d{2}|Z)${'$'}""")
}
