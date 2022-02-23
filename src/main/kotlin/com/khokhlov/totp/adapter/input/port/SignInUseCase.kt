package com.khokhlov.totp.adapter.input.port

import com.khokhlov.totp.domain.model.AuthenticationFlow

/**
 * Интерфейс use-case авторизации
 */
interface SignInUseCase {

    /**
     * Метод авторизации с помощью [username] и [password]
     */
    fun signIn(username: String, password: String): AuthenticationFlow
}