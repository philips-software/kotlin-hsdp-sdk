/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PatchContentType(val value: String) {
    @SerialName("application/json-patch+json")
    JsonPatchPlusJson("application/json-patch+json"),

    @SerialName("application/xml-patch+xml")
    XmlPatchPlusXml("application/xml-patch+xml"),

    @SerialName("application/fhir+xml")
    FhirXml("application/fhir+xml"),

    @SerialName("application/fhir+json")
    FhirJson("application/fhir+json"),
}
