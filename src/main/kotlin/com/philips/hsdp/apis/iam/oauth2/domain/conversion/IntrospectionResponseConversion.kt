/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.conversion

import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.IntrospectionResponse
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.TokenMetadata

fun IntrospectionResponse.toTokenMetadata() = TokenMetadata(
    isActive = active,
    scopes = scopes,
    clientId = clientId,
    userName = userName,
    tokenType = tokenType,
    expirationTimeInEpochSeconds = exp,
    subject = sub,
    issuer = iss,
    identityType = identityType,
    deviceType = deviceType,
    organizations = organizations,
    tokenTypeHint = tokenTypeHint,
    clientOrganizationId = clientOrganizationId,
    actor = actor,
)
