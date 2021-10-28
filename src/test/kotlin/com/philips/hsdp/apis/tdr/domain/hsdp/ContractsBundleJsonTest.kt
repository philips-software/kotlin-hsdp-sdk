/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import com.philips.hsdp.apis.tdr.TDR
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Test

class ContractsBundleJsonTest {
    @Test
    fun `An actual TDR response for GetContracts is parsed successfully`() {
        val contractsBundlePayload = javaClass.getResource("/tdr/GetContractsResponse.json")?.readText() ?: throw Exception("Resource not found")

        val parsedContractsBundle: Bundle = TDR.json.decodeFromString(contractsBundlePayload)
        parsedContractsBundle.total shouldBe 27
    }
}
