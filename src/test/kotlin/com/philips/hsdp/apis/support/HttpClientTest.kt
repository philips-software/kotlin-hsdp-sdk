/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support

import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
/* ktlint-disable no-wildcard-imports */
import io.mockk.*
/* ktlint-enable no-wildcard-imports */
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class HttpClientTest {

    private val token: Token = mockk()
    private val tokenRefresher: TokenRefresher = mockk()
    private val httpClient: HttpClient = HttpClient()

    @BeforeAll
    fun setup() {
        httpClient.tokenRefresher = tokenRefresher
    }

    @BeforeEach
    fun init() {
        clearAllMocks()
    }

    @Test
    fun `Returns null when authenticate header is not present`() {
        // Given
        val request: Request = createRequest().build()
        val response: Response = createResponse().request(request).build()
        // When
        val result: Request? = httpClient.authenticate(null, response)
        // Then
        result shouldBe null
    }

    @Test
    fun `Returns null when authenticate header does not contain bearer`() {
        // Given
        val request: Request = createRequest().header("Authorization", "Something").build()
        val response: Response = createResponse().request(request).build()
        // When
        val result: Request? = httpClient.authenticate(null, response)
        // Then
        result shouldBe null
    }

    @Test
    fun `Use current access token when not expired`() {
        // Given
        val request: Request = createRequest().header("Authorization", "Bearer accessToken").build()
        val response: Response = createResponse().request(request).build()
        every { tokenRefresher.token } returns token
        every { token.accessToken } returns "some test"
        every { token.isExpired } returns false
        // When
        val result: Request? = httpClient.authenticate(null, response)
        // Then
        result?.header("Authorization") shouldBe "Bearer some test"
    }

    @Test
    fun `refresh access token when expired and a refresh token is available`() {
        // Given
        val request: Request = createRequest().header("Authorization", "Bearer accessToken").build()
        val response: Response = createResponse().request(request).build()
        every { tokenRefresher.token } returns token
        every { token.accessToken } returns "some test" andThen "another token"
        every { token.isExpired } returns true
        every { token.refreshToken } returns "some refresh token"
        coEvery { tokenRefresher.refreshToken() } returns token
        // When
        val result: Request? = httpClient.authenticate(null, response)
        // Then
        result?.header("Authorization") shouldBe "Bearer another token"
    }

    @Test
    fun `skip refreshing access token when expired and there is no refresh token, eg for service login`() {
        // Given
        val request: Request = createRequest().header("Authorization", "Bearer accessToken").build()
        val response: Response = createResponse().request(request).build()
        every { tokenRefresher.token } returns token
        every { token.accessToken } returns "some test" andThen "another token"
        every { token.isExpired } returns true
        every { token.refreshToken } returns ""
        coEvery { tokenRefresher.refreshToken() } returns token
        // When
        val result: Request? = httpClient.authenticate(null, response)
        // Then
        result should beNull()
    }

    private fun createResponse(): Response.Builder {
        return Response.Builder().code(200).protocol(Protocol.HTTP_1_1).message("mwah")
    }

    private fun createRequest(): Request.Builder {
        return Request.Builder().url("https://url")
    }
}
