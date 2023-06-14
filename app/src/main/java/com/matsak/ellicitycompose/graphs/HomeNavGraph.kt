package com.matsak.ellicitycompose.graphs;

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.matsak.ellicitycompose.BottomBarScreen
import com.matsak.ellicitycompose.screens.*
import com.matsak.ellicitycompose.screens.circuits.CircuitDetails
import com.matsak.ellicitycompose.screens.circuits.CircuitsContent
import com.matsak.ellicitycompose.screens.settings.SettingsContent
import com.matsak.ellicitycompose.screens.statistics.StatisticsContent
import com.matsak.ellicitycompose.screens.systems.SystemScreen
import com.matsak.ellicitycompose.screens.systems.SystemsContent

private var systemsArray : ArrayList<com.matsak.ellicitycompose.dto.System> = arrayListOf()

@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = EllicityScreens.HomeScreen.name,
        startDestination = EllicityScreens.SettingsScreen.name
    ) {
        composable(route = EllicityScreens.SystemsScreen.name) {
            SystemsContent(navController = navController)
        }
        composable(route = EllicityScreens.StatisticsScreen.name) {
            StatisticsContent(navController = navController)
        }
        composable(route = EllicityScreens.SettingsScreen.name) {
            SettingsContent(navController = navController)
        }
        composable(route = SystemScreen.Circuits.name + "/{system}",
        arguments = listOf(navArgument(name = "system") {type = NavType.LongType})
        ) { backStackEntry ->
            CircuitsContent(
                navController = navController,
                backStackEntry.arguments?.getLong("system")
            )
        }
        composable(
            route = SystemScreen.CircuitDetails.name + "/{circuit}",
            arguments = listOf(navArgument(name = "circuit") { type = NavType.LongType })
        ) { backStackEntry ->
            CircuitDetails(
                navController = navController,
                backStackEntry.arguments?.getLong("circuit")
            )
        }
    }
}