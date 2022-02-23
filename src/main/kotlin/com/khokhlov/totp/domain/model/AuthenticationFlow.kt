package com.khokhlov.totp.domain.model

/**
 * Состояния аунтефикации
 */
enum class AuthenticationFlow {
    /**
     * Не аунтефицирован
     */
    NOT_AUTHENTICATED,

    /**
     * Аунтефицирован
     */
    AUTHENTICATED,

    /**
     * Двухфакторная аунтефикация
     */
    TOTP,

    /**
     * Требуется пройти дополнительный этап проверки для аунтефикации
     */
    TOTP_ADDITIONAL_SECURITY
}