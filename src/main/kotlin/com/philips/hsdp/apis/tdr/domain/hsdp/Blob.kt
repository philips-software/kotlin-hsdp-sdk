/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

@Serializable(with = BlobAsBase64StringSerializer::class)
data class Blob(val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Blob

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }
}

object BlobAsBase64StringSerializer: KSerializer<Blob> {
    override fun deserialize(decoder: Decoder): Blob {
        val base64BlobString = decoder.decodeString()
        val data = Base64.getDecoder().decode(base64BlobString)
        return Blob(data)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Blob", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Blob) {
        val base64BlobString = Base64.getEncoder().encodeToString(value.data)
//        val base64BlobString = "blabla"
        encoder.encodeString(base64BlobString)
    }
}
