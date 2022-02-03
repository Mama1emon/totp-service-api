package com.khokhlov.totp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Компонент, запускающий приложение
 */
@SpringBootApplication
class Application

/**
 * Функция запуска приложения
 */
fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
