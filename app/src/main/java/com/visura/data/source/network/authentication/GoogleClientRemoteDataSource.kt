package com.visura.data.source.network.authentication

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.FirebaseNetworkException
import com.visura.domain.exceptions.authentication.AuthenticationException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleClientRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    private val credentialManager: CredentialManager = CredentialManager.create(context)
    private val webClientId: String = "853549966921-g1n0kcgbmmsnnltrr9c15gvme5mr90u0.apps.googleusercontent.com"

    suspend fun signInWithGoogle() {
        try {
            val credential = requestCredential(filterByAuthorizedAccounts = true, autoSelectEnabled = true)
            authenticateWithFirebase(credential)
        } catch (_: AuthenticationException.NoAccountFound) {
            signUpWithGoogle()
        }
    }

    suspend fun signUpWithGoogle() {
        val credential = requestCredential(filterByAuthorizedAccounts = false, autoSelectEnabled = false)
        authenticateWithFirebase(credential)
    }

    suspend fun signOut() {
        clearCredentialState()
        firebaseAuth.signOut()
    }

    private suspend fun requestCredential(
        filterByAuthorizedAccounts: Boolean,
        autoSelectEnabled: Boolean
    ): Credential {
        return try {
            val googleIdOption = buildGoogleIdOption(filterByAuthorizedAccounts, autoSelectEnabled)
            val request = buildCredentialRequest(googleIdOption)
            val response = getCredentialFromManager(request)
            response.credential
        } catch (e: GetCredentialCancellationException) {
            throw AuthenticationException.UserCancelled(cause = e)
        } catch (_: NoCredentialException) {
            throw AuthenticationException.NoAccountFound(provider = "Google")
        } catch (e: GetCredentialException) {
            throw AuthenticationException.NetworkError(cause = e)
        }
    }

    private fun buildGoogleIdOption(
        filterByAuthorizedAccounts: Boolean,
        autoSelectEnabled: Boolean
    ): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setAutoSelectEnabled(autoSelectEnabled)
            .build()
    }

    private fun buildCredentialRequest(googleIdOption: GetGoogleIdOption): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private suspend fun getCredentialFromManager(request: GetCredentialRequest): GetCredentialResponse {
        return credentialManager.getCredential(request = request, context = context)
    }

    private suspend fun authenticateWithFirebase(credential: Credential) {
        validateCredential(credential)
        val idToken = extractIdToken(credential)
        signInWithFirebase(idToken)
    }

    private fun validateCredential(credential: Credential) {
        val isValid = credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

        if (!isValid) {
            throw AuthenticationException.InvalidCredential()
        }
    }

    private fun extractIdToken(credential: Credential): String {
        return try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(
                (credential as CustomCredential).data
            )
            googleIdTokenCredential.idToken
        } catch (e: Exception) {
            throw AuthenticationException.InvalidCredential(cause = e)
        }
    }

    private suspend fun signInWithFirebase(idToken: String) {
        try {
            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(authCredential).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw AuthenticationException.EmailAlreadyInUse(cause = e)
        } catch (e: FirebaseNetworkException) {
            throw AuthenticationException.NetworkError(cause = e)
        } catch (e: FirebaseAuthException) {
            throw AuthenticationException.UnknownAuthError(cause = e)
        } catch (e: Exception) {
            throw AuthenticationException.SocialAuthenticationFailed(provider = "Google", cause = e)
        }
    }

    private suspend fun clearCredentialState() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}