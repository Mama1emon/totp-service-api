package com.khokhlov.totp.config

import org.springframework.security.core.GrantedAuthority

/**
 * Информация о пользователе, используемая для аунтефикации
 *
 * @property userId   уникальный идентификатор пользователя
 * @property username уникальное имя пользователя
 * @property secret   секретный код для 2fa
 */
data class AuthUserDetail(
    val userId: Long,
    val username: String,
    val secret: String
) {
    /**
     * Набор привилегий пользователя
     */
    val authoritiesSet: Set<GrantedAuthority> = setOf()
}
