package com.khokhlov.totp.domain.service

import com.khokhlov.totp.adapter.input.port.SignInUseCase
import com.khokhlov.totp.adapter.output.UserRepository
import com.khokhlov.totp.config.AuthUserDetail
import com.khokhlov.totp.config.UserAuthentication
import com.khokhlov.totp.domain.model.AuthenticationFlow
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SignInService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository
) : SignInUseCase {

    override fun signIn(username: String, password: String): AuthenticationFlow {
        val user = userRepository.findByUsernameEquals(username)
        if (user != null) {
            val isCorrectPassword = passwordEncoder.matches(password, user.hashedPassword)
            if (isCorrectPassword && user.finishedRegistration == true) {
                if (user.secret?.isNotBlank() == true) {

                    if (user.requiredAdditionalSecurity == true) {
                        return AuthenticationFlow.TOTP_ADDITIONAL_SECURITY
                    }

                    return AuthenticationFlow.TOTP
                }
            }
            SecurityContextHolder.getContext().authentication = UserAuthentication(
                AuthUserDetail(
                    userId = requireNotNull(user.id),
                    username = requireNotNull(user.username),
                    secret = requireNotNull(user.secret)
                )
            )
            return AuthenticationFlow.AUTHENTICATED
        }
//        else {
//            passwordEncoder.matches(password, passwordEncoder.encode("userNotFoundPassword"))
//        }

        return AuthenticationFlow.NOT_AUTHENTICATED
    }

    private companion object {
        val USER_AUTHENTICATION_OBJECT = "USER_AUTHENTICATION_OBJECT"
    }
}