/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2

import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.Actor
import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.Organization
import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.Organizations
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.PostalAddress
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.TokenMetadata
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.UserInfo
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.HttpException
import com.philips.hsdp.apis.support.TokenRefresher
import com.philips.hsdp.apis.support.logging.MockLoggerFactory
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
/* ktlint-disable no-wildcard-imports */
import org.junit.jupiter.api.*
/* ktlint-enable no-wildcard-imports */
import java.time.Duration
import java.util.Base64
import java.util.UUID

class IamOAuth2Test {

    init {
        PlatformLoggerFactory.registerConcreteFactory(MockLoggerFactory)
    }

    /**
     * The Setup class is needed to prevent that the mocked results in the various inner classes are used in one of
     * the other inner test classes, resulting in failing tests. Now, every inner class has its own mock web server.
     */
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    open inner class Setup {
        protected val server = MockWebServer().apply {
            start()
        }
        private val tokenRefresherMock = mockk<TokenRefresher>()
        val httpClient = HttpClient(callTimeout = Duration.ofMillis(300)).apply {
            tokenRefresher = tokenRefresherMock
        }
        private val initialToken = Token(
            accessToken = "22a34a6e-214c-4e3e-b85f-b4bbd1448613",
            refreshToken = "64d50b31-3917-4f9a-993e-76f0dcfcebdb",
            idToken = "idToken",
            tokenType = "Bearer",
            expiresIn = 1200,
        )
        protected val iamOAuth2 = IamOAuth2(
            iamUrl = server.url("").toString(),
            clientId = "clientId",
            clientSecret = "clientSecret",
            httpClient = httpClient,
            initialToken = initialToken
        )

        @AfterAll
        fun afterAll() {
            server.shutdown()
        }
    }

    @Nested
    inner class UserLogin : Setup() {
        @Test
        fun `Should return a token when a login is successful`(): Unit = runBlocking {
            // Given
            val response = buildJsonObject {
                put("scope", "auth_iam_introspect auth_iam_organization cn openid profile")
                put("access_token", "22a34a6e-214c-4e3e-b85f-b4bbd1448613")
                put("refresh_token", "64d50b31-3917-4f9a-993e-76f0dcfcebdb")
                put("expires_in", 1799)
                put("token_type", "Bearer")
            }

            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(response.toString())

            server.enqueue(mockedResponse)

            // When
            val token = iamOAuth2.login("user1", "secret1")

            // Then
            token.accessToken shouldBe "22a34a6e-214c-4e3e-b85f-b4bbd1448613"
            token.refreshToken shouldBe "64d50b31-3917-4f9a-993e-76f0dcfcebdb"
            token.scopes shouldBe "auth_iam_introspect auth_iam_organization cn openid profile"
            token.tokenType shouldBe "Bearer"
            token.expiresIn shouldBe 1799

            // Then
            val request = server.takeRequest()
            request.requestUrl?.encodedPath shouldBe "/authorize/oauth2/token"
            request.method shouldBe "POST"
            request.body.toString() shouldBe "[text=grant_type=password&username=user1&password=secret1]"
        }

        @Test
        fun `Should throw an HttpException when login is unsuccessful due to invalid grant`(): Unit = runBlocking {
            // Given
            val response = buildJsonObject {
                put("error", "invalid_grant")
                put("error_description", "The provided access grant is invalid, expired, or revoked.")
            }

            val mockedResponse = MockResponse()
                .setResponseCode(400)
                .setBody(response.toString())

            server.enqueue(mockedResponse)

            // When
            shouldThrow<HttpException> {
                iamOAuth2.login("user1", "secret1")
            }

            // Then
            val request = server.takeRequest()
            request.requestUrl?.encodedPath shouldBe "/authorize/oauth2/token"
            request.method shouldBe "POST"
            request.body.toString() shouldBe "[text=grant_type=password&username=user1&password=secret1]"
        }

        @Test
        fun `Should throw when login returns an invalid payload`(): Unit = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("""{"invalid":"payload"}""")

            server.enqueue(mockedResponse)

            // When
            val exception = assertThrows<HttpException> {
                iamOAuth2.login("user1", "secret1")
            }

            // Then
            server.takeRequest()
            exception.code shouldBe 500
            exception.message shouldStartWith "Unexpected JSON token at offset 2: Encountered an unknown key 'invalid'"
        }
    }

    @Nested
    inner class ClientCredentialsLogin : Setup() {
        @Test
        fun `Should return a token when a client_credentials login is successful`(): Unit = runBlocking {
            // Given
            val response = buildJsonObject {
                put("scope", "auth_iam_introspect auth_iam_organization cn openid profile")
                put("access_token", "22a34a6e-214c-4e3e-b85f-b4bbd1448613")
                put("refresh_token", "")
                put("expires_in", 1799)
                put("token_type", "Bearer")
            }

            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(response.toString())

            server.enqueue(mockedResponse)

            // When
            val token = iamOAuth2.login()

            // Then
            token.accessToken shouldBe "22a34a6e-214c-4e3e-b85f-b4bbd1448613"
            token.refreshToken shouldBe ""
            token.scopes shouldBe "auth_iam_introspect auth_iam_organization cn openid profile"
            token.tokenType shouldBe "Bearer"
            token.expiresIn shouldBe 1799

            // Then
            val request = server.takeRequest()
            request.requestUrl?.encodedPath shouldBe "/authorize/oauth2/token"
            request.method shouldBe "POST"
            request.body.toString() shouldBe "[text=grant_type=client_credentials]"
        }

        @Test
        fun `Should throw when login returns an invalid payload`(): Unit = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("""{"invalid":"payload"}""")

            server.enqueue(mockedResponse)

            // When
            val exception = assertThrows<HttpException> {
                iamOAuth2.login()
            }

            // Then
            server.takeRequest()
            exception.code shouldBe 500
            exception.message shouldStartWith "Unexpected JSON token at offset 2: Encountered an unknown key 'invalid'"
        }
    }

    @Nested
    inner class ServiceLogin : Setup() {
        // Private key file in pkcs1-format was generated using command:
        // $ ssh-keygen -t rsa -b 4096 -o -a 100 -f src/test/resources/iam/oauth2/pkcs1-private.key.pem -m PEM < /dev/null
        // And generating a pkcs8-format file from that:
        // $ openssl pkcs8 -topk8 -inform pem -in src/test/resources/iam/oauth2/pkcs1-private.key.pem -outform PEM -nocrypt -out src/test/resources/iam/oauth2/pkcs8-private.key.pem
        // For generation of a pkcs8 private key we could also have used:
        // $ openssl genpkey -out src/test/resources/iam/oauth2/private8-via-genpkey.txt -outform PEM -algorithm RSA -pkeyopt rsa_keygen_bits:2048
        @Test
        fun `Should return a valid token when providing a valid private key in pkcs1 format and subject`(): Unit = runBlocking {
            val privateKey =
                javaClass.getResource("/iam/oauth2/pkcs1-private.key.pem")?.readText() ?: throw Exception("Resource not found")
            // Given
            val response = buildJsonObject {
                put("scope", "auth_iam_introspect auth_iam_organization cn openid profile")
                put("access_token", "22a34a6e-214c-4e3e-b85f-b4bbd1448613")
                put("refresh_token", "")
                put("expires_in", 1799)
                put("token_type", "Bearer")
            }

            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(response.toString())

            server.enqueue(mockedResponse)

            // When
            val token = iamOAuth2.serviceLogin(privateKey, "issuer")

            // Then
            token.accessToken shouldBe "22a34a6e-214c-4e3e-b85f-b4bbd1448613"
            token.refreshToken shouldBe ""
            token.scopes shouldBe "auth_iam_introspect auth_iam_organization cn openid profile"
            token.tokenType shouldBe "Bearer"
            token.expiresIn shouldBe 1799

            // Then
            val request = server.takeRequest()
            request.requestUrl?.encodedPath shouldBe "/authorize/oauth2/token"
            request.method shouldBe "POST"
            request.body.toString() shouldContain "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer"
        }

        @Test
        fun `Should return a valid token when providing a valid private key in pkcs8 format and subject`(): Unit = runBlocking {
            val privateKey =
                javaClass.getResource("/iam/oauth2/pkcs8-private.key.pem")?.readText() ?: throw Exception("Resource not found")
            // Given
            val response = buildJsonObject {
                put("scope", "auth_iam_introspect auth_iam_organization cn openid profile")
                put("access_token", "22a34a6e-214c-4e3e-b85f-b4bbd1448613")
                put("refresh_token", "")
                put("expires_in", 1799)
                put("token_type", "Bearer")
            }

            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(response.toString())

            server.enqueue(mockedResponse)

            // When
            val token = iamOAuth2.serviceLogin(privateKey, "issuer")

            // Then
            token.accessToken shouldBe "22a34a6e-214c-4e3e-b85f-b4bbd1448613"
            token.refreshToken shouldBe ""
            token.scopes shouldBe "auth_iam_introspect auth_iam_organization cn openid profile"
            token.tokenType shouldBe "Bearer"
            token.expiresIn shouldBe 1799

            // Then
            val request = server.takeRequest()
            request.requestUrl?.encodedPath shouldBe "/authorize/oauth2/token"
            request.method shouldBe "POST"
            request.body.toString() shouldContain "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer"
        }

        @Test
        fun `Should throw an exception when providing a key with invalid pre and postfix`(): Unit = runBlocking {
            // Given
            val privateKey =
                javaClass.getResource("/iam/oauth2/invalid.key")?.readText() ?: throw Exception("Resource not found")

            // When
            shouldThrow<Exception> {
                iamOAuth2.serviceLogin(privateKey, "issuer")
            }
        }
    }

    @Nested
    inner class RefreshToken : Setup() {
        @Test
        fun `Should return a new token when current token contains refresh token`(): Unit = runBlocking {
            // Given
            val response = buildJsonObject {
                put("scope", "auth_iam_introspect auth_iam_organization cn openid profile")
                put("access_token", "22a34a6e-214c-4e3e-b85f-b4bbd1448613")
                put("refresh_token", "64d50b31-3917-4f9a-993e-76f0dcfcebdb")
                put("expires_in", 1799)
                put("token_type", "Bearer")
                put("id_token", "id_token_new_not_to_take_over")
            }
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(response.toString())

            server.enqueue(mockedResponse)

            // When
            val token = iamOAuth2.refreshToken()

            // Then
            // Next fields should contain the newly reported values
            token.accessToken shouldBe "22a34a6e-214c-4e3e-b85f-b4bbd1448613"
            token.scopes shouldBe "auth_iam_introspect auth_iam_organization cn openid profile"
            token.tokenType shouldBe "Bearer"
            token.expiresIn shouldBe 1799
            // Next fields should stick to the old values
            token.refreshToken shouldBe "64d50b31-3917-4f9a-993e-76f0dcfcebdb"
            token.idToken shouldBe "idToken"

            // Then
            val request = server.takeRequest()
            request.requestUrl?.encodedPath shouldBe "/authorize/oauth2/token"
            request.method shouldBe "POST"
            request.body.toString() shouldContain "grant_type=refresh_token&refresh_token=64d50b31-3917"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf(
                    "Basic ${
                        Base64.getEncoder().encodeToString("clientId:clientSecret".toByteArray())
                    }"
                ),
                "api-version" to listOf("2"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
        }

        @Test
        fun `Should throw IllegalArgumentException when current token contains no refresh token`(): Unit = runBlocking {
            // Given
            val iamOAuth2 = IamOAuth2(
                server.url("").toString(),
                clientId = "clientId",
                clientSecret = "clientSecret",
                httpClient = httpClient,
                initialToken = Token(
                    accessToken = "c19cbc4d-cb39-4255-b203-95e4b781883f",
                    refreshToken = "",
                    scopes = "initialScopes",
                    expiresIn = 10,
                    tokenType = "Bearer_old",
                    idToken = "idToken",
                )
            )

            // When
            val exception = shouldThrow<IllegalArgumentException> {
                iamOAuth2.refreshToken()
            }

            // Then
            exception.message shouldBe "The refresh token must not be empty."
        }

        @Test
        fun `Should throw HttpException when returned body is not valid JSON`(): Unit = runBlocking {
            // Given
            val response = """{"invalid json"}"""
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(response)

            server.enqueue(mockedResponse)

            // When
            val exception = shouldThrow<HttpException> {
                iamOAuth2.refreshToken()
            }

            exception.code shouldBe 500
            exception.message shouldStartWith "Unexpected JSON token at offset 14: Expected semicolon"
        }
    }

    @Nested
    inner class RevokeToken : Setup() {
        @Test
        fun `Should revoke the token when the current token is valid`(): Unit = runBlocking {
            // Given
            val response = buildJsonObject {
                put("scope", "")
                put("access_token", "")
                put("refresh_token", "")
                put("expires_in", 0)
                put("token_type", "")
                put("id_token", "")
            }
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(response.toString())

            server.enqueue(mockedResponse)

            // When
            val token = iamOAuth2.revokeToken()

            // Then
            token.accessToken shouldBe ""
            token.scopes shouldBe ""
            token.tokenType shouldBe ""
            token.expiresIn shouldBe 0
            token.refreshToken shouldBe ""
            token.idToken shouldBe ""
            token.timestamp shouldBe 0

            // Then
            val request = server.takeRequest()
            request.requestUrl?.encodedPath shouldBe "/authorize/oauth2/revoke"
            request.method shouldBe "POST"
            request.body.toString() shouldContain "token=22a34a6e-214c-4e3e-b85f-b4bbd1448613"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf(
                    "Basic ${
                        Base64.getEncoder().encodeToString("clientId:clientSecret".toByteArray())
                    }"
                ),
                "api-version" to listOf("2"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
        }

        @Test
        fun `Should throw an IllegalParameterException when the current token is not valid`(): Unit = runBlocking {
            // Given
            val iamOAuth2 = IamOAuth2(
                server.url("").toString(),
                clientId = "clientId",
                clientSecret = "clientSecret",
                httpClient = httpClient,
                initialToken = Token(accessToken = "")
            )

            // When
            val exception = shouldThrow<IllegalArgumentException> {
                iamOAuth2.revokeToken()
            }

            // Then
            exception.message shouldBe "The token must be valid."
        }
    }

    @Nested
    inner class Introspect : Setup() {
        private val subject = UUID.randomUUID()
        private val managingOrganization = UUID.randomUUID()
        private val expectedTokenMetadata = TokenMetadata(
            isActive = true,
            scopes = "foo bar",
            clientId = "clientId",
            userName = "username",
            tokenType = "tokenType",
            expirationTimeInEpochSeconds = 123,
            subject = subject,
            issuer = "issuer",
            identityType = "identityType",
            deviceType = "deviceType",
            organizations = Organizations(
                managingOrganization = managingOrganization,
                organizationList = listOf(
                    Organization(
                        organizationId = "organization id",
                        organizationName = "organization name",
                        disabled = false,
                        permissions = listOf("p1", "p2"),
                        effectivePermissions = listOf("ep1"),
                        roles = listOf("role1"),
                        groups = listOf("group1", "group2")
                    )
                )
            ),
            tokenTypeHint = "tokenTypeHint",
            clientOrganizationId = "clientOrganizationId",
            actor = Actor(
                sub = "sub"
            )
        )
        private val hsdpResponse = """{
            "active": true,
            "scope": "foo bar",
            "client_id": "clientId",
            "username": "username",
            "token_type": "tokenType",
            "exp": 123,
            "sub": "$subject",
            "iss": "issuer",
            "identity_type": "identityType",
            "device_type": "deviceType",
            "organizations": {
                "managingOrganization": "$managingOrganization",
                "organizationList": [
                    {
                        "organizationId": "organization id",
                        "organizationName": "organization name",
                        "disabled": false,
                        "permissions": ["p1","p2"],
                        "effectivePermissions": ["ep1"],
                        "roles": ["role1"],
                        "groups": ["group1", "group2"]
                    }
                ]
            },
            "token_type_hint": "tokenTypeHint",
            "client_organization_id": "clientOrganizationId",
            "act": {
                "sub": "sub"
            }
        }
        """.trimIndent()

        @Test
        fun `Should return token metadata when providing a valid token and having introspect permissions`(): Unit =
            runBlocking {
                // Given
                val mockedResponse = MockResponse()
                    .setResponseCode(200)
                    .setBody(hsdpResponse)

                server.enqueue(mockedResponse)

                // When
                val tokenMetadata = iamOAuth2.introspect(iamOAuth2.token.accessToken)

                // Then
                tokenMetadata shouldBe expectedTokenMetadata

                val request = server.takeRequest()
                request.requestUrl?.encodedPath shouldBe "/authorize/oauth2/introspect"
                request.method shouldBe "POST"
                request.body.toString() shouldContain "token=22a34a6e-214c-4e3e-b85f-b4bbd1448613"
                request.headers.toMultimap() shouldContainAll mapOf(
                    "authorization" to listOf(
                        "Basic ${
                            Base64.getEncoder().encodeToString("clientId:clientSecret".toByteArray())
                        }"
                    ),
                    "api-version" to listOf("4"),
                    "accept" to listOf("application/json; charset=utf-8"),
                )
            }

        @Test
        fun `Should throw an IllegalParameterException when the supplied access token is empty`(): Unit = runBlocking {
            // Given
            val accessToken = ""

            // When
            val exception = shouldThrow<IllegalArgumentException> {
                iamOAuth2.introspect(accessToken)
            }

            // Then
            exception.message shouldBe "Provided access token must not be empty."
        }

        @Test
        fun `Should throw a HttpException when the server responds with invalid JSON`(): Unit = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("""{"invalid json"}""")

            server.enqueue(mockedResponse)

            // When
            val exception = shouldThrow<HttpException> {
                iamOAuth2.introspect(iamOAuth2.token.accessToken)
            }

            // Then
            exception.code shouldBe 500
            exception.message shouldStartWith "Unexpected JSON token at offset 14: Expected semicolon"
        }
    }

    @Nested
    inner class UserInfoTest : Setup() {
        private val expectedUserInfo = UserInfo(
            subject = "subject",
            name = "name",
            givenName = "givenName",
            familyName = "familyName",
            email = "email",
            address = PostalAddress(
                formatted = "formatted",
                streetAddress = "streetAddress",
                postalCode = "postalCode",
            ),
            updatedAtInEpochSeconds = 123L,
        )
        private val hsdpResponse = """{
            "sub": "subject",
            "name": "name",
            "given_name": "givenName",
            "family_name": "familyName",
            "email": "email",
            "address": {
                "formatted": "formatted",
                "street_address": "streetAddress",
                "postal_code": "postalCode"
            },
            "updated_at": 123
        }
        """.trimIndent()

        @Test
        fun `Should return user info when current access token is valid`(): Unit = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(hsdpResponse)

            server.enqueue(mockedResponse)

            // When
            val tokenMetadata = iamOAuth2.userInfo()

            // Then
            tokenMetadata shouldBe expectedUserInfo

            val request = server.takeRequest()
            request.requestUrl?.encodedPath shouldBe "/authorize/oauth2/userinfo"
            request.method shouldBe "GET"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer 22a34a6e-214c-4e3e-b85f-b4bbd1448613"),
                "api-version" to listOf("2"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
        }

        @Test
        fun `Should throw an IllegalParameterException when current access token is empty`(): Unit = runBlocking {
            // Given
            val iamOAuth2 = IamOAuth2(
                server.url("").toString(),
                clientId = "clientId",
                clientSecret = "clientSecret",
                httpClient = httpClient,
                initialToken = Token(accessToken = "")
            )

            // When
            val exception = shouldThrow<IllegalArgumentException> {
                iamOAuth2.userInfo()
            }

            // Then
            exception.message shouldBe "The access token must not be empty."
        }

        @Test
        fun `Should throw a HttpException when the server responds with invalid JSON`(): Unit = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("""{"invalid json"}""")

            server.enqueue(mockedResponse)

            // When
            val exception = shouldThrow<HttpException> {
                iamOAuth2.userInfo()
            }

            // Then
            exception.code shouldBe 500
            exception.message shouldStartWith "Unexpected JSON token at offset 14: Expected semicolon"
        }

        @Test
        fun `Should throw a HttpException when the server does not respond`(): Unit = runBlocking {
            // Given
            val mockedResponse = MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)

            server.enqueue(mockedResponse)

            // When/Then
            val exception = shouldThrow<HttpException> {
                iamOAuth2.userInfo()
            }
            server.takeRequest()

            exception.code shouldBe 500
            exception.message shouldBe "timeout"
        }
    }
}
