package com.visura.domain.repositories.authentication

interface GoogleClientRepository {
    suspend fun signUpWithGoogle()
    suspend fun signInWithGoogle()
    suspend fun signOut()
}