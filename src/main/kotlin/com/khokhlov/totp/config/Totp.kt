package com.khokhlov.totp.config

/**
 * Модель ответа на верификацию totp-кода
 *
 * @property isValid валидность кода
 * @property shift   количество 30-секундных интервалов, в которых существует код в прошлом или будущем
 */
data class VerificationResult(val isValid: Boolean, val shift: Long)
