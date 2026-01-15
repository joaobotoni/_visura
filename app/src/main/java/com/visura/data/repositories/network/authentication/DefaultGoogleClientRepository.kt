package com.visura.data.repositories.network.authentication

import com.visura.data.source.network.authentication.GoogleClientRemoteDataSource
import com.visura.domain.repositories.authentication.GoogleClientRepository
import javax.inject.Inject

class DefaultGoogleClientRepository @Inject constructor(private val googleClientRemoteDataSource: GoogleClientRemoteDataSource) :
    GoogleClientRepository {
    override suspend fun signInWithGoogle() = googleClientRemoteDataSource.signInWithGoogle()
    override suspend fun signUpWithGoogle() = googleClientRemoteDataSource.signUpWithGoogle()
    override suspend fun signOut() = googleClientRemoteDataSource.signOut();
}