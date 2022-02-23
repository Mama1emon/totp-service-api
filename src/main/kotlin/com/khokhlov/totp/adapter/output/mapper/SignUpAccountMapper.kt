package com.khokhlov.totp.adapter.output.mapper

import com.khokhlov.totp.adapter.output.model.User
import com.khokhlov.totp.domain.model.SignUpAccount

/**
 * Конвертер из домейн модели [SignUpAccount] в дата [User] модель
 */
object SignUpAccountMapper {

    /**
     * Конвертация модели [from]
     */
    fun convert(from: SignUpAccount) = User(
        username = from.username,
        hashedPassword = from.hashedPassword,
        finishedRegistration = from.finishedRegistration,
        secret = from.secret,
        requiredAdditionalSecurity = false
    )
}