/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.user.domain.sdk.ChangeLogin
import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class ChangeLoginIdTest : IamUserTestBase() {

    @Test
    fun `Should not throw when call to HSDP is successful`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(204)

        server.enqueue(mockedResponse)

        // When
        iamUser.changeLoginId(changeLogin)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User/${changeLogin.userId}/\$change-loginid"
        request.method shouldBe "POST"
        val headers = request.headers.toMultimap()
        headers shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("2"),
            "Content-Type" to listOf("application/json; charset=utf-8"),
            "accept" to listOf("application/json; charset=utf-8"),
        )
        request.body.readUtf8() shouldEqualJson expectedRequestBody
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
            iamUser.changeLoginId(changeLogin)
        }

        // Then
        server.takeRequest()
        exception.message shouldBe missingApiVersionHeaderResponse
    }

    private val changeLogin = ChangeLogin(
        userId = userId,
        loginId = "newLoginId",
    )

    private val expectedRequestBody = """
    {
        "loginId": "newLoginId"
    }
    """.trimIndent()
}