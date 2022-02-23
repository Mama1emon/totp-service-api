package com.khokhlov.totp.domain.model

import org.jboss.aerogear.security.otp.api.Base32
import java.util.regex.Pattern

class SignUpAccount(
    val username: String,
    val hashedPassword: String,
    val enableTotp: Boolean
) {
    val secret: String? = if (enableTotp) Base32.random() else null
    val requiredAdditionalSecurity = false
    val finishedRegistration = !enableTotp

    init {
        validatePassword(hashedPassword)
    }

    private fun validatePassword(password: String) {
        if (Pattern.matches("^(?=.*[A-Z]+)(?=.*[!@#$&*_-]+)(?=.*[0-9]+)(?=.*[a-z]+).{8,}$", password)) {
            throw WeakPasswordException()
        }
    }
}
