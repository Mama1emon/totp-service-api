package com.khokhlov.totp.domain.service

import com.khokhlov.totp.adapter.input.port.SignInUseCase
import com.khokhlov.totp.adapter.output.UserRepository
import com.khokhlov.totp.adapter.output.mapper.SignInAccountMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 * Сервис для аунтефикации пользователя
 *
 * @property userRepository  репозиторий для работы с пользовательскими данными
 */
@Service
class SignInService(private val userRepository: UserRepository) : SignInUseCase {

    override fun getSignInInfo(username: String) = userRepository.findByUsernameEquals(username)?.let {
        SignInAccountMapper.convert(it)
    }

    override fun checkRequiredAdditionalSecurity(userId: Long) =
        userRepository.findByIdOrNull(userId)?.requiredAdditionalSecurity

    override fun setRequiredAdditionalSecurity(userId: Long) {
        userRepository.findByIdOrNull(userId)?.let { it.requiredAdditionalSecurity = true }
    }

    override fun clearRequiredAdditionalSecurity(userId: Long) {
        userRepository.findByIdOrNull(userId)?.let { it.requiredAdditionalSecurity = false }
    }
}