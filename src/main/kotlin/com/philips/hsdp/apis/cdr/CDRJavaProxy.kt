/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
@file:Suppress("unused")

package com.philips.hsdp.apis.cdr

/* ktlint-disable no-wildcard-imports */
import com.philips.hsdp.apis.cdr.domain.sdk.*
/* ktlint-enable no-wildcard-imports */
import com.philips.hsdp.apis.support.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

class CDRJavaProxy(cdrUrl: String, fhirVersion: String, mediaType: String, httpClient: HttpClient) : AutoCloseable {
    private val cdr = CDR(cdrUrl, fhirVersion, mediaType, httpClient)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun read(readRequest: ReadRequest): CompletableFuture<ReadResponse> =
        scope.future { cdr.read(readRequest) }

    fun read(readVersionRequest: ReadVersionRequest): CompletableFuture<ReadResponse> =
        scope.future { cdr.read(readVersionRequest) }

    fun search(searchRequest: SearchRequest, searchMethod: SearchMethod): CompletableFuture<SearchResponse> =
        scope.future { cdr.search(searchRequest, searchMethod) }

    fun create(createRequest: CreateRequest): CompletableFuture<CreateResponse> =
        scope.future { cdr.create(createRequest) }

    fun createBatchOrTransaction(
        batchOrTransactionRequest: BatchOrTransactionRequest
    ): CompletableFuture<BatchOrTransactionResponse> =
        scope.future { cdr.createBatchOrTransaction(batchOrTransactionRequest) }

    fun delete(deleteByIdRequest: DeleteByIdRequest): CompletableFuture<DeleteResponse> =
        scope.future { cdr.delete(deleteByIdRequest) }

    fun delete(deleteByQueryRequest: DeleteByQueryRequest): CompletableFuture<DeleteResponse> =
        scope.future { cdr.delete(deleteByQueryRequest) }

    fun update(updateByIdRequest: UpdateByIdRequest): CompletableFuture<UpdateResponse> =
        scope.future { cdr.update(updateByIdRequest) }

    fun update(updateByQueryRequest: UpdateByQueryRequest): CompletableFuture<UpdateResponse> =
        scope.future { cdr.update(updateByQueryRequest) }

    fun patch(patchByIdRequest: PatchByIdRequest): CompletableFuture<PatchResponse> =
        scope.future { cdr.patch(patchByIdRequest) }

    fun patch(patchByQueryRequest: PatchByQueryRequest): CompletableFuture<PatchResponse> =
        scope.future { cdr.patch(patchByQueryRequest) }

    override fun close() {
        scope.cancel()
    }
}
