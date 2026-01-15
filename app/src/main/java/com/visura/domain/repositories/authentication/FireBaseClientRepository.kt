package com.visura.domain.repositories.authentication

import com.visura.domain.vo.authentication.Email
import com.visura.domain.vo.authentication.Password

interface FireBaseClientRepository {
    suspend fun signUp(email: Email, password: Password)
    suspend fun signIn(email: Email, password: Password)
    suspend fun signOut()
}