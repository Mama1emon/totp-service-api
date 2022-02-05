package com.khokhlov.totp.config

import org.springframework.security.core.Authentication

/**
 * Объект аунтефикации пользователя
 */
data class UserAuthentication(private val userDetail: AuthUserDetail) : Authentication {

    override fun getName() = userDetail.username

    override fun getAuthorities() = userDetail.authoritiesSet

    override fun getCredentials() = null

    override fun getDetails() = null

    override fun getPrincipal() = userDetail

    override fun isAuthenticated() = true

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw UnsupportedOperationException("This authentication object is always authenticated")
    }
}
