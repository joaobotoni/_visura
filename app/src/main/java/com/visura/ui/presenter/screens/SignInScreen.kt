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
import com.visura.ui.viewmodels.SignInEvent
import com.visura.ui.viewmodels.SignInState
import com.visura.ui.viewmodels.SignInViewModel

private data class SignInEvents(
    val onEmailChange: (String) -> Unit,
    val onPasswordChange: (String) -> Unit,
    val onTogglePasswordVisibility: () -> Unit,
    val onSignInWithEmail: () -> Unit,
    val onSignInWithGoogle: () -> Unit,
    val onSignUpClick: () -> Unit
)

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navSignUp: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarType by remember { mutableStateOf(SnackbarType.DEFAULT) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            snackbarType = when (event) {
                is SignInEvent.Success -> SnackbarType.SUCCESS
                is SignInEvent.Error -> SnackbarType.ERROR
            }
            val message = when (event) {
                is SignInEvent.Success -> "Login realizado com sucesso!"
                is SignInEvent.Error -> event.exception.message ?: "Erro desconhecido ao realizar login."
            }
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    SignInScreenContent(
        state = state,
        snackbar = SnackbarConfig(hostState = snackbarHostState, type = snackbarType),
        events = SignInEvents(
            onEmailChange = { viewModel.setEmail(Email(it)) },
            onPasswordChange = { viewModel.setPassword(Password(it)) },
            onTogglePasswordVisibility = viewModel::togglePassword,
            onSignInWithEmail = viewModel::signInWithEmail,
            onSignInWithGoogle = viewModel::signInWithGoogle,
            onSignUpClick = navSignUp
        )
    )
}

@Composable
private fun SignInScreenContent(
    state: SignInState,
    snackbar: SnackbarConfig,
    events: SignInEvents
) {
    DemoTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                snackbarHost = {
                    StandardSnackbar(hostState = snackbar.hostState, type = snackbar.type)
                }
            ) { paddingValues ->
                SignInForm(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    events = events
                )
            }
        }
    }
}

@Composable
private fun SignInForm(
    modifier: Modifier = Modifier,
    state: SignInState,
    events: SignInEvents
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
        SignInHeader()
        Spacer(modifier = Modifier.height(Spacing.Huge))
        SignInInputFields(state = state, events = events)
        Spacer(modifier = Modifier.height(Spacing.Large))
        SignUpLink(onClick = events.onSignUpClick)
        Spacer(modifier = Modifier.height(Spacing.XLarge))
        StandardButton(
            text = stringResource(R.string.button_login),
            onClick = events.onSignInWithEmail,
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
            text = stringResource(R.string.button_social_login),
            enabled = !state.googleLoading,
            onClick = events.onSignInWithGoogle
        )
    }
}

@Composable
private fun SignInHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Small)
    ) {
        Text(
            text = stringResource(R.string.header_sign_in),
            fontSize = FontSize.XLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.subheader_sign_in),
            fontSize = FontSize.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = Alpha.XHigh),
            textAlign = TextAlign.Center,
            lineHeight = FontSize.Large
        )
    }
}

@Composable
private fun SignInInputFields(state: SignInState, events: SignInEvents) {
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
    }
}

@Composable
private fun SignUpLink(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        StandardTextButton(text = stringResource(R.string.link_to_sign_up), onClick = onClick)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignInScreenPreview() {
    DemoTheme {
        SignInScreenContent(
            state = SignInState(),
            snackbar = SnackbarConfig(SnackbarHostState(), SnackbarType.DEFAULT),
            events = SignInEvents(
                onEmailChange = {},
                onPasswordChange = {},
                onTogglePasswordVisibility = {},
                onSignInWithEmail = {},
                onSignInWithGoogle = {},
                onSignUpClick = {}
            )
        )
    }
}