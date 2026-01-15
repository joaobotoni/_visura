package com.visura.data.repositories.network.authentication

import com.visura.data.source.network.authentication.FireBaseClientRemoteDataSource
import com.visura.domain.repositories.authentication.FireBaseClientRepository
import com.visura.domain.vo.authentication.Email
import com.visura.domain.vo.authentication.Password
import javax.inject.Inject

class DefaultFireBaseClientRepository @Inject constructor(
    private val fireBaseClientRemoteDataSource: FireBaseClientRemoteDataSource
) : FireBaseClientRepository {

    override suspend fun signUp(email: Email, password: Password) {
        fireBaseClientRemoteDataSource.signUp(email, password)
    }

    override suspend fun signIn(email: Email, password: Password) {
        fireBaseClientRemoteDataSource.signIn(email, password)
    }

    override suspend fun signOut() {
        fireBaseClientRemoteDataSource.signOut()
    }
}