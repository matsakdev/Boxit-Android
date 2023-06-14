package com.matsak.ellicitycompose.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.matsak.ellicitycompose.screens.EllicityScreens
import com.matsak.ellicitycompose.screens.home.HomeScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = EllicityScreens.LoginScreen.name
    ) {
        authNavGraph(navController = navController)
        composable(route = EllicityScreens.HomeScreen.name) {
            HomeScreen()
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
}