package com.khokhlov.totp.domain.model

import org.jboss.aerogear.security.otp.api.Base32
import java.util.regex.Pattern

/**
 * Модель пользователя для регистрации
 *
 * @property username       имя пользователя
 * @property hashedPassword захешированный пароль
 * @param enableTotp        флаг, включения 2fa аунтефикации
 */
class SignUpAccount(
    val username: String,
    val hashedPassword: String,
    enableTotp: Boolean
) {
    /**
     * Секрет в формате [Base32] для отображения QR-кода, если пользователь подключает 2fa
     */
    val secret: String? = if (enableTotp) Base32.random() else null

    /**
     * Регистрация завершена, если пользователь подключает 2fa
     */
    val finishedRegistration = !enableTotp

    companion object {

        /**
         * Валидация пароля [password] требованиям безопасности
         */
        fun validatePassword(password: String) {
            if (!Pattern.matches("^(?=.*[A-Z]+)(?=.*[!@#$&*_-]+)(?=.*[0-9]+)(?=.*[a-z]+).{8,}$", password)) {
                throw WeakPasswordException()
            }
        }
    }
}
