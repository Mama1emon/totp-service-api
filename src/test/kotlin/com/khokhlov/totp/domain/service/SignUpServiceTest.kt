package com.khokhlov.totp.domain.service

import com.khokhlov.totp.adapter.output.UserRepository
import com.khokhlov.totp.domain.model.UserAlreadyExistException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.doThrow
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class SignUpServiceTest {

    @Mock
    private lateinit var signUpService: SignUpService

    @Mock
    private lateinit var userRepository: UserRepository

//    @Mock
//    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun signUp() {
        `when`(userRepository.existsByUsernameEquals(anyString())).thenReturn(true)

        doThrow(UserAlreadyExistException()).`when`(signUpService).signUp("username", "password", true)
    }
}