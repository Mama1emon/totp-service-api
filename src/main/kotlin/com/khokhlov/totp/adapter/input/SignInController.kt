package com.khokhlov.totp.adapter.input

import com.khokhlov.totp.adapter.input.port.SignInUseCase
import com.khokhlov.totp.config.AuthUserDetail
import com.khokhlov.totp.config.UserAuthentication
import com.khokhlov.totp.domain.model.AuthenticationFlow
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession
import javax.validation.constraints.NotBlank

/**
 * Контроллер авторизации
 *
 * @property signInService   сервис аунтефикации пользователя
 * @property passwordEncoder энкодер для шифрования пароля
 */
@RestController
class SignInController(
    private val signInService: SignInUseCase,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * Аунтефицировать пользователя по имени [username] и паролю [password]
     */
    @PostMapping("/signin")
    fun signIn(
        @RequestParam @NotBlank username: String,
        @RequestParam @NotBlank password: String,
        httpSession: HttpSession
    ): ResponseEntity<AuthenticationFlow> {
        // Если пользователь не зарегистрирован, то вернуть AuthenticationFlow.NOT_AUTHENTICATED
        val account = signInService.getSignInInfo(username)
            ?: return ResponseEntity.ok(AuthenticationFlow.NOT_AUTHENTICATED)

        // Проверка, что пользователь успешно закончил регистрацию
        val userAuthentication = UserAuthentication(
            AuthUserDetail(account.userId, account.username, account.secret)
        )
        val isCorrectPassword = passwordEncoder.matches(password, account.hashedPassword)
        if (isCorrectPassword && account.finishedRegistration) {
            // Если пользователь подключил 2fa, то необходимо подтвердить одноразовый пароль
            return if (account.secret?.isNotBlank() == true) {
                ResponseEntity.ok(
                    getFlowFor2faUser(
                        account.requiredAdditionalSecurity,
                        httpSession,
                        userAuthentication
                    )
                )
            } else {
                SecurityContextHolder.getContext().authentication = userAuthentication
                ResponseEntity.ok(AuthenticationFlow.AUTHENTICATED)
            }
        }

        return ResponseEntity.ok(AuthenticationFlow.NOT_AUTHENTICATED)
    }

    /**
     * Вернуть результат обработки аунтефикационных данных для пользователя с подключенной 2FA
     */
    private fun getFlowFor2faUser(
        requiredAdditionalSecurity: Boolean,
        httpSession: HttpSession,
        userAuthentication: UserAuthentication
    ): AuthenticationFlow {
        httpSession.setAttribute(USER_AUTHENTICATION_OBJECT, userAuthentication)
        return if (requiredAdditionalSecurity) AuthenticationFlow.TOTP_ADDITIONAL_SECURITY else AuthenticationFlow.TOTP
    }

    companion object {
        private const val USER_AUTHENTICATION_OBJECT = "USER_AUTHENTICATION_OBJECT"
    }
}