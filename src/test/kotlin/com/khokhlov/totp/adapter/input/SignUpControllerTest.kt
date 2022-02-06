package com.khokhlov.totp.adapter.input

import com.khokhlov.totp.adapter.input.model.SignUpResponse
import com.khokhlov.totp.adapter.input.model.Status
import com.khokhlov.totp.adapter.input.port.SignUpUseCase
import com.khokhlov.totp.domain.model.UserAlreadyExistException
import com.khokhlov.totp.domain.model.WeakPasswordException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.`when`
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * Тест контроллера регистрации
 */
@ExtendWith(MockitoExtension::class)
internal class SignUpControllerTest {

    @InjectMocks
    private lateinit var signUpController: SignUpController

    @Mock
    private lateinit var signUpService: SignUpUseCase

    @BeforeEach
    fun setUp() {
        val request = MockHttpServletRequest()
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request))
    }

    @Test
    fun `signUp without 2fa`() {
        `when`(signUpService.signUp("", "", false)).thenReturn(null)

        val expected = SignUpResponse(Status.OK)
        val actual = signUpController.signUp("", "", false)

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `signUp with 2fa`() {
        val username = "username"
        val secret = "secret"

        `when`(signUpService.signUp(username, "", true)).thenReturn(secret)

        val expected = SignUpResponse(Status.OK, username, secret)
        val actual = signUpController.signUp(username, "", true)

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `signUp with already exists username`() {
        `when`(signUpService.signUp("", "", true)).thenThrow(UserAlreadyExistException())

        val expected = SignUpResponse(Status.USERNAME_TAKEN)
        val actual = signUpController.signUp("", "", true)

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `signUp with weak password`() {
        `when`(signUpService.signUp("", "", true)).thenThrow(WeakPasswordException())

        val expected = SignUpResponse(Status.WEAK_PASSWORD)
        val actual = signUpController.signUp("", "", true)

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `signUp confirm secret with correct code`() {
        `when`(signUpService.confirmSecret("", "")).thenReturn(true)

        val expected = true
        val actual = signUpController.signUpConfirmSecret("", "")

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `signUp confirm secret with incorrect code`() {
        `when`(signUpService.confirmSecret("", "")).thenReturn(false)

        val expected = false
        val actual = signUpController.signUpConfirmSecret("", "")

        assertThat(expected).isEqualTo(actual)
    }
}