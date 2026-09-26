package com.example.firstproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firstproject.ui.screens.createaccount.CreateAccountDocsScreen
import com.example.firstproject.ui.screens.createaccount.CreateAccountInfoScreen
import com.example.firstproject.ui.screens.forgotpassword.ForgotPasswordCodeScreen
import com.example.firstproject.ui.screens.forgotpassword.ForgotPasswordEmailScreen
import com.example.firstproject.ui.screens.forgotpassword.ForgotPasswordNewScreen
import com.example.firstproject.ui.screens.login.LoginScreen
import com.example.firstproject.ui.screens.home.HomeScreen
import com.example.firstproject.ui.screens.googleauth.GoogleAccountPickerScreen

@Composable
fun FirstProjectNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD_EMAIL) },
                onCreateAccountClick = { navController.navigate(Routes.CREATE_ACCOUNT_INFO) },
                onGoogleClick = { navController.navigate(Routes.GOOGLE_ACCOUNT_PICKER) }
            )
        }

        composable(Routes.GOOGLE_ACCOUNT_PICKER) {
            GoogleAccountPickerScreen(
                onBackClick = { navController.popBackStack() },
                onAccountSelected = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD_EMAIL) {
            ForgotPasswordEmailScreen(
                onBackClick = { navController.popBackStack() },
                onCodeSent = { navController.navigate(Routes.FORGOT_PASSWORD_CODE) }
            )
        }

        composable(Routes.FORGOT_PASSWORD_CODE) {
            ForgotPasswordCodeScreen(
                onBackClick = { navController.popBackStack() },
                onCodeConfirmed = { navController.navigate(Routes.FORGOT_PASSWORD_NEW) }
            )
        }

        composable(Routes.FORGOT_PASSWORD_NEW) {
            ForgotPasswordNewScreen(
                onBackClick = { navController.popBackStack() },
                onPasswordChanged = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CREATE_ACCOUNT_INFO) {
            CreateAccountInfoScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(Routes.CREATE_ACCOUNT_DOCS) }
            )
        }

        composable(Routes.CREATE_ACCOUNT_DOCS) {
            CreateAccountDocsScreen(
                onBackClick = { navController.popBackStack() },
                onFinishClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNovaVistoriaClick = {
                    // ainda sem tela de nova vistoria — próximo passo
                }
            )
        }
    }
}