package com.visura.domain.usecase.authentication


import com.visura.domain.repositories.authentication.FireBaseClientRepository
import com.visura.domain.repositories.authentication.GoogleClientRepository
import com.visura.domain.vo.authentication.Email
import com.visura.domain.vo.authentication.Password
import javax.inject.Inject

class AuthenticationUseCase @Inject constructor(
    val defaultFireBaseClientRepository: FireBaseClientRepository,
    val defaultGoogleClientRepository: GoogleClientRepository
) {
    suspend fun signIn(email: Email, password: Password) =
        defaultFireBaseClientRepository.signIn(email, password)

    suspend fun signUp(email: Email, password: Password) =
        defaultFireBaseClientRepository.signUp(email, password)

    suspend fun signInWithGoogle() =
        defaultGoogleClientRepository.signInWithGoogle()

    suspend fun signUpWithGoogle() =
        defaultGoogleClientRepository.signUpWithGoogle()

    suspend fun signOut() {
        defaultFireBaseClientRepository.signOut()
        defaultGoogleClientRepository.signOut()
    }
}