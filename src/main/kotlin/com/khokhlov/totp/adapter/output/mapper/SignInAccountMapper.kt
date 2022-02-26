package com.khokhlov.totp.adapter.output.mapper

import com.khokhlov.totp.adapter.output.model.User
import com.khokhlov.totp.domain.model.SignInAccount

/**
 * Конвертер из дата [User] модели в домейн [SignInAccount] модель
 */
object SignInAccountMapper {

    /**
     * Конвертация модели [from]
     */
    fun convert(from: User) = SignInAccount(
        userId = requireNotNull(from.id),
        username = requireNotNull(from.username),
        hashedPassword = requireNotNull(from.hashedPassword),
        secret = from.secret,
        requiredAdditionalSecurity = requireNotNull(from.requiredAdditionalSecurity),
        finishedRegistration = requireNotNull(from.finishedRegistration)
    )
}