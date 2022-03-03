package com.khokhlov.totp.adapter.input

import com.khokhlov.totp.adapter.input.model.AuthenticationFlow
import com.khokhlov.totp.adapter.input.port.SignInUseCase
import com.khokhlov.totp.config.AuthUserDetail
import com.khokhlov.totp.config.CustomTotp
import com.khokhlov.totp.config.UserAuthentication
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.validation.constraints.NotBlank
import kotlin.math.abs

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
     * Проверить аунтефикацию пользователя, используя данные из сессии запроса [request]
     */
    @GetMapping("/authenticate")
    fun authenticate(request: HttpServletRequest) =
        if (SecurityContextHolder.getContext().authentication is UserAuthentication) {
            AuthenticationFlow.AUTHENTICATED
        } else {
            request.getSession(false)?.let(HttpSession::invalidate)
            AuthenticationFlow.NOT_AUTHENTICATED
        }

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
                    getFlowFor2faUser(account.userId, httpSession, userAuthentication)
                )
            } else {
                SecurityContextHolder.getContext().authentication = userAuthentication
                ResponseEntity.ok(AuthenticationFlow.AUTHENTICATED)
            }
        }

        return ResponseEntity.ok(AuthenticationFlow.NOT_AUTHENTICATED)
    }

    /**
     * Проверить одноразовый код [code], используя аунтефикационные из сессии [httpSession]
     */
    @PostMapping("/verify-totp")
    fun verifyTotp(@RequestParam code: String?, httpSession: HttpSession): ResponseEntity<AuthenticationFlow> {
        val userAuthentication = httpSession.getAttribute(SESSION_KEY_USER_AUTHENTICATION) as? UserAuthentication
            ?: return ResponseEntity.ok().body(AuthenticationFlow.NOT_AUTHENTICATED)

        val authInfo = userAuthentication.principal
        if (signInService.checkRequiredAdditionalSecurity(authInfo.userId) == true) {
            return ResponseEntity.ok().body(AuthenticationFlow.TOTP_ADDITIONAL_SECURITY)
        }

        val secret = authInfo.secret
        if (secret?.isNotBlank() == true && code?.isNotBlank() == true) {
            val isValidCode = CustomTotp(secret).verify(code, 2, 2).isValid
            return if (isValidCode) {
                SecurityContextHolder.getContext().authentication = userAuthentication
                ResponseEntity.ok().body(AuthenticationFlow.AUTHENTICATED)
            } else {
                signInService.setRequiredAdditionalSecurity(authInfo.userId)
                ResponseEntity.ok().body(AuthenticationFlow.TOTP_ADDITIONAL_SECURITY)
            }
        }
        return ResponseEntity.ok().body(AuthenticationFlow.NOT_AUTHENTICATED)
    }

    /**
     * Проверить одноразовые кода [code1], [code2], [code3], введенные для прохождения доп. этапов проверки.
     * Также используется сессия [httpSession] для получения аунтефикационных данных пользователя.
     */
    @PostMapping("/verify-totp-additional-security")
    fun verifyTotpAdditionalSecurity(
        @RequestParam code1: String?,
        @RequestParam code2: String?,
        @RequestParam code3: String?,
        httpSession: HttpSession
    ): ResponseEntity<AuthenticationFlow> {
        val userAuthentication = httpSession.getAttribute(SESSION_KEY_USER_AUTHENTICATION) as? UserAuthentication
            ?: return ResponseEntity.ok().body(AuthenticationFlow.NOT_AUTHENTICATED)

        if (code1 == code2 || code1 == code3 || code2 == code3) {
            return ResponseEntity.ok().body(AuthenticationFlow.NOT_AUTHENTICATED)
        }

        val authInfo = userAuthentication.principal
        val secret = authInfo.secret
        if (secret?.isNotBlank() == true &&
            code1?.isNotBlank() == true && code2?.isNotBlank() == true && code3?.isNotBlank() == true
        ) {
            val totp = CustomTotp(secret)

            // check 25 hours into the past and future.
            val noOf30SecondsIntervals = TimeUnit.HOURS.toSeconds(25) / 30
            val result = totp.verify(listOf(code1, code2, code3), noOf30SecondsIntervals, noOf30SecondsIntervals)
            if (result.isValid) {
                if (result.shift > 2 || result.shift < -2) {
                    httpSession.setAttribute(SESSION_KEY_TOTP_SHIFT, result.shift)
                }
                signInService.clearRequiredAdditionalSecurity(authInfo.userId)
                httpSession.removeAttribute(SESSION_KEY_USER_AUTHENTICATION)
                SecurityContextHolder.getContext().authentication = userAuthentication
                return ResponseEntity.ok().body(AuthenticationFlow.AUTHENTICATED)
            }
        }
        return ResponseEntity.ok().body(AuthenticationFlow.NOT_AUTHENTICATED)
    }

    /**
     * Получить разницу во времени между часами клиента и пользователя,
     * используя аунтефикационные данные из сессии [httpSession]
     */
    @GetMapping("/totp-shift")
    fun getTotpShift(httpSession: HttpSession): String? {
        val shift = httpSession.getAttribute(SESSION_KEY_TOTP_SHIFT) as? Long ?: return null
        httpSession.removeAttribute(SESSION_KEY_TOTP_SHIFT)
        val out = StringBuilder()
        var total30Seconds = abs(shift).toInt().toLong()
        val hours = total30Seconds / 120
        total30Seconds %= 120
        val minutes = total30Seconds / 2
        val seconds = total30Seconds % 2 != 0L

        if (hours == 1L) {
            out.append("1 hour ")
        } else if (hours > 1) {
            out.append(hours).append(" hours ")
        }

        if (minutes == 1L) {
            out.append("1 minute ")
        } else if (minutes > 1) {
            out.append(minutes).append(" minutes ")
        }

        if (seconds) {
            out.append("30 seconds ")
        }

        return out.append(if (shift < 0) "behind" else "ahead").toString()
    }

    /**
     * Вернуть результат обработки аунтефикационных данных для пользователя с подключенной 2FA
     */
    private fun getFlowFor2faUser(
        userId: Long,
        httpSession: HttpSession,
        userAuthentication: UserAuthentication
    ): AuthenticationFlow {
        httpSession.setAttribute(SESSION_KEY_USER_AUTHENTICATION, userAuthentication)
        return if (signInService.checkRequiredAdditionalSecurity(userId) == true)
            AuthenticationFlow.TOTP_ADDITIONAL_SECURITY
        else AuthenticationFlow.TOTP
    }

    companion object {
        private const val SESSION_KEY_USER_AUTHENTICATION = "USER_AUTHENTICATION_OBJECT"
        private const val SESSION_KEY_TOTP_SHIFT = "totp-shift"
    }
}