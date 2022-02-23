package com.khokhlov.totp.domain.service

import com.khokhlov.totp.adapter.input.port.SignUpUseCase
import com.khokhlov.totp.adapter.output.UserRepository
import com.khokhlov.totp.adapter.output.mapper.SignUpAccountMapper
import com.khokhlov.totp.domain.model.SignUpAccount
import com.khokhlov.totp.domain.model.UserAlreadyExistException
import org.jboss.aerogear.security.otp.Totp
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
    private val userRepository: UserRepository
) : SignUpUseCase {

    override fun signUp(username: String, password: String, enableTotp: Boolean): SignUpAccount {
        // Проверка на существование пользователя с таким именем
        if (userRepository.existsByUsernameEquals(username)) {
            throw UserAlreadyExistException()
        }

        val account = SignUpAccount(username, passwordEncoder.encode(password), enableTotp)
        userRepository.save(SignUpAccountMapper.convert(account))

        return account
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