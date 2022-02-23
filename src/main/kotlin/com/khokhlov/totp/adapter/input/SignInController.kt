package com.khokhlov.totp.adapter.input

import com.khokhlov.totp.adapter.input.port.SignInUseCase
import org.springframework.web.bind.annotation.RestController

/**
 * Контроллер авторизации
 */
@RestController
class SignInController(private val signInService: SignInUseCase) {

    /**
     *
     */
//    @PostMapping("/signin")
//    fun signIn(
//        @RequestParam @NotBlank username: String,
//        @RequestParam @NotBlank password: String,
//        httpSession: HttpSession
//    ): ResponseEntity<AuthenticationFlow> {
//        val userAuthentication = AppUserAuthentication(detail)
//        return when(signInService.signIn(username, password)) {
//            AuthenticationFlow.AUTHENTICATED -> TODO()
//            AuthenticationFlow.TOTP -> TODO()
//            AuthenticationFlow.TOTP_ADDITIONAL_SECURITY -> TODO()
//            AuthenticationFlow.NOT_AUTHENTICATED -> ResponseEntity.ok().body(AuthenticationFlow.NOT_AUTHENTICATED)
//            else -> {}
//        }
//        if (appUserRecord != null) {
//            val pwMatches: Boolean = this.passwordEncoder.matches(
//                password,
//                appUserRecord.getPasswordHash()
//            )
//            if (pwMatches && appUserRecord.getEnabled().booleanValue()) {
//                val detail = AppUserDetail(appUserRecord)
//                val userAuthentication = AppUserAuthentication(detail)
//                if (ch.rasc.twofa.security.AuthController.isNotBlank(appUserRecord.getSecret())) {
//                    httpSession.setAttribute(
//                        ch.rasc.twofa.security.AuthController.USER_AUTHENTICATION_OBJECT,
//                        userAuthentication
//                    )
//                    return if (isUserInAdditionalSecurityMode(detail.getAppUserId())) {
//                        ResponseEntity.ok().body<AuthenticationFlow?>(AuthenticationFlow.TOTP_ADDITIONAL_SECURITY)
//                    } else ResponseEntity.ok().body<AuthenticationFlow?>(AuthenticationFlow.TOTP)
//                }
//                SecurityContextHolder.getContext().authentication = userAuthentication
//                return ResponseEntity.ok().body<AuthenticationFlow?>(AuthenticationFlow.AUTHENTICATED)
//            }
//        } else {
//            this.passwordEncoder.matches(password, this.userNotFoundEncodedPassword)
//        }
//        return ResponseEntity.ok().body<AuthenticationFlow?>(AuthenticationFlow.NOT_AUTHENTICATED)
//    }
}