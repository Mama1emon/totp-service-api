package com.khokhlov.totp.adapter.output

import com.khokhlov.totp.adapter.output.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Репозиторий для работы с моделью [User]
 */
@Repository
interface UserRepository : CrudRepository<User, Long>