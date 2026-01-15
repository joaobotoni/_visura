package com.visura.data.source.network.authentication

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.visura.domain.exceptions.authentication.AuthenticationException
import com.visura.domain.vo.authentication.Email
import com.visura.domain.vo.authentication.Password
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireBaseClientRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun signUp(email: Email, password: Password) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email.value, password.value).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw AuthenticationException.EmailAlreadyInUse(cause = e)
        } catch (e: FirebaseAuthWeakPasswordException) {
            throw AuthenticationException.WeakPassword(cause = e)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthenticationException.InvalidCredential(cause = e)
        } catch (e: FirebaseNetworkException) {
            throw AuthenticationException.NetworkError(cause = e)
        } catch (e: FirebaseAuthException) {
            throw AuthenticationException.UnknownAuthError(cause = e)
        } catch (e: Exception) {
            throw AuthenticationException.UnexpectedError(cause = e)
        }
    }

    suspend fun signIn(email: Email, password: Password) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email.value, password.value).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthenticationException.InvalidCredential(cause = e)
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthenticationException.UserNotFound(cause = e)
        } catch (e: FirebaseTooManyRequestsException) {
            throw AuthenticationException.TooManyRequests(cause = e)
        } catch (e: FirebaseNetworkException) {
            throw AuthenticationException.NetworkError(cause = e)
        } catch (e: FirebaseAuthException) {
            throw AuthenticationException.UnknownAuthError(cause = e)
        } catch (e: Exception) {
            throw AuthenticationException.UnexpectedError(cause = e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}