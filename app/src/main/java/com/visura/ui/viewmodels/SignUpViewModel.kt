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

data class SignUpState(
    val email: Email = Email(""),
    val password: Password = Password(""),
    val confirm: Password = Password(""),
    val showPassword: Boolean = false,
    val showConfirm: Boolean = false,
    val emailLoading: Boolean = false,
    val googleLoading: Boolean = false
)

sealed interface SignUpEvent {
    data object Success : SignUpEvent
    data class Error(val exception: AuthenticationException) : SignUpEvent
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: AuthenticationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _event = Channel<SignUpEvent>()
    val event = _event.receiveAsFlow()

    fun setEmail(email: Email) {
        _state.update { it.copy(email = email) }
    }

    fun setPassword(password: Password) {
        _state.update { it.copy(password = password) }
    }

    fun setConfirm(confirm: Password) {
        _state.update { it.copy(confirm = confirm) }
    }

    fun togglePassword() {
        _state.update { it.copy(showPassword = !it.showPassword) }
    }

    fun toggleConfirm() {
        _state.update { it.copy(showConfirm = !it.showConfirm) }
    }

    fun signUpWithEmail() {
        viewModelScope.launch {
            _state.update { it.copy(emailLoading = true) }
            try {
                _event.send(performEmailSignUp())
            } finally {
                _state.update { it.copy(emailLoading = false) }
            }
        }
    }

    fun signUpWithGoogle() {
        viewModelScope.launch {
            _state.update { it.copy(googleLoading = true) }
            try {
                _event.send(performGoogleSignUp())
            } finally {
                _state.update { it.copy(googleLoading = false) }
            }
        }
    }

    private suspend fun performEmailSignUp(): SignUpEvent {
        return validateInputs()
            .mapCatching { (email, password) ->
                auth.signUp(email, password)
            }
            .fold(
                onSuccess = { SignUpEvent.Success },
                onFailure = { SignUpEvent.Error(it.toAuthException()) }
            )
    }

    private suspend fun performGoogleSignUp(): SignUpEvent {
        return runCatching {
            auth.signUpWithGoogle()
        }.fold(
            onSuccess = { SignUpEvent.Success },
            onFailure = { SignUpEvent.Error(it.toAuthException()) }
        )
    }

    private fun validateInputs(): Result<Pair<Email, Password>> = runCatching {
        val email = Email.of(_state.value.email.value).getOrThrow()
        val password = Password.create(_state.value.password.value).getOrThrow()
        Password.confirm(_state.value.confirm.value, password.value).getOrThrow()
        email to password
    }

    private fun Throwable.toAuthException(): AuthenticationException = when (this) {
        is AuthenticationException -> this
        else -> AuthenticationException.UnexpectedError(this)
    }
}