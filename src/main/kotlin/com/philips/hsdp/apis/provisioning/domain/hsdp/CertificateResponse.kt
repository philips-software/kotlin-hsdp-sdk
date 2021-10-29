/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Represents platform identity certificate response
 */
@Serializable
data class CertificateResponse(
    /**
     * According to the HSDP API spec, the resourceType field should also be included in a CertificateResponse structure.
     * The deserialization requires a hint as to which data structure to decode to, and this is realized
     * by using the classDiscriminator, which will include the resourceType field to the serialized JSON
     * and use that to decode a JSON representation to a CertificateResponse structure.
     */

    override val meta: Meta? = null,

    /**
     * ID of the certificate
     */
    override val id: String,

    /**
     * This contains identity certificate as a string
     */
    val identityCertificate: String,

    /**
     * This contains identity certificate public key as a string
     */
    val identityCertificatePublicKey: String,

    /**
     * This contains identity certificate private key as a string
     */
    val identityCertificatePrivateKey: String,

    /**
     * This contains AWS root CA certificate as a string
     */
    val rootCertificateAuthority: String,
) : Resource, Result
