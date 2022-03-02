package com.khokhlov.totp.config

import org.jboss.aerogear.security.otp.api.Base32
import org.jboss.aerogear.security.otp.api.Hash
import org.jboss.aerogear.security.otp.api.Hmac
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

class CustomTotp(private val secret: String) {

    inner class Result(val isValid: Boolean, val shift: Long)

    fun verify(codeString: String, pastIntervals: Int, futureIntervals: Int): Result {
        var valid = false
        var shift: Long = 0
        val code = codeString.toInt()
        val currentInterval = System.currentTimeMillis() / 1000 / 30
        val expectedResponse = generate(currentInterval)
        if (expectedResponse == code) {
            valid = true
        }
        for (i in 1..pastIntervals) {
            val pastResponse = generate(currentInterval - i)
            if (pastResponse == code) {
                valid = true
                shift = -i.toLong()
            }
        }
        for (i in 1..futureIntervals) {
            val futureResponse = generate(currentInterval + i)
            if (futureResponse == code) {
                valid = true
                shift = i.toLong()
            }
        }
        return Result(valid, shift)
    }

    fun verify(codeStrings: List<String?>, pastIntervals: Long, futureIntervals: Long): Result {
        val codes = codeStrings.map(Integer::valueOf).toList()
        var shift: Long = 0
        val currentInterval = System.currentTimeMillis() / 1000 / 30
        val first = codes[0]
        for (i in -pastIntervals..futureIntervals) {
            val generated = generate(currentInterval + i)
            if (first == generated) {
                var codesOkay = true
                shift = i
                for (j in 1 until codes.size) {
                    val next = generate(currentInterval + i + j)
                    if (next != codes[j]) {
                        codesOkay = false
                        break
                    }
                }
                return Result(codesOkay, shift)
            }
        }
        return Result(false, shift)
    }

    private fun generate(interval: Long): Int = hash(interval)

    private fun hash(interval: Long): Int {
        var hash = ByteArray(0)
        try {
            hash = Hmac(Hash.SHA1, Base32.decode(secret), interval).digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: Base32.DecodingException) {
            e.printStackTrace()
        }
        return littleEndianConversion(hash)
    }

    companion object {
        private fun littleEndianConversion(bytes: ByteArray): Int {
            var result = 0
            for (i in bytes.indices) {
                result = result or (bytes[i].toInt() shl 8 * i)
            }
            return result
        }
    }
}