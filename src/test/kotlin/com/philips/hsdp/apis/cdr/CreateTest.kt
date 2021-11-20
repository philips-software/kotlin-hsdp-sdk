/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import com.philips.hsdp.apis.cdr.domain.sdk.CreateRequest
import com.philips.hsdp.apis.cdr.domain.sdk.CreateResponse
import com.philips.hsdp.apis.cdr.domain.sdk.FormatParameter
import com.philips.hsdp.apis.cdr.domain.sdk.ReturnPreference
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
internal class CreateTest : CdrTestBase() {

    private val basicRequest = CreateRequest(
        resourceType = "Patient",
        body = """{"key":"value"}""",
    )
    private val mockSuccessResponse = MockResponse()
        .setResponseCode(200)
        .setHeader("Location", "location")
        .setHeader("ETag", """W/"abc"""")
        .setHeader("Last-Modified", "Tue, 21 Sep 2021 17:11:39 GMT")
        .setBody("""{"key":"value"}""")

    @Test
    fun `Should return the response when CDR returns a 200 with the specified headers`(): Unit = runBlocking {
        // Given
        server.enqueue(mockSuccessResponse)

        // When
        val result = cdr.create(basicRequest)
        val request = server.takeRequest()

        // Then
        request.requestUrl?.encodedPath shouldBe "/Patient"
        request.requestUrl?.encodedQuery should beNull()
        request.method shouldBe "POST"
        request.headers.toMultimap() shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
            "Content-Type" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
        )
        result shouldBe CreateResponse(200, """{"key":"value"}""", "location", "abc", "2021-09-21T17:11:39Z")
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
                cdr.create(basicRequest.copy(format = format))
                val request = server.takeRequest()

                // Then
                request.requestUrl?.encodedQuery shouldContain expectedQueryParameter
            }
        }
    }

    @Test
    fun `Condition value should be propagated to the request headers`(): Unit = runBlocking {
        // Given
        server.enqueue(mockSuccessResponse)

        // When
        cdr.create(basicRequest.copy(condition = "some.field=abc"))
        val request = server.takeRequest()

        // Then
        request.headers["If-None-Exists"] shouldBe "some.field=abc"
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
                cdr.create(basicRequest.copy(preference = preference))
                val request = server.takeRequest()

                // Then
                request.headers["Prefer"] shouldBe expectedPreference
            }
        }
    }

}