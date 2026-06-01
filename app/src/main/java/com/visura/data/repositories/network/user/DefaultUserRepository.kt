package com.visura.data.repositories.network.user

import com.visura.data.source.network.user.UserRemoteDataSource
import com.visura.domain.repositories.user.User
import com.visura.domain.repositories.user.UserRepository
import com.visura.domain.vo.user.UserId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun save(user: User): UserId =
        remoteDataSource.save(user)

    override suspend fun findById(id: UserId): User =
        remoteDataSource.findById(id)

    override suspend fun findByEmail(email: String): User? =
        remoteDataSource.findByEmail(email)

    override fun listAll(): Flow<List<User>> =
        remoteDataSource.listAll()

    override suspend fun delete(id: UserId) =
        remoteDataSource.delete(id)
}