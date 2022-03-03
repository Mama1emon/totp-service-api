package com.khokhlov.totp.config

import org.jboss.aerogear.security.otp.api.Base32
import org.jboss.aerogear.security.otp.api.Digits
import org.jboss.aerogear.security.otp.api.Hash
import org.jboss.aerogear.security.otp.api.Hmac
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

/**
 * Проверить одноразовый пароль [totpCode] в интервале от [pastIntervals] до [futureIntervals]
 */
internal fun String.verify(totpCode: String, pastIntervals: Int, futureIntervals: Int): VerificationResult {
    var valid = false
    var shift: Long = 0
    val code = totpCode.toInt()
    val currentInterval = System.currentTimeMillis() / 1000 / 30
    val expectedResponse = hash(
        secret = this,
        interval = currentInterval
    )
    if (expectedResponse == code) {
        valid = true
    }
    for (i in 1..pastIntervals) {
        val pastResponse = hash(
            secret = this,
            interval = currentInterval - i
        )
        if (pastResponse == code) {
            valid = true
            shift = -i.toLong()
        }
    }
    for (i in 1..futureIntervals) {
        val futureResponse = hash(
            secret = this,
            interval = currentInterval + i
        )
        if (futureResponse == code) {
            valid = true
            shift = i.toLong()
        }
    }
    return VerificationResult(valid, shift)
}

/**
 * Проверить одноразовые пароли [totpCodes] в интервале от [pastIntervals] до [futureIntervals]
 */
internal fun String.verify(totpCodes: List<String>, pastIntervals: Long, futureIntervals: Long): VerificationResult {
    val codes = totpCodes.map(Integer::valueOf).toList()
    var shift: Long = 0
    val currentInterval = System.currentTimeMillis() / 1000 / 30
    val first = codes[0]
    for (i in -pastIntervals..futureIntervals) {
        val generated = hash(
            secret = this,
            interval = currentInterval + i
        )
        if (first == generated) {
            var codesOkay = true
            shift = i
            for (j in 1 until codes.size) {
                val next = hash(
                    secret = this,
                    interval = currentInterval + i + j
                )
                if (next != codes[j]) {
                    codesOkay = false
                    break
                }
            }
            return VerificationResult(codesOkay, shift)
        }
    }
    return VerificationResult(false, shift)
}

private fun hash(secret: String, interval: Long): Int {
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
    return bytesToInt(hash)
}

private fun bytesToInt(hash: ByteArray): Int {
    // put selected bytes into result int
    val offset: Int = hash[hash.size - 1].toInt() and 0xf
    val binary: Int = hash[offset].toInt() and 0x7f shl 24 or
            (hash[offset + 1].toInt() and 0xff shl 16) or
            (hash[offset + 2].toInt() and 0xff shl 8) or
            (hash[offset + 3].toInt() and 0xff)
    return binary % Digits.SIX.value
}