package com.visura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.visura.domain.exceptions.authentication.AuthenticationException
import com.visura.domain.vo.authentication.Email
import com.visura.domain.vo.authentication.Password
import com.visura.domain.usecase.authentication.AuthenticationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInState(
    val email: Email = Email(""),
    val password: Password = Password(""),
    val showPassword: Boolean = false,
    val emailLoading: Boolean = false,
    val googleLoading: Boolean = false
)

sealed interface SignInEvent {
    data object Success : SignInEvent
    data class Error(val exception: AuthenticationException) : SignInEvent
}

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val auth: AuthenticationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _event = Channel<SignInEvent>()
    val event = _event.receiveAsFlow()

    fun setEmail(email: Email) {
        _state.update { it.copy(email = email) }
    }

    fun setPassword(password: Password) {
        _state.update { it.copy(password = password) }
    }

    fun togglePassword() {
        _state.update { it.copy(showPassword = !it.showPassword) }
    }

    fun signInWithEmail() {
        viewModelScope.launch {
            _state.update { it.copy(emailLoading = true) }
            try {
                _event.send(performEmailSignIn())
            } finally {
                _state.update { it.copy(emailLoading = false) }
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _state.update { it.copy(googleLoading = true) }
            try {
                _event.send(performGoogleSignIn())
            } finally {
                _state.update { it.copy(googleLoading = false) }
            }
        }
    }

    private suspend fun performEmailSignIn(): SignInEvent {
        return validateInputs()
            .mapCatching { (email, password) ->
                auth.signIn(email, password)
            }
            .fold(
                onSuccess = { SignInEvent.Success },
                onFailure = { SignInEvent.Error(it.toAuthException()) }
            )
    }

    private suspend fun performGoogleSignIn(): SignInEvent {
        return runCatching {
            auth.signInWithGoogle()
        }.fold(
            onSuccess = { SignInEvent.Success },
            onFailure = { SignInEvent.Error(it.toAuthException()) }
        )
    }

    private fun validateInputs(): Result<Pair<Email, Password>> = runCatching {
        val email = Email.of(_state.value.email.value).getOrThrow()
        val password = Password.access(_state.value.password.value).getOrThrow()
        email to password
    }

    private fun Throwable.toAuthException(): AuthenticationException = when (this) {
        is AuthenticationException -> this
        else -> AuthenticationException.UnexpectedError(this)
    }
}