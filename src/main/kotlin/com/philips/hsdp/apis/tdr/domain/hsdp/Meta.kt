/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import com.philips.hsdp.apis.support.ValidationPatterns.iso8601UtcPattern
import kotlinx.serialization.Serializable

/**
 * The metadata about the resource. This is content that is maintained by the infrastructure.
 * Changes to the content may not always be associated with version changes to the resource.
 *
 * NOTE: according to API spec, both fields are optional; a meta in the parent data class can be optional,
 *  but if there is a meta, it should probably contain both "lastUpdated" and "versionId".
 */
@Serializable
data class Meta(
    /**
     * The date and time the resource version last changed. In ISO 8601 (UTC) format.
     */
//    @field:Pattern("""^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d+)?Z${'$'}""")
    val lastUpdated: String,

    /**
     * The version of this resource.
     */
    val versionId: String,
) {
    init {
        require(lastUpdated.matches(iso8601UtcPattern)) {
            "lastUpdated is not in ISO 8601 (UTC) format"
        }
    }
}
