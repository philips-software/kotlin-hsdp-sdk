/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import com.philips.hsdp.apis.tdr.domain.hsdp.*
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class ResourceCreationResultDtoTest {
    @Test
    fun `Supports polymorphic deserialization when providing a JSON string with a success and failure entry`() {
        val body = """{
                "type": "batch",
                "resourceType": "Bundle",
                "total": 2,
                "entry": [
                    {
                        "status": 201,
                        "location": "url1",
                        "etag": "1",
                        "lastModified": "2021-09-20T16:01:02.023Z"
                    },
                    {
                        "status": 409,
                        "outcome": {
                            "issue": [
                                {
                                    "severity": "error",
                                    "code": "invalid",
                                    "details": {
                                        "text": "The DataItem could not be created because it already exists."
                                    }
                                }
                            ],
                            "resourceType": "OperationOutcome"
                        }
                    }
                ]
            }""".trimIndent()

        val result = Json.decodeFromString(BatchCreateResponseBundle.serializer(), body)
        result shouldBe BatchCreateResponseBundle(
            type = "batch",
            resourceType = ResourceType.Bundle,
            total = 2,
            entry = listOf(
                BatchCreatedResource(
                    status = 201,
                    location = "url1",
                    etag = "1",
                    lastModified = "2021-09-20T16:01:02.023Z",
                ),
                BatchCreationFailure(
                    status = 409,
                    outcome = CreationOutcome(
                        issue = listOf(
                            Issue(
                                severity = Severity.Error,
                                code = IssueCode.Invalid,
                                details = CodeableConcept("The DataItem could not be created because it already exists.")
                            )
                        ),
                        resourceType = ResourceType.OperationOutcome
                    )
                ),
            )
        )
    }
}
