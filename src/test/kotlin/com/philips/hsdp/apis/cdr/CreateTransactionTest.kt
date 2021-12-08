/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import com.philips.hsdp.apis.cdr.domain.sdk.*
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CreateTransactionTest : CdrTestBase() {

    private val basicRequest = BatchOrTransactionRequest(
        body = """{"key":"value"}""",
    )
    private val mockSuccessResponse = MockResponse()
        .setResponseCode(200)
        .setBody("""{"key":"value"}""")

    @Test
    fun `Should return the response when CDR returns a 200 with the specified headers`(): Unit = runBlocking {
        // Given
        server.enqueue(mockSuccessResponse)

        // When
        val result = cdr.createBatchOrTransaction(basicRequest)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/"
        request.requestUrl?.encodedQuery should beNull()
        request.method shouldBe "POST"
        request.headers.toMultimap() shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
            "Content-Type" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
        )
        result shouldBe BatchOrTransactionResponse(200, """{"key":"value"}""")
    }

    @TestFactory
    fun `FormatParameter value should be propagated to query parameters`() = listOf(
        FormatParameter.Json to "_format=json",
        FormatParameter.ApplicationJson to "_format=application%2Fjson",
        FormatParameter.ApplicationFhirJson to "_format=application%2Ffhir%2Bjson",
        FormatParameter.Xml to "_format=xml",
        FormatParameter.ApplicationXml to "_format=application%2Fxml",
        FormatParameter.ApplicationFhirXml to "_format=application%2Ffhir%2Bxml",
    ).map { (format, expectedQueryParameter) ->
        DynamicTest.dynamicTest("Value $format should lead to query parameter $expectedQueryParameter") {
            runBlocking {
                // Given
                server.enqueue(mockSuccessResponse)

                // When
                cdr.createBatchOrTransaction(basicRequest.copy(format = format))

                // Then
                val request = server.takeRequest()
                request.requestUrl?.encodedQuery shouldContain expectedQueryParameter
            }
        }
    }

    @TestFactory
    fun `ReturnPreference value should be propagated to the request headers`() = listOf(
        ReturnPreference.Minimal to "return=minimal",
        ReturnPreference.OperationOutcome to "return=OperationOutcome",
        ReturnPreference.Representation to "return=representation",
    ).map { (preference, expectedPreference) ->
        DynamicTest.dynamicTest("Value $preference should lead to query parameter $expectedPreference") {
            runBlocking {
                // Given
                server.enqueue(mockSuccessResponse)

                // When
                cdr.createBatchOrTransaction(basicRequest.copy(preference = preference))

                // Then
                val request = server.takeRequest()
                request.headers["Prefer"] shouldBe expectedPreference
            }
        }
    }
}