/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import com.philips.hsdp.apis.support.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A user identity representing an individual person. User belongs to an organization and a set of groups
 * within that organization. http://hl7.org/fhir/2015May/person.html
 */
@Serializable
data class Person(
    /**
     * The type of the resource.
     */
    val resourceType: String,

    /**
     * Unique login ID for the user. Maximum length can be 100.
     */
    val loginId: String,

    /**
     * This is optional. When password is provided -
     * (1) User account is activated automatically.
     * (2) Activation flow based on email is not triggered.
     * (3) In case of admin creating the account, user must change password after first login.
     */
    val password: String? = null,

    /**
     * A name of a human with text, parts and usage information.
     */
    val name: HumanName,

    /**
     * List of technology-mediated contact details for a person or organization. The system attribute determine
     * the type of contact point. Telephone numbers must be in E.164 format.
     * Note:
     * - In current version, Please provide one entry for mobile and one entry for email are supported. Other types
     *   shall be ignored.
     * - When duplicate entries for a system type is found only the last value - as received at server side
     *   of the given values shall be considered.
     */
    val telecom: List<ContactPoint>? = null,

    /**
     * Addresses of the user. http://hl7.org/fhir/2015May/datatypes.html#Address
     */
    val address: List<Address>? = null,

    /**
     * The organization UUID this User is part of.
     */
    @Serializable(with = UUIDSerializer::class)
    val managingOrganization: UUID? = null,

    /**
     * Language preference for all communications. Value can be a two letter language code as defined by
     * ISO-639-1 (en, de) or it can be a combination of language code and country code (en-gb, en-us).
     * The country code is as per ISO-3166 two-letter code (alpha-2)
     */
    val preferredLanguage: String? = null,

    /**
     * Preferred communication channel. Email and SMS are supported channels. Email is the default channel if
     * e-mail address is provided.
     */
    val preferredCommunicationChannel: String? = null,

    /**
     * This should be present for the self-registration user flow. The only accepted value is 'true'.
     */
    val isAgeValidated: String? = null,
)
