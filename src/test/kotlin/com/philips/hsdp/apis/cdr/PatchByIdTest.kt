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
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PatchByIdTest : CdrTestBase() {

    private val basicRequest = PatchByIdRequest(
        resourceType = "Patient",
        id = "id",
        body = """{"key":"value"}""",
        contentType = PatchContentType.JsonPatchPlusJson,
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
        val result = cdr.patch(basicRequest)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/Patient/id"
        request.requestUrl?.encodedQuery should beNull()
        request.method shouldBe "PATCH"
        request.headers.toMultimap() shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
            "Content-Type" to listOf("application/json-patch+json; charset=utf-8"),
        )
        result shouldBe PatchResponse(200, """{"key":"value"}""", "location", "abc", "2021-09-21T17:11:39Z")
    }

    @TestFactory
    fun `ContentType value should be propagated to request headers`() = listOf(
        PatchContentType.FhirJson to "application/fhir+json; charset=utf-8",
        PatchContentType.JsonPatchPlusJson to "application/json-patch+json; charset=utf-8",
        PatchContentType.FhirXml to "application/fhir+xml; charset=utf-8",
        PatchContentType.XmlPatchPlusXml to "application/xml-patch+xml; charset=utf-8",
    ).map { (contentType, expectedContentType) ->
        DynamicTest.dynamicTest("Value $contentType should lead to query parameter $expectedContentType") {
            runBlocking {
                // Given
                server.enqueue(mockSuccessResponse)

                // When
                cdr.patch(basicRequest.copy(contentType = contentType))

                // Then
                val request = server.takeRequest()
                request.headers["Content-Type"] shouldBe expectedContentType
            }
        }
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
                cdr.patch(basicRequest.copy(validate = validate))

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
        cdr.patch(basicRequest.copy(forVersion = "abc"))

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
                cdr.patch(basicRequest.copy(format = format))

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
                cdr.patch(basicRequest.copy(preference = preference))
                val request = server.takeRequest()

                // Then
                request.headers["Prefer"] shouldBe expectedPreference
            }
        }
    }

}