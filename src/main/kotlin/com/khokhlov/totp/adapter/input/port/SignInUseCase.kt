package com.khokhlov.totp.adapter.input.port

import com.khokhlov.totp.domain.model.SignInAccount

/**
 * Интерфейс use-case авторизации
 */
interface SignInUseCase {

    /**
     * Получить данные о пользователе по имени [username], если он прошел регистрацию
     */
    fun getSignInInfo(username: String): SignInAccount?
}