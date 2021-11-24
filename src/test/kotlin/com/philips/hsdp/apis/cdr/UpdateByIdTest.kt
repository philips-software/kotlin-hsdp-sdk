/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import com.philips.hsdp.apis.cdr.domain.sdk.FormatParameter
import com.philips.hsdp.apis.cdr.domain.sdk.ReturnPreference
import com.philips.hsdp.apis.cdr.domain.sdk.UpdateByIdRequest
import com.philips.hsdp.apis.cdr.domain.sdk.UpdateResponse
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UpdateByIdTest : CdrTestBase() {

    private val basicRequest = UpdateByIdRequest(
        resourceType = "Patient",
        id = "id",
        body = """{"key":"value"}""",
    )
    private val mockSuccessResponse = MockResponse()
        .setResponseCode(200)
        .setHeader("ETag", """W/"abc"""")
        .setHeader("Last-Modified", "Tue, 21 Sep 2021 17:11:39 GMT")
        .setBody("""{"key":"value"}""")

    @Test
    fun `Should return the response when CDR returns a 200 with the specified headers`(): Unit = runBlocking {
        // Given
        server.enqueue(mockSuccessResponse)

        // When
        val result = cdr.update(basicRequest)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/Patient/id"
        request.requestUrl?.encodedQuery should beNull()
        request.method shouldBe "PUT"
        request.headers.toMultimap() shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
            "Content-Type" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
        )
        result shouldBe UpdateResponse(200, """{"key":"value"}""", null, "abc", "2021-09-21T17:11:39Z")
    }

    @Test
    fun `Should return the location when CDR returns it in the headers`(): Unit = runBlocking {
        // Given
        val mockResponseWithLocation = MockResponse()
            .setResponseCode(200)
            .setHeader("Location", "location")
            .setHeader("ETag", """W/"abc"""")
            .setHeader("Last-Modified", "Tue, 21 Sep 2021 17:11:39 GMT")
            .setBody("""{"key":"value"}""")
        server.enqueue(mockResponseWithLocation)

        // When
        val result = cdr.update(basicRequest)

        // Then
        server.takeRequest()
        result shouldBe UpdateResponse(200, """{"key":"value"}""", "location", "abc", "2021-09-21T17:11:39Z")
    }

    @TestFactory
    fun `Validate value - if set - should be propagated to headers`() = listOf(
        false to "false",
        true to "true",
    ).map { (validate, expectedHeaderValue) ->
        DynamicTest.dynamicTest("Value $validate should lead to header parameter $expectedHeaderValue") {
            runBlocking {
                // Given
                server.enqueue(mockSuccessResponse)

                // When
                cdr.update(basicRequest.copy(validate = validate))

                // Then
                val request = server.takeRequest()
                request.headers["X-validate-resource"] shouldBe expectedHeaderValue
            }
        }
    }

    @Test
    fun `ForVersion value should be propagated to the request headers`(): Unit = runBlocking {
        // Given
        server.enqueue(mockSuccessResponse)

        // When
        cdr.update(basicRequest.copy(forVersion = "abc"))

        // Then
        val request = server.takeRequest()
        request.headers["If-Match"] shouldBe """W/"abc""""
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
                cdr.update(basicRequest.copy(format = format))

                // Then
                val request = server.takeRequest()
                request.requestUrl?.encodedQuery shouldBe expectedQueryParameter
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
                cdr.update(basicRequest.copy(preference = preference))

                // Then
                val request = server.takeRequest()
                request.headers["Prefer"] shouldBe expectedPreference
            }
        }
    }

}