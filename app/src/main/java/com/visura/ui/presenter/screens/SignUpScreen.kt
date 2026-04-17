package com.visura.ui.presenter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.visura.R
import com.visura.domain.vo.authentication.Email
import com.visura.domain.vo.authentication.Password
import com.visura.ui.presenter.elements.button.StandardButton
import com.visura.ui.presenter.elements.button.StandardGoogleButton
import com.visura.ui.presenter.elements.button.StandardTextButton
import com.visura.ui.presenter.elements.divider.StandardLabeledDivider
import com.visura.ui.presenter.elements.field.EmailTextField
import com.visura.ui.presenter.elements.field.PasswordTextField
import com.visura.ui.presenter.elements.snackbar.SnackbarConfig
import com.visura.ui.presenter.elements.snackbar.SnackbarType
import com.visura.ui.presenter.elements.snackbar.StandardSnackbar
import com.visura.ui.presenter.theme.Alpha
import com.visura.ui.presenter.theme.ComponentSize
import com.visura.ui.presenter.theme.DemoTheme
import com.visura.ui.presenter.theme.FontSize
import com.visura.ui.presenter.theme.Spacing
import com.visura.ui.viewmodels.SignUpEvent
import com.visura.ui.viewmodels.SignUpState
import com.visura.ui.viewmodels.SignUpViewModel
import kotlinx.coroutines.flow.collectLatest

private data class SignUpEvents(
    val onEmailChange: (String) -> Unit,
    val onPasswordChange: (String) -> Unit,
    val onConfirmPasswordChange: (String) -> Unit,
    val onTogglePasswordVisibility: () -> Unit,
    val onToggleConfirmPasswordVisibility: () -> Unit,
    val onSignUpWithEmail: () -> Unit,
    val onSignUpWithGoogle: () -> Unit,
    val onSignInClick: () -> Unit
)

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    navSignIn: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarType by remember { mutableStateOf(SnackbarType.DEFAULT) }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            snackbarType = when (event) {
                is SignUpEvent.Success -> SnackbarType.SUCCESS
                is SignUpEvent.Error -> SnackbarType.ERROR
            }
            val message = when (event) {
                is SignUpEvent.Success -> "Cadastro realizado com sucesso!"
                is SignUpEvent.Error -> event.exception.message ?: "Erro desconhecido ao realizar cadastro."
            }
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    SignUpScreenContent(
        state = state,
        snackbar = SnackbarConfig(hostState = snackbarHostState, type = snackbarType),
        events = SignUpEvents(
            onEmailChange = { viewModel.setEmail(Email(it)) },
            onPasswordChange = { viewModel.setPassword(Password(it)) },
            onConfirmPasswordChange = { viewModel.setConfirm(Password(it)) },
            onTogglePasswordVisibility = viewModel::togglePassword,
            onToggleConfirmPasswordVisibility = viewModel::toggleConfirm,
            onSignUpWithEmail = viewModel::signUpWithEmail,
            onSignUpWithGoogle = viewModel::signUpWithGoogle,
            onSignInClick = navSignIn
        )
    )
}

@Composable
private fun SignUpScreenContent(
    state: SignUpState,
    snackbar: SnackbarConfig,
    events: SignUpEvents
) {
    DemoTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                snackbarHost = {
                    StandardSnackbar(hostState = snackbar.hostState, type = snackbar.type)
                }
            ) { paddingValues ->
                SignUpForm(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    events = events
                )
            }
        }
    }
}

@Composable
private fun SignUpForm(
    modifier: Modifier = Modifier,
    state: SignUpState,
    events: SignUpEvents
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.XXXLarge)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SignUpHeader()
        Spacer(modifier = Modifier.height(Spacing.Huge))
        SignUpInputFields(state = state, events = events)
        Spacer(modifier = Modifier.height(Spacing.Large))
        SignInLink(onClick = events.onSignInClick)
        Spacer(modifier = Modifier.height(Spacing.XLarge))
        StandardButton(
            text = stringResource(R.string.button_register),
            onClick = events.onSignUpWithEmail,
            enabled = !state.emailLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(ComponentSize.Medium)
        )
        Spacer(modifier = Modifier.height(Spacing.XXLarge))
        StandardLabeledDivider(
            label = stringResource(R.string.divider_text),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Spacing.XXLarge))
        StandardGoogleButton(
            text = stringResource(R.string.button_social_register),
            enabled = !state.googleLoading,
            onClick = events.onSignUpWithGoogle
        )
    }
}

@Composable
private fun SignUpHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Small)
    ) {
        Text(
            text = stringResource(R.string.header_sign_up),
            fontSize = FontSize.XLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.subheader_sign_up),
            fontSize = FontSize.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = Alpha.XHigh),
            textAlign = TextAlign.Center,
            lineHeight = FontSize.Large
        )
    }
}

@Composable
private fun SignUpInputFields(state: SignUpState, events: SignUpEvents) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.XLarge),
        modifier = Modifier.fillMaxWidth()
    ) {
        EmailTextField(
            value = state.email.value,
            onValueChange = events.onEmailChange
        )
        PasswordTextField(
            value = state.password.value,
            isVisible = state.showPassword,
            label = stringResource(R.string.field_label_password),
            placeholder = stringResource(R.string.field_placeholder_password),
            onValueChange = events.onPasswordChange,
            onToggleVisibility = events.onTogglePasswordVisibility
        )
        PasswordTextField(
            value = state.confirm.value,
            isVisible = state.showConfirm,
            label = stringResource(R.string.field_label_password_confirm),
            placeholder = stringResource(R.string.field_placeholder_password_confirm),
            onValueChange = events.onConfirmPasswordChange,
            onToggleVisibility = events.onToggleConfirmPasswordVisibility
        )
    }
}

@Composable
private fun SignInLink(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        StandardTextButton(text = stringResource(R.string.link_to_sign_in), onClick = onClick)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {
    DemoTheme {
        SignUpScreenContent(
            state = SignUpState(),
            snackbar = SnackbarConfig(SnackbarHostState(), SnackbarType.DEFAULT),
            events = SignUpEvents(
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onTogglePasswordVisibility = {},
                onToggleConfirmPasswordVisibility = {},
                onSignUpWithEmail = {},
                onSignUpWithGoogle = {},
                onSignInClick = {}
            )
        )
    }
}