/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import com.philips.hsdp.apis.tdr.TDR
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Test

class DataItemsBundleJsonTest {
    @Test
    fun `An actual TDR response for GetDataItems is parsed successfully`() {
        val dataItemsBundlePayload = javaClass.getResource("/tdr/GetDataItemsResponse.json")?.readText() ?: throw Exception("Resource not found")

        val parsedDataItemsBundle: Bundle = TDR.json.decodeFromString(dataItemsBundlePayload)
        parsedDataItemsBundle.total shouldBe 100
    }
}
