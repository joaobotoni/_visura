package com.visura.domain.usecase.user

import com.visura.domain.exceptions.user.UserException
import com.visura.domain.repositories.user.User
import com.visura.domain.repositories.user.UserRepository
import com.visura.domain.vo.user.UserId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun save(user: User): UserId {
        validate(user)
        return runCatching {
            userRepository.save(user)
        }.getOrElse { throw UserException.NetworkError(it) }
    }

    suspend fun findById(id: UserId): User {
        return runCatching {
            userRepository.findById(id)
        }.getOrElse { throw UserException.NotFound(it) }
    }

    suspend fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    fun listAll(): Flow<List<User>> = userRepository.listAll()

    suspend fun delete(id: UserId) {
        runCatching {
            userRepository.delete(id)
        }.getOrElse { throw UserException.NetworkError(it) }
    }

    private fun validate(user: User) {
        if (user.email.isBlank())
            throw UserException.EmailRequired()
        if (user.password.isBlank())
            throw UserException.PasswordRequired()
    }
}