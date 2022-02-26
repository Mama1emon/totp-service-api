package com.khokhlov.totp.domain.model

/**
 * Модель с данными пользователя для аунтефикации
 *
 * @property userId                     уникальный идентификатор пользователя
 * @property username                   имя пользователя
 * @property hashedPassword             захешированный пароль
 * @property secret                     секретный код для 2fa. Может быть равен null, если пользователь не подключил 2fa
 * @property requiredAdditionalSecurity флаг, указывающий на необходимость прохождения дополнительных этапов проверки
 * @property finishedRegistration       флаг, включения 2fa аунтефикации
 */
data class SignInAccount(
    val userId: Long,
    val username: String,
    val hashedPassword: String,
    val secret: String?,
    val requiredAdditionalSecurity: Boolean,
    val finishedRegistration: Boolean
)