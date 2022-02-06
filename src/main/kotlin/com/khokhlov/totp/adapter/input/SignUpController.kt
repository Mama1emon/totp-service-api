package com.khokhlov.totp.adapter.input

import com.khokhlov.totp.adapter.input.model.SignUpResponse
import com.khokhlov.totp.adapter.input.model.Status
import com.khokhlov.totp.adapter.input.port.SignUpUseCase
import com.khokhlov.totp.domain.model.UserAlreadyExistException
import com.khokhlov.totp.domain.model.WeakPasswordException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotBlank


/**
 * Контроллер регистрации пользователя
 *
 * @property signUpService сервис регистрации пользователя
 */
@RestController
class SignUpController(private val signUpService: SignUpUseCase) {

    /**
     * Запрос регистрации пользователя
     *
     * @param username   имя пользователя
     * @param password   пароль
     * @param enableTotp флаг, включения 2fa аунтефикации
     */
    @PostMapping("/signup")
    fun signUp(
        @RequestParam("username") @NotBlank username: String,
        @RequestParam("password") @NotBlank password: String,
        @RequestParam("totp") enableTotp: Boolean
    ): SignUpResponse {
        val secret: String?
        try {
            secret = signUpService.signUp(username, password, enableTotp)
        } catch (e: UserAlreadyExistException) {
            return SignUpResponse(Status.USERNAME_TAKEN)
        } catch (e: WeakPasswordException) {
            return SignUpResponse(Status.WEAK_PASSWORD)
        }

        return if (secret != null) {
            SignUpResponse(Status.OK, username, secret)
        } else {
            SignUpResponse(Status.OK)
        }
    }

    /**
     * Подтверждение добавления генератора одноразовых паролей в приложении
     *
     * @param username имя пользователя
     * @param code     одноразовый временный код
     *
     * @return true - проверка пройдена
     *         false - проверка не пройдена
     */
    @PostMapping("/signup-confirm-secret")
    fun signUpConfirmSecret(
        @RequestParam("username") @NotBlank username: String,
        @RequestParam("code") @NotBlank code: String
    ) = signUpService.confirmSecret(username, code)
}