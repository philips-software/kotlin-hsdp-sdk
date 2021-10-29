/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr

import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.tdr.domain.hsdp.PatchDocument
/* ktlint-disable no-wildcard-imports */
import com.philips.hsdp.apis.tdr.domain.sdk.*
/* ktlint-enable no-wildcard-imports */
import com.philips.hsdp.apis.tdr.domain.sdk.query.ContractQuery
import com.philips.hsdp.apis.tdr.domain.sdk.query.DataItemDeleteQuery
import com.philips.hsdp.apis.tdr.domain.sdk.query.DataItemPatchQuery
import com.philips.hsdp.apis.tdr.domain.sdk.query.DataItemQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

class TDRJavaProxy(tdrUrl: String, httpClient: HttpClient) : AutoCloseable {
    private val tdr = TDR(tdrUrl, httpClient)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @JvmOverloads
    fun getDataItems(query: DataItemQuery, requestId: String? = null): CompletableFuture<DataItemsDto> =
        scope.future { tdr.getDataItems(query, requestId) }

    @JvmOverloads
    fun storeDataItem(dataItem: NewDataItemDto, requestId: String? = null): CompletableFuture<CreatedResourceDto> =
        scope.future { tdr.storeDataItem(dataItem, requestId) }

    @JvmOverloads
    fun patchDataItem(
        query: DataItemPatchQuery,
        patchDocuments: List<PatchDocument>,
        requestId: String? = null
    ): CompletableFuture<RequestIdDto> =
        scope.future { tdr.patchDataItem(query, patchDocuments, requestId) }

    @JvmOverloads
    fun deleteDataItem(query: DataItemDeleteQuery, requestId: String? = null): CompletableFuture<RequestIdDto> =
        scope.future { tdr.deleteDataItem(query, requestId) }

    @JvmOverloads
    fun storeDataItems(dataItems: NewDataItemsDto, requestId: String? = null): CompletableFuture<CreatedDataItemsDto> =
        scope.future { tdr.storeDataItems(dataItems, requestId) }

    @JvmOverloads
    fun storeContract(contract: NewContractDto, requestId: String? = null): CompletableFuture<CreatedResourceDto> =
        scope.future { tdr.storeContract(contract, requestId) }

    @JvmOverloads
    fun getContracts(query: ContractQuery, requestId: String? = null): CompletableFuture<ContractsDto> =
        scope.future { tdr.getContracts(query, requestId) }

    override fun close() {
        scope.cancel()
    }
}
