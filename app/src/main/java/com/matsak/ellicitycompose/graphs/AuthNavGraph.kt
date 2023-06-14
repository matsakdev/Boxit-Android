package com.matsak.ellicitycompose.graphs;

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.matsak.ellicitycompose.screens.EllicityScreens
import com.matsak.ellicitycompose.screens.LoginContent
import com.matsak.ellicitycompose.screens.ScreenContent

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = EllicityScreens.LoginScreen.name,
        startDestination = AuthScreen.LogScreen.name
    ) {
        composable(route = AuthScreen.LogScreen.name) {
            LoginContent(
                navController = navController
            )
        }
        composable(route = AuthScreen.SignUpScreen.name) {
            ScreenContent(name = AuthScreen.SignUpScreen.name) {}
        }
        composable(route = AuthScreen.ForgotScreen.name) {
            ScreenContent(name = AuthScreen.ForgotScreen.name) {}
        }
    }
}

enum class AuthScreen {
    LogScreen,
    ForgotScreen,
    SignUpScreen;

    companion object {
        fun fromRoute(route: String?) : AuthScreen
                = when (route?.substringBefore("/")) {
            LogScreen.name -> LogScreen
            ForgotScreen.name -> ForgotScreen
            SignUpScreen.name -> SignUpScreen
            null -> LogScreen
            else -> throw java.lang.IllegalArgumentException("Route $route is not recognized")
        }
    }
}