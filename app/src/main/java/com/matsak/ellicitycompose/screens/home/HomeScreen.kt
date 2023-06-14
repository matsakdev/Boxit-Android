package com.matsak.ellicitycompose.screens.home;

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.matsak.ellicitycompose.BottomBarScreen
import com.matsak.ellicitycompose.graphs.HomeNavGraph
import com.matsak.ellicitycompose.screens.EllicityScreens
import com.matsak.ellicitycompose.screens.circuits.CircuitDetailsViewModel
import com.matsak.ellicitycompose.screens.circuits.CircuitViewModel
import com.matsak.ellicitycompose.screens.systems.SystemScreen
import com.matsak.ellicitycompose.screens.systems.SystemsScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    var screenTitle by remember {
        mutableStateOf(
            navController
                .currentDestination
                ?.route
                .toString()
        )
    }
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                HomeNavGraph(navController = navController)
            }
        },
        topBar = {
            TopBar {
                Row(horizontalArrangement = Arrangement.Start) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Arrow back",
                        modifier = Modifier.clickable {
                            stopSendingRequestsIfNeeded(navController)
                            navController.popBackStack()
                        })
                    Spacer(modifier = Modifier.width(100.dp))
                    Text(
                        text = "Ellicity",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
        }
    )
}

fun stopSendingRequestsIfNeeded(navController: NavHostController) {
    val route: String? = navController.currentDestination?.route

    if (route != null) {
        if (route == EllicityScreens.SystemsScreen.name) {
            SystemsScreenViewModel.stopSendingRequests()
        }
        if (route == SystemScreen.Circuits.name) {
            CircuitViewModel.stopSendingRequests()
        }
        if (route == SystemScreen.CircuitDetails.name) {
            CircuitDetailsViewModel.stopSendingRequests()
        }

    }

}

@ExperimentalMaterial3Api
@Composable
fun TopBar(
    title: @Composable () -> Unit,
): Unit {
    return TopAppBar(
        title = title,
        colors = TopAppBarDefaults.smallTopAppBarColors()
    )
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Systems,
        BottomBarScreen.Statistics,
        BottomBarScreen.Settings,
    )

    val allScreensWithBottomBar = listOf(
        EllicityScreens.HomeScreen,
        EllicityScreens.SystemsScreen,
        EllicityScreens.SettingsScreen,
        EllicityScreens.StatisticsScreen,
        SystemScreen.Systems,
        SystemScreen.Circuits,
        SystemScreen.CircuitDetails,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = allScreensWithBottomBar.any {
        it.name == currentDestination?.route ||
                SystemScreen
                    .values()
                    .map{element -> element.name}
                    .contains(it.name.substringBefore("/"))
    }
    if (bottomBarDestination) {
        NavigationBar() {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    screensWithBar = allScreensWithBottomBar,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController,
    screensWithBar: List<Enum<*>>
) {
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon"
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route.name ||
                    screen == BottomBarScreen.Systems &&
                    SystemScreen.values().map{ screen -> screen.name}.contains(it.route?.substringBefore("/"))
        } == true,
        colors = NavigationBarItemDefaults.colors(),
        onClick = {
            navController.navigate(screen.route.name) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}