/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Possible types of resources that can be part of a bundle entry.
 */
@Serializable
enum class ResourceType {
    Parameters,
    CertificateRequest,
    CertificateResponse,
    Identity,
    BasicResource, // Mentioned in API spec as Resource
    OperationOutcome,
}
