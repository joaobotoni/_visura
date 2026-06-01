package com.visura.domain.repositories.user

import com.visura.domain.vo.user.UserId
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun save(user: User): UserId
    suspend fun findById(id: UserId): User
    suspend fun findByEmail(email: String): User?
    fun listAll(): Flow<List<User>>
    suspend fun delete(id: UserId)
}