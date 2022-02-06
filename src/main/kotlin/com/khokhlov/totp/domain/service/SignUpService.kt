package com.khokhlov.totp.domain.service

import com.codahale.passpol.PasswordPolicy
import com.codahale.passpol.Status
import com.khokhlov.totp.adapter.input.port.SignUpUseCase
import com.khokhlov.totp.adapter.output.UserRepository
import com.khokhlov.totp.adapter.output.model.User
import com.khokhlov.totp.domain.model.UserAlreadyExistException
import com.khokhlov.totp.domain.model.WeakPasswordException
import org.jboss.aerogear.security.otp.Totp
import org.jboss.aerogear.security.otp.api.Base32
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Сервис регистрации пользователя
 *
 * @property passwordEncoder энкодер для шифрования пароля
 * @property passwordPolicy  сущность для проверки пароля на соответствующие требования
 * @property userRepository  репозиторий для работы с пользовательскими данными
 */
@Service
class SignUpService(
    private val passwordEncoder: PasswordEncoder,
    private val passwordPolicy: PasswordPolicy,
    private val userRepository: UserRepository
) : SignUpUseCase {

    override fun signUp(username: String, password: String, enableTotp: Boolean): String? {
        // Проверка на существование пользователя с таким именем
        if (userRepository.existsByUsernameEquals(username)) {
            throw UserAlreadyExistException()
        }

        // Проверка пароля на заданные требования
        if (passwordPolicy.check(password) != Status.OK) {
            throw WeakPasswordException()
        }

        var secret: String? = null
        if (enableTotp) {
            secret = Base32.random()
            userRepository.save(
                User(
                    username = username,
                    hashedPassword = passwordEncoder.encode(password),
                    finishedRegistration = false,
                    secret = secret,
                    requiredAdditionalSecurity = false
                )
            )
        } else {
            userRepository.save(
                User(
                    username = username,
                    hashedPassword = passwordEncoder.encode(password),
                    finishedRegistration = true,
                    secret = null,
                    requiredAdditionalSecurity = false
                )
            )
        }

        return secret
    }

    override fun confirmSecret(username: String, code: String): Boolean {
        val user = userRepository.findByUsernameEquals(username)

        return if (user != null) {
            val totp = Totp(user.secret)
            if (totp.verify(code)) {
                user.finishedRegistration = true
            }
            true
        } else false
    }
}