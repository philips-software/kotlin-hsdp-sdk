/****************************************************************************
 * Copyright (c) 1998-2010 AOL Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.philips.hsdp.apis.support

import java.io.IOException
import java.security.PrivateKey
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.spec.RSAPrivateCrtKeySpec
import java.security.spec.InvalidKeySpecException
import java.security.spec.EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.math.BigInteger
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

/**
 * Class for reading RSA private key from PEM formatted text.
 *
 * It can read PEM files with PKCS#8 or PKCS#1 encodings.
 * It doesn't support encrypted PEM files.
 */
internal class PrivateKeyReader(private val keyString: String) {
    /**
     * Read the PEM string and return the key
     *
     * @return PrivateKey
     * @throws IOException
     */
    @Throws(IOException::class)
    fun read(): PrivateKey {
        val factory: KeyFactory = try {
            KeyFactory.getInstance("RSA") //$NON-NLS-1$
        } catch (e: NoSuchAlgorithmException) {
            throw IOException("JCE error: " + e.message) //$NON-NLS-1$
        }
        when {
            keyString.contains(P1_BEGIN_MARKER) -> {
                val keyBytes = getPrivateKeyBytes(keyString, P1_BEGIN_MARKER, P1_END_MARKER)
                val keySpec = getRSAKeySpec(keyBytes)
                return try {
                    factory.generatePrivate(keySpec)
                } catch (e: InvalidKeySpecException) {
                    throw IOException("Invalid PKCS#1 PEM file: " + e.message) //$NON-NLS-1$
                }
            }
            keyString.contains(P8_BEGIN_MARKER) -> {
                val keyBytes = getPrivateKeyBytes(keyString, P8_BEGIN_MARKER, P8_END_MARKER)
                val keySpec: EncodedKeySpec = PKCS8EncodedKeySpec(keyBytes)
                return try {
                    factory.generatePrivate(keySpec)
                } catch (e: InvalidKeySpecException) {
                    throw IOException("Invalid PKCS#8 PEM file: " + e.message) //$NON-NLS-1$
                }
            }
            else -> throw IOException("Invalid PEM file: no begin marker") //$NON-NLS-1$
        }
    }

    private fun getPrivateKeyBytes(keyString: String, beginMarker: String, endMarker: String): ByteArray {
        val key = keyString
            .replace(beginMarker, "")
            .replace(endMarker, "")
            .replace(Regex("\\s"), "")
        return Base64.getDecoder().decode(key)
    }

    /**
     * Convert PKCS#1 encoded private key into RSAPrivateCrtKeySpec.
     *
     * The ASN.1 syntax for the private key with CRT is
     *
     * <pre>
     * --
     * -- Representation of RSA private key with information for the CRT algorithm.
     * --
     * RSAPrivateKey ::= SEQUENCE {
     * version           Version,
     * modulus           INTEGER,  -- n
     * publicExponent    INTEGER,  -- e
     * privateExponent   INTEGER,  -- d
     * prime1            INTEGER,  -- p
     * prime2            INTEGER,  -- q
     * exponent1         INTEGER,  -- d mod (p-1)
     * exponent2         INTEGER,  -- d mod (q-1)
     * coefficient       INTEGER,  -- (inverse of q) mod p
     * otherPrimeInfos   OtherPrimeInfos OPTIONAL
     * }
     * </pre>
     *
     * @param keyBytes PKCS#1 encoded key
     * @return KeySpec
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun getRSAKeySpec(keyBytes: ByteArray?): RSAPrivateCrtKeySpec {
        var parser = DerParser(keyBytes)
        val sequence = parser.read()
        if (sequence.type != DerParser.SEQUENCE)  {
            throw IOException("Invalid DER: not a sequence")
        } //$NON-NLS-1$

        // Parse inside the sequence
        parser = sequence.parser
        parser.read() // Skip version
        val modulus = parser.read().integer
        val publicExp = parser.read().integer
        val privateExp = parser.read().integer
        val prime1 = parser.read().integer
        val prime2 = parser.read().integer
        val exp1 = parser.read().integer
        val exp2 = parser.read().integer
        val crtCoef = parser.read().integer
        return RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef)
    }

    companion object {
        // Private key file using PKCS #1 encoding
        const val P1_BEGIN_MARKER = "-----BEGIN RSA PRIVATE KEY-----" //$NON-NLS-1$
        const val P1_END_MARKER = "-----END RSA PRIVATE KEY-----" //$NON-NLS-1$

        // Private key file using PKCS #8 encoding
        const val P8_BEGIN_MARKER = "-----BEGIN PRIVATE KEY-----" //$NON-NLS-1$
        const val P8_END_MARKER = "-----END PRIVATE KEY-----" //$NON-NLS-1$
    }
}

/**
 * A bare-minimum ASN.1 DER decoder, just having enough functions to
 * decode PKCS#1 private keys. Especially, it doesn't handle explicitly
 * tagged types with an outer tag.
 *
 * This parser can only handle one layer. To parse nested constructs,
 * get a new parser for each layer using `Asn1Object.getParser()`.
 *
 * There are many DER decoders in JRE but using them will tie this
 * program to a specific JCE/JVM.
 */
internal class DerParser(private val derEncodedInputStream: InputStream) {
    /**
     * Create a new DER decoder from a byte array.
     *
     * @param bytes
     * encoded bytes
     * @throws IOException
     */
    constructor(bytes: ByteArray?) : this(ByteArrayInputStream(bytes))

    /**
     * Read next object. If it's constructed, the value holds
     * encoded content, and it should be parsed by a new
     * parser from `Asn1Object.getParser`.
     *
     * @return A object
     * @throws IOException
     */
    @Throws(IOException::class)
    fun read(): Asn1Object {
        val tag = derEncodedInputStream.read()
        if (tag == -1) throw IOException("Invalid DER: stream too short, missing tag") //$NON-NLS-1$
        val length = length
        val value = ByteArray(length)
        val n = derEncodedInputStream.read(value)
        if (n < length) throw IOException("Invalid DER: stream too short, missing value") //$NON-NLS-1$
        return Asn1Object(tag, length, value)
    } //$NON-NLS-1$

    /**
     * Decode the length of the field. Can only support length encoding up to 4 octets.
     *
     * In BER/DER encoding, length can be encoded in 2 forms:
     *  * Short form. One octet. Bit 8 has value "0" and bits 7-1 give the length.
     *  * Long form. Two to 127 octets (only 4 is supported here).
     * Bit 8 of first octet has value "1" and bits 7-1 give the number of additional length octets.
     * Second and following octets give the length, base 256, most significant digit first.
     *
     * @return The length as integer
     * @throws IOException
     */
    @get:Throws(IOException::class)
    private val length: Int
        get() {
            val i = derEncodedInputStream.read()
            if (i == -1) throw IOException("Invalid DER: length missing") //$NON-NLS-1$

            // A single byte short length
            if (i and 0x7F.inv() == 0) return i
            val num = i and 0x7F

            // We can't handle length longer than 4 bytes
            if (i >= 0xFF || num > 4) throw IOException("Invalid DER: length field too big ($i)") //$NON-NLS-1$
            val bytes = ByteArray(num)
            val n = derEncodedInputStream.read(bytes)
            if (n < num) throw IOException("Invalid DER: length too short") //$NON-NLS-1$
            return BigInteger(1, bytes).toInt()
        }

    companion object {
        // Classes
        const val UNIVERSAL = 0x00
        const val APPLICATION = 0x40
        const val CONTEXT = 0x80
        const val PRIVATE = 0xC0

        // Constructed Flag
        const val CONSTRUCTED = 0x20

        // Tag and data types
        const val ANY = 0x00
        const val BOOLEAN = 0x01
        const val INTEGER = 0x02
        const val BIT_STRING = 0x03
        const val OCTET_STRING = 0x04
        const val NULL = 0x05
        const val OBJECT_IDENTIFIER = 0x06
        const val REAL = 0x09
        const val ENUMERATED = 0x0a
        const val RELATIVE_OID = 0x0d
        const val SEQUENCE = 0x10
        const val SET = 0x11
        const val NUMERIC_STRING = 0x12
        const val PRINTABLE_STRING = 0x13
        const val T61_STRING = 0x14
        const val VIDEOTEX_STRING = 0x15
        const val IA5_STRING = 0x16
        const val GRAPHIC_STRING = 0x19
        const val ISO646_STRING = 0x1A
        const val GENERAL_STRING = 0x1B
        const val UTF8_STRING = 0x0C
        const val UNIVERSAL_STRING = 0x1C
        const val BMP_STRING = 0x1E
        const val UTC_TIME = 0x17
        const val GENERALIZED_TIME = 0x18
    }
}

/**
 * An ASN.1 TLV. The object is not parsed. It can only handle integers and strings.
 *
 * Construct an ASN.1 TLV. The TLV could be either a constructed or primitive entity.
 *
 * The first byte in DER encoding is made of following fields,
 * <pre>
 * -------------------------------------------------
 * |Bit 8|Bit 7|Bit 6|Bit 5|Bit 4|Bit 3|Bit 2|Bit 1|
 * -------------------------------------------------
 * |  Class    | CF  |     +      Type             |
 * -------------------------------------------------
 * </pre>
 *
 *  * Class: Universal, Application, Context or Private
 *  * CF: Constructed flag. If 1, the field is constructed.
 *  * Type: This is actually called tag in ASN.1. It
 * indicates data type (Integer, String) or a construct (sequence, choice, set).
 *
 * @param tag Tag or Identifier
 * @param length Length of the field
 * @param value Encoded octet string for the field.
 */
internal class Asn1Object(private val tag: Int, private val length: Int, private val value: ByteArray) {
    val type: Int = tag and 0x1F
    private val isConstructed: Boolean
        get() = tag and DerParser.CONSTRUCTED == DerParser.CONSTRUCTED//$NON-NLS-1$

    /**
     * For constructed field, return a parser for its content.
     *
     * @return A parser for the construct.
     * @throws IOException
     */
    @get:Throws(IOException::class)
    val parser: DerParser
        get() {
            if (!isConstructed) throw IOException("Invalid DER: can't parse primitive entity") //$NON-NLS-1$
            return DerParser(value)
        }//$NON-NLS-1$

    /**
     * Get the value as integer
     *
     * @return BigInteger
     * @throws IOException
     */
    @get:Throws(IOException::class)
    val integer: BigInteger
        get() {
            if (type != DerParser.INTEGER) throw IOException("Invalid DER: object is not integer") //$NON-NLS-1$
            return BigInteger(value)
        }//$NON-NLS-1$

    /**
     * Get value as string. Most strings are treated
     * as Latin-1.
     *
     * @return Java string
     * @throws IOException
     */
    @get:Throws(IOException::class)
    val string: String
        get() {
            val encoding: Charset = when (type) {
                DerParser.NUMERIC_STRING,
                DerParser.PRINTABLE_STRING,
                DerParser.VIDEOTEX_STRING,
                DerParser.IA5_STRING,
                DerParser.GRAPHIC_STRING,
                DerParser.ISO646_STRING,
                DerParser.GENERAL_STRING -> Charsets.ISO_8859_1 //"ISO-8859-1" //$NON-NLS-1$
                DerParser.BMP_STRING -> Charsets.UTF_16BE //"UTF-16BE" //$NON-NLS-1$
                DerParser.UTF8_STRING -> Charsets.UTF_8  //$NON-NLS-1$
                DerParser.UNIVERSAL_STRING -> throw IOException("Invalid DER: can't handle UCS-4 string") //$NON-NLS-1$
                else -> throw IOException("Invalid DER: object is not a string") //$NON-NLS-1$
            }
            return String(value, encoding)
        }
}