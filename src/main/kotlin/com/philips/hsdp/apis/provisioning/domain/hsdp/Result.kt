/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

/**
 * In case of success,
 * - For Provisioning async task result would be Parameters,
 * - For Certificate/create transaction response would be CertificateResponse.
 * And in case of failure response would be OperationOutcome
 */
sealed interface Result
