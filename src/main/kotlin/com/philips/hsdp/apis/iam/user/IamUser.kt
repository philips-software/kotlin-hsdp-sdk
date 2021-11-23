/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.oauth2.IamOAuth2
import com.philips.hsdp.apis.iam.user.domain.conversion.*
import com.philips.hsdp.apis.iam.user.domain.hsdp.Challenges
import com.philips.hsdp.apis.iam.user.domain.hsdp.PasswordPolicy
import com.philips.hsdp.apis.iam.user.domain.hsdp.Users
import com.philips.hsdp.apis.iam.user.domain.sdk.*
import com.philips.hsdp.apis.support.HeaderParameter
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import com.philips.hsdp.apis.tdr.domain.sdk.query.QueryParameter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.suspendCoroutine

/**
 * IAM User Management.
 */
class IamUser(
    /**
     * Region information for access and identity user management.
     */
    private val idmUrl: String,
    private val httpClient: HttpClient,
    /**
     * Client ID to be used for endpoints that require basic authentication.
     */
    clientId: String? = null,
    /**
     * Client Secret to be used for endpoints that require basic authentication.
     */
    clientSecret: String? = null,
    private val sharedKey: String? = null,
    private val secretKey: String? = null,
    private val signingPrefix: String? = null,
) {
    private val logger = PlatformLoggerFactory.create(javaClass.simpleName, javaClass)
    private val basicAuthorization = clientId?.let { id ->
        clientSecret?.let { secret ->
            Base64.getEncoder().encodeToString("$id:$secret".toByteArray())
        }
    }

    companion object {
        /**
         * JSON serialization instance configured to cope with polymorphic resources in the bundles returned by
         * HSDP TDR API.
         */
        val json = Json {
            ignoreUnknownKeys = true
        }
    }

    private val jsonMediaType = "application/json; charset=utf-8"
    private val authorizeIdentityUserPath = "authorize/identity/User"

    private fun buildRequest(
        pathSegments: String,
        apiVersion: String,
        authorizationType: IamOAuth2.AuthorizationType = IamOAuth2.AuthorizationType.None,
        headerParameters: List<HeaderParameter> = emptyList(),
        queryParameters: List<QueryParameter> = emptyList(),
    ): Request.Builder {
        // Use OkHttp URL builder to make sure the path segments and query parameters are URL-encoded
        val urlBuilder = idmUrl.toHttpUrl().newBuilder()
            .addPathSegments(pathSegments)
        queryParameters.forEach { qp ->
            urlBuilder.addQueryParameter(qp.name, qp.value)
        }

        val url = urlBuilder.build().toString()
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("Api-Version", apiVersion)
            .addHeader("Accept", jsonMediaType)
        headerParameters.forEach { (name, value) -> requestBuilder.addHeader(name, value) }

        when (authorizationType) {
            IamOAuth2.AuthorizationType.Basic ->
                requestBuilder.addHeader("Authorization", "Basic $basicAuthorization")
            IamOAuth2.AuthorizationType.Bearer ->
                requestBuilder.addHeader("Authorization", "Bearer ${httpClient.token.accessToken}")
            IamOAuth2.AuthorizationType.None -> {
                // Don't add any authorization header
            }
        }
        return requestBuilder
    }

    /**
     * Search for a user account that matches the given userId and returns the basic profile attributes and a
     * set of extended attributes of the matching user account based on the requested profile.
     * Required permission: Any user with USER.READ or USER.WRITE permission can perform this operation.
     *
     * @param userId A string that uniquely identifies a user in the IAM system. The account matching is performed
     *               using this parameter which takes either login id or a unique id of a user account.
     * @param profileType: Determines the parts of information (if available) to be included in the search results
     *                     (membership, account status, password status, consented apps, delegations, all)
     * @return The information about the user or null if the user with given id does not exist.
     */
    suspend fun searchUser(userId: String, profileType: ProfileType = ProfileType.All): User? =
        suspendCoroutine { continuation ->
            val queryParameters = listOf(
                QueryParameter("userId", userId),
                QueryParameter("profileType", profileType.value)
            )

            val request = buildRequest(
                pathSegments = authorizeIdentityUserPath,
                apiVersion = "2",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
                queryParameters = queryParameters,
            )
                .get()
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                json.decodeFromString<Users>(responseBody).toUserList().firstOrNull()
            }
        }

    /**
     * Creates a new user within an organization as an administrator.
     *
     * A user can be registered in the system in two ways:
     * - User account with activation email (without password)
     *   The user registration triggers an account activation email to the registered email ID of the user.
     *   Although the activation email has an expiry period of 72 hours, the user account stays in the system
     *   in an inactive state. It is possible to request a new activation email for an expired account.
     *   Upon activation, the user shall set the password for the account and can update the complete user profile.
     * - User account with temporary password
     *   The organization administrator can provide temporary password for activation if the email ID is not available.
     *   In such cases, the user is enforced to change the password upon first login.
     */
    suspend fun registerUser(newUser: CreateUserAsAdmin): UserLocation? =
        suspendCoroutine { continuation ->
            val headerParameters = getSignatureHeaders()
            val body: RequestBody = Json.encodeToString(newUser.toPerson())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = authorizeIdentityUserPath,
                apiVersion = "3",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
                headerParameters = headerParameters,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                logger.debug { requireNotNull(response.body).string() }
                if (response.code == 201) {
                    UserLocation(location = response.headers["Location"]!!)
                } else {
                    null
                }
            }
        }

    /**
     * Creates a new user within an organization using self-registration.
     *
     * A user can be registered in the system in two ways:
     * - User account with activation email (without password)
     *   The user registration triggers an account activation email to the registered email ID of the user.
     *   Although the activation email has an expiry period of 72 hours, the user account stays in the system
     *   in an inactive state. It is possible to request a new activation email for an expired account.
     *   Upon activation, the user shall set the password for the account and can update the complete user profile.
     * - User account with temporary password
     *   The organization administrator can provide temporary password for activation if the email ID is not available.
     *   In such cases, the user is enforced to change the password upon first login.
     */
    suspend fun registerUser(newUser: SelfCreateUser): UserLocation? =
        suspendCoroutine { continuation ->
            val headerParameters = getSignatureHeaders()
            val body: RequestBody = Json.encodeToString(newUser.toPerson())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = authorizeIdentityUserPath,
                apiVersion = "3",
                headerParameters = headerParameters,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                logger.debug { requireNotNull(response.body).string() }
                if (response.code == 201) {
                    UserLocation(location = response.headers["Location"]!!)
                } else {
                    null
                }
            }
        }

    /**
     * Delete a user account as an administrative user.
     *
     * Removes a user account from an organization. Any user with USER.DELETE or USER.WRITE permission can do this
     * operation. For this operation to be successful, the user should not have any memberships attached to it.
     */
    suspend fun deleteUserAsAdmin(userId: UUID): Unit =
        suspendCoroutine { continuation ->
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/$userId",
                apiVersion = "2",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
            )
                .delete()
                .build()

            httpClient.performRequest(request, continuation, logger) {
                logger.info { "User $userId deleted successfully" }
            }
        }

    /**
     * Delete a user account as a self-managed user.
     *
     * For this operation to be successful, the user should not have any memberships attached to it.
     */
    suspend fun selfDeleteUser(userId: UUID): Unit =
        suspendCoroutine { continuation ->
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/$userId",
                apiVersion = "2",
            )
                .delete()
                .build()

            httpClient.performRequest(request, continuation, logger) {
                logger.info { "User $userId deleted successfully" }
            }
        }

    /**
     * Change a user's password.
     *
     * Set a new password in case of password expiration or if a user wants to change his or her password.
     * The inputs are user loginId, the old password, and the new password. The new password will be set only
     * after verification of the old password. The account will be in an active/enabled state for this operation.
     * The API supports password history, as users cannot enter their past 5 successful passwords.
     */
    suspend fun changePassword(changePassword: ChangePassword): Unit =
        suspendCoroutine { continuation ->
            val headerParameters = getSignatureHeaders() +
                    listOfNotNull(changePassword.acceptLanguage?.let {
                        HeaderParameter("Accept-Language", it)
                    })
            val body = json.encodeToString(changePassword.toChangePasswordRequest())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/\$change-password",
                apiVersion = "1",
                headerParameters = headerParameters,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) {
                logger.debug { "Password change was successful." }
            }
        }

    /**
     * Reset a user's password.
     *
     * Triggers password reset flow for a user. This acts as kba verification step before sending the reset code
     * through an email or SMS. Only confidential client that passes basic authorization header will be able to
     * execute this API. If invalid kba information is submitted more than permitted times (based on the org's
     * maxIncorrectAttempts), the user account will be locked. If KBA information is valid, reset code will be
     * sent using the notificationMode attribute in input. EMAIL & SMS value is supported in notificationMode.
     * To support SMS notification mode SMS Gateway should be created under the user's organization.
     */
    suspend fun resetPassword(resetPassword: ResetPassword): Unit =
        suspendCoroutine { continuation ->
            val headerParameters = resetPassword.acceptLanguage?.let {
                listOf(HeaderParameter("Accept-Language", it))
            } ?: emptyList()
            val body = json.encodeToString(resetPassword.toHsdpResetPassword())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/\$reset-password",
                apiVersion = "1",
                authorizationType = IamOAuth2.AuthorizationType.Basic,
                headerParameters = headerParameters,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) {
                logger.debug { "Password reset was successful." }
            }
        }

    /**
     * Set the password for a user.
     *
     * Called at the end of forgot password flow or at the end of user creation flow. The inputs are loginId,
     * confirmationcode, new password and context. A context parameter is provided to identify where it got called.
     * At the end of forgot password, it just sets the new password provided in the API.
     * At the end of user create, it sets the given new password and activates the user.
     * Context parameters can be context=userCreate or context=recoverPassword.
     * To prevent account enumeration attacks, in cases where the given confirmation code and the loginID do not make
     * a valid combination, a 401 code will be returned with an abstract message so that hacker can not determine
     * whether the account exists. The correctness/existence of the given emailID is not revealed in the call output.
     */
    suspend fun setPassword(setPassword: SetPassword): Unit =
        suspendCoroutine { continuation ->
            val headerParameters = getSignatureHeaders()
            val body = json.encodeToString(setPassword.toSetPasswordRequest())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/\$set-password",
                apiVersion = "2",
                headerParameters = headerParameters,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) {
                logger.debug { "Password was set successfully." }
            }
        }

    /**
     * This API enables Admin to reset user password. This is useful when there are no channels like Email or SMS
     * for password recovery. This API requires USER.RESET_PASSWORD permission.
     * User must change password on next login.
     */
    suspend fun forceResetPassword(forceResetPassword: ForceResetPassword): Unit =
        suspendCoroutine { continuation ->
            val body = json.encodeToString(forceResetPassword.toForceResetPasswordBody())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/\$force-reset-password",
                apiVersion = "1",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) {
                logger.debug { "Forced password reset was successful." }
            }
        }

    /**
     * Resend the activation email to the registered email ID.
     *
     * Provides an ability to get a new activation email when a user has lost the previously sent activation email
     * or the existing activation email has expired. The platform sends a new activation mail to the user's registered
     * email account.
     * The input for this API is the user loginId provided during registration.
     * - The activation link has a validity of 72 hours, after which the user must request a new activation email.
     * - Any existing, valid activation emails become invalid after calling this API. Users must always use the latest
     *   activation/invitation email received.
     * - To avoid account enumeration attacks, the same message will be displayed whether the user exists.
     * - If the user account is already active, the platform sends an account status confirmation mail in place of
     *   the activation email.
     */
    suspend fun resendActivation(resendActivation: ResendActivation): Unit =
        suspendCoroutine { continuation ->
            val headerParameters = getSignatureHeaders() +
                    listOfNotNull(resendActivation.acceptLanguage?.let {
                        HeaderParameter("Accept-Language", it)
                    })
            val body = json.encodeToString(resendActivation.toRecoverPasswordRequest())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/\$resend-activation",
                apiVersion = "1",
                headerParameters = headerParameters,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                logger.debug { requireNotNull(response.body).string() }
            }
        }

    /**
     * Unlock a user account.
     *
     * Allows an administrator to unlock a user account if the user account is locked due to invalid login attempts.
     * USER.WRITE permission is required to do this operation.
     */
    suspend fun unlockUser(userId: UUID): Unit =
        suspendCoroutine { continuation ->
            val body = """{"action": "reset"}""".toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/$userId/\$unlock",
                apiVersion = "1",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { }
        }

    /**
     * Save challenge questions and answers selected by the user.
     *
     * This API saves given set of questions and answers selected by a user. In a typical UI workflow,
     * this will be used while completing registration or after setting password. This is a self-service API;
     * a given user's access token will permit the user's kba to be set. Alternately, USER.WRITE permission
     * allows setting user's kba. The payload submitted must match restrictions set by the minQuestionCount
     * attribute for kba at organization level.
     */
    suspend fun saveChallenges(userChallenges: UserChallenges): Unit =
        suspendCoroutine { continuation ->
            val body = json.encodeToString(userChallenges.toUserKbaRequest())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/${userChallenges.userId}/\$kba",
                apiVersion = "1",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { }
        }

    /**
     * Retrieve saved Kba challenges questions for a User.
     *
     * Retrieves saved Kba questions for a user based on login id.
     * A private OAuth2.0 client that passes basic authorization header will be able to execute this API.
     */
    suspend fun getChallengeQuestions(loginId: String): List<String> =
        suspendCoroutine { continuation ->
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/\$kba?loginId=$loginId",
                apiVersion = "1",
                authorizationType = IamOAuth2.AuthorizationType.Basic,
            )
                .get()
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body).string()
                val challenges: Challenges = json.decodeFromString(responseBody)
                challenges.challenges.map { it.challenge }
            }
        }

    /**
     * Get Effective password policy of a user.
     *
     * Retrieves the effective password policy applied for a user. Effective password policy is derived from the
     * user's organization, its parent organizations and IAM default password policy.
     * A User with PASSWORDPOLICY.READ or PASSWORDPOLICY.WRITE permission can get the effective password policy.
     */
    suspend fun getEffectivePasswordPolicy(userId: UUID): PasswordPolicy =
        suspendCoroutine { continuation ->
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/$userId/\$password-policy",
                apiVersion = "1",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
            )
                .get()
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body).string()
                json.decodeFromString(responseBody)
            }
        }

    /**
     * Send verification code (OTP) to secondary authentication factors like Email and SMS.
     *
     * Send one-time passcode (OTP) for verification using secondary authentication factors like EMAIL and SMS.
     * Enables second factor authentication based on OTP (one-time passcode) sent to registered primary email address
     * or registered primary phone. The registered primary phone number should be in E.164 format.
     * Use confirmVerificationCode API to verify the code.
     */
    suspend fun sendVerificationCode(verificationCode: VerificationCode): Unit =
        suspendCoroutine { continuation ->
            val headerParameters = listOfNotNull(verificationCode.acceptLanguage?.let {
                HeaderParameter("Accept-Language", it)
            })
            val body = json.encodeToString(verificationCode.toSendVerificationCodeBody())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/\$send-verification-code",
                apiVersion = "1",
                authorizationType = IamOAuth2.AuthorizationType.Basic,
                headerParameters = headerParameters
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                logger.debug { requireNotNull(response.body).string() }
            }
        }

    /**
     * Verify the code (OTP) sent to secondary authentication factors like Email and SMS.
     *
     * Verifies the code sent to second authentication factor like primary email address and phone based on
     * registered user profile. Enables second factor authentication based on OTP (one-time passcode) sent to
     * registered primary email address or registered primary phone. Verification also activates user account
     * if account is not activated.
     */
    suspend fun confirmVerificationCode(verificationWithCode: VerificationWithCode): Unit =
        suspendCoroutine { continuation ->
            val body = json.encodeToString(verificationWithCode.toConfirmVerificationCodeBody())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/\$confirm-verification-code",
                apiVersion = "1",
                authorizationType = IamOAuth2.AuthorizationType.Basic,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                logger.debug { requireNotNull(response.body).string() }
            }
        }

    /**
     * Remove OTP device registration.
     *
     * Resets the device key registered for the user. This allows user to re-register a new device to generate
     * soft OTP for multi-factor authentication. An OAuth2 Bearer token is required to do this operation. Self user
     * with no permission or any user with ORGANIZATION.MFA permission within the organization can do this operation.
     */
    suspend fun resetMultiFactorAuthentication(userId: UUID): Unit =
        suspendCoroutine { continuation ->
            val body = """{"action": "reset"}""".toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/$userId/\$mfa-reset",
                apiVersion = "2",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { }
        }

    /**
     * Update user's loginId.
     *
     * Allows user or an administrator to change user's loginId. Self user or a user with USER.WRITE permission can
     * perform this operation. Once user's loginId is changed all existing tokens of the user will be invalidated.
     */
    suspend fun changeLoginId(changeLogin: ChangeLogin): Unit =
        suspendCoroutine { continuation ->
            val body = """{"loginId": "${changeLogin.loginId}"}""".toRequestBody(jsonMediaType.toMediaTypeOrNull())
            val request = buildRequest(
                pathSegments = "$authorizeIdentityUserPath/${changeLogin.userId}/\$change-loginid",
                apiVersion = "2",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
            )
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { }
        }


    private fun getSignatureHeaders(): List<HeaderParameter> {
        require(sharedKey != null && secretKey != null && signingPrefix != null) {
            "A shared and secret key are required for signing the request."
        }
        val signedHeaders = "SignedDate"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val instant =  ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"))
        val signedDate = formatter.format(instant)
        val generatedSignature = createSignature(signedDate, "$signingPrefix$secretKey")
        val hsdpApiSignature =
            "HmacSHA256;Credential:$sharedKey;SignedHeaders:${signedHeaders};Signature:${generatedSignature}"
        return listOf(
            HeaderParameter("HSDP-API-Signature", hsdpApiSignature),
            HeaderParameter("SignedDate", signedDate),
        )
    }

    private fun createSignature(data: String, key: String): String {
        val sha256Hmac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
        sha256Hmac.init(secretKey)
        val base64EncodedData = Base64.getEncoder().encodeToString(data.toByteArray())
        return Base64.getEncoder()
            .encodeToString(sha256Hmac.doFinal(base64EncodedData.toByteArray()))
    }
}
