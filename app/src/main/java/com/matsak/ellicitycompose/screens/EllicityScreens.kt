package com.matsak.ellicitycompose.screens

enum class EllicityScreens {
    LoginScreen,
    HomeScreen,
    SystemsScreen,
    SettingsScreen,
    StatisticsScreen;

    companion object {
        fun fromRoute(route: String?) : EllicityScreens
        = when (route?.substringBefore("/")) {
            LoginScreen.name -> LoginScreen
            HomeScreen.name -> HomeScreen
            SettingsScreen.name -> SettingsScreen
            StatisticsScreen.name -> StatisticsScreen
            SystemsScreen.name -> SystemsScreen
            null -> LoginScreen
            else -> throw java.lang.IllegalArgumentException("Route $route is not recognized")
        }
    }
}