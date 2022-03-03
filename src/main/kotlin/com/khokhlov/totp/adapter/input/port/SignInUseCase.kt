package com.khokhlov.totp.adapter.input.port

import com.khokhlov.totp.domain.model.SignInAccount
import org.springframework.transaction.annotation.Transactional

/**
 * Интерфейс use-case авторизации
 */
interface SignInUseCase {

    /**
     * Получить данные о пользователе по имени [username], если он прошел регистрацию
     */
    fun getSignInInfo(username: String): SignInAccount?

    /**
     * Проверить, что пользователю [userId] не требуется проходить доп. этапы проверки
     */
    fun checkRequiredAdditionalSecurity(userId: Long): Boolean?

    /**
     * Установить пользователю [userId] флаг для прохождения доп. этапов проверки
     */
    @Transactional
    fun setRequiredAdditionalSecurity(userId: Long)

    /**
     * Удалить у пользователя [userId] флаг для прохождения доп. этапов проверок
     */
    @Transactional
    fun clearRequiredAdditionalSecurity(userId: Long)
}