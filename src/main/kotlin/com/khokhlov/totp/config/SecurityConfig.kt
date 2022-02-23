package com.khokhlov.totp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler

/**
 * Конфиг защиты приложения
 */
@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Bean
    @Throws(Exception::class)
    override fun authenticationManager() = AuthenticationManager {
        throw AuthenticationServiceException("Cannot authenticate $it")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .authorizeRequests { registry ->
                registry.antMatchers(
                    "/authenticate", "/signin", "/verify-totp", "/verify-totp-additional-security", "/signup",
                    "/signup-confirm-secret"
                ).permitAll()
                registry.anyRequest().authenticated()
            }
            .logout { it.logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler()) }
    }

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
            "/", "/assets/**/*", "/svg/**/*", "/*.br", "/*.gz",
            "/*.html", "/*.js", "/*.css", "/*.woff2", "/*.ttf", "/*.eot",
            "/*.svg", "/*.woff", "/*.ico"
        )
    }

    /**
     * Функция шифрования на базе алгоритма Argon2
     */
    @Bean
    fun passwordEncoder() = Argon2PasswordEncoder(16, 32, 8, 1 shl 16, 4)
}