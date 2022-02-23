package com.khokhlov.totp.adapter.output.model

import javax.persistence.*

/**
 * Модель БД - пользователь
 *
 * @property id                         уникальный идентификатор
 * @property username                   уникальное имя пользователя
 * @property hashedPassword             зашифрованный пароль пользователя
 * @property secret                     секретный код необходимый для 2fa
 * @property finishedRegistration       флаг, указывающий на успешное прохождение регистрации
 * @property requiredAdditionalSecurity флаг, указывающий на необходимость прохождения дополнительных этапов проверки.
 *                                      По-умолчанию принимает значение false.
 */
@Entity
@Table(name = "users")
open class User() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "username", unique = true, nullable = false)
    open var username: String? = null

    @Column(name = "password")
    open var hashedPassword: String? = null

    @Column(name = "secret", length = 16)
    open var secret: String? = null

    @Column(name = "enabled", nullable = false)
    open var finishedRegistration: Boolean? = false

    @Column(name = "additional_security", nullable = false)
    open var requiredAdditionalSecurity: Boolean? = null

    constructor(
        username: String,
        hashedPassword: String,
        finishedRegistration: Boolean,
        secret: String?,
        requiredAdditionalSecurity: Boolean
    ) : this() {
        this.username = username
        this.hashedPassword = hashedPassword
        this.finishedRegistration = finishedRegistration
        this.secret = secret
        this.requiredAdditionalSecurity = requiredAdditionalSecurity
    }
}