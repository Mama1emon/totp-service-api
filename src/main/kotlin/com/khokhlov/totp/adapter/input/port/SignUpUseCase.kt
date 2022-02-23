package com.khokhlov.totp.adapter.input.port

/**
 * Интерфейс use-case регистрации пользователя
 */
interface SignUpUseCase {

    /**
     * Метод регистрации пользователя
     *
     * @param username   имя пользователя
     * @param password   пароль
     * @param enableTotp флаг, включения 2fa аунтефикации
     *
     * @return сгенерированный секрет
     */
    fun signUp(username: String, password: String, enableTotp: Boolean): String?

    /**
     * Подтверждение добавления генератора одноразовых паролей в приложении
     *
     * @param username имя пользователя
     * @param code     одноразовый временный код
     *
     * @return true - проверка пройдена
     *         false - проверка не пройдена
     */
    fun confirmSecret(username: String, code: String): Boolean
}