/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.user.domain.sdk.*
import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SelfRegisterUserTest : IamUserTestBase() {

    @Test
    fun `Should return user's location when a new user was created successfully`(): Unit = runBlocking {
        // Given
        val newUserResponse = """
        {
            "resourceType": "OperationOutcome",
            "issue": [
                {
                    "severity": "information",
                    "code": "informational",
                    "details": {
                        "text": "User Created Successfully and activated."
                    }
                }
            ]
        }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(201)
            .setHeader("Location", "/authorize/identity/User/$userId")
            .setBody(newUserResponse)

        server.enqueue(mockedResponse)

        // When
        val result = iamUser.registerUser(newUser)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User"
        request.method shouldBe "POST"
        val headers = request.headers.toMultimap()
        headers shouldContainAll mapOf(
            "api-version" to listOf("3"),
            "content-type" to listOf("application/json; charset=utf-8"),
            "accept" to listOf("application/json; charset=utf-8"),
        )
        headers shouldHaveKey "hsdp-api-signature"
        headers shouldHaveKey "signeddate"
        request.body.readUtf8() shouldEqualJson expectedRequestBody
        result shouldBe UserLocation("/authorize/identity/User/$userId")
    }

    @Test
    fun `Should return null when creating a user that already exists`(): Unit = runBlocking {
        // Given
        val existingUserResponse = """
        {
            "resourceType": "OperationOutcome",
            "issue": [
                {
                    "severity": "information",
                    "code": "informational",
                    "details": {
                        "text": "User account already activated."
                    }
                }
            ]
        }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(existingUserResponse)

        server.enqueue(mockedResponse)

        // When
        val result = iamUser.registerUser(newUser)

        // Then
        server.takeRequest()
        result shouldBe null
    }

    @Test
    fun `Should throw when the HSDP reports a status 400 due to a missing Api-Version header`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(400)
            .setBody(missingApiVersionHeaderResponse)

        server.enqueue(mockedResponse)

        // When
        val exception = shouldThrowExactly<HttpException> {
            iamUser.registerUser(newUser)
        }

        // Then
        server.takeRequest()
        exception.message shouldBe missingApiVersionHeaderResponse
    }

    @Test
    fun `Should return nothing when Content-Type header is missing`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(415)

        server.enqueue(mockedResponse)

        // When
        val exception = shouldThrowExactly<HttpException> {
            iamUser.registerUser(newUser)
        }

        // Then
        server.takeRequest()
        exception.code shouldBe 415
        exception.message shouldBe ""
    }

    private val newUser = SelfCreateUser(
        loginId = loginId,
        password = "password",
        name = UserName(
            family = "family",
            given = "given",
        ),
        mobile = "mobile-number",
        email = "email@address",
        addresses = listOf(
            Address(
                use = AddressUse.Home,
                text = "text",
                lines = listOf("line 1", "line 2"),
                city = "city",
                state = "state",
                postalCode = "postalCode",
                country = "country"
            )
        ),
        preferredLanguage = "en",
        preferredCommunicationChannel = "EMAIL",
        isAgeValidated = true,
    )

    private val expectedRequestBody = """
    {
        "resourceType": "Person",
        "isAgeValidated": "true",
        "preferredLanguage": "en",
        "preferredCommunicationChannel": "EMAIL",
        "loginId": "$loginId",
        "password": "password",
        "name": {
            "family": "family",
            "given": "given"
        },
        "telecom": [
            {
                "system": "email",
                "value": "email@address"
            },
            {
                "system": "mobile",
                "value": "mobile-number"
            }
        ],
        "address": [
            {
                "use": "home",
                "text": "text",
                "line": [ "line 1", "line 2" ],
                "city": "city",
                "state": "state",
                "postalCode": "postalCode",
                "country": "country"
            }
        ]
    }
    """.trimIndent()
}