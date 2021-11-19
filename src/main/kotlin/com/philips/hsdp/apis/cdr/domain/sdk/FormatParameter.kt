/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

@Serializable
enum class FormatParameter(val value: String) {
    Xml("xml"),
    TextXml("text/xml"),
    ApplicationXml("application/xml"),
    ApplicationFhirXml("application/fhir+xml"),
    Json("json"),
    ApplicationJson("application/json"),
    ApplicationFhirJson("application/fhir+json"),
}
